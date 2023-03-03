package com.flowshop.simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

public class WorkGroup extends Workstation implements SimObjectObserver {

   @Getter
   private final String id;
   @Getter
   private final Set<WorkCell> workCells;

   public WorkGroup(String id, Set<WorkCell> workCells) {
      this.id = id;
      this.workCells = workCells;
      for (WorkCell wc : workCells) {
         wc.addSimObjectObserver(this);
         wc.setWorkGroup(this);
      }
   }

   @Override
   public boolean assignOperation(Operation op) {
      for (WorkCell workCell : workCells) {
         if (Workstation.Status.IDLE.equals(workCell.getStatus())) {
            workCell.assignOperation(op);
            return true;
         }
      }
      return false;
   }

   static Map<Workstation.Status, Integer> statusWeights = new HashMap<>();
   static {
      statusWeights.put(Workstation.Status.IDLE, 1);
      statusWeights.put(Workstation.Status.WAITING_FOR_OPERATOR, 2);
      statusWeights.put(Workstation.Status.BLOCKED, 3);
      statusWeights.put(Workstation.Status.PROCESSING, 4);
   }

   @Override
   public Workstation.Status getStatus() {
      Workstation.Status lower = Workstation.Status.PROCESSING;
      for (WorkCell workCell : workCells) {
         if (statusWeights.get(workCell.getStatus()) < statusWeights.get(lower))
            lower = workCell.getStatus();
      }
      return lower;
   }

   public long evalProcess() {
      long minProcess = 0;
      for (Workstation wrk : this.getWorkCells()) {
         if (wrk.getStatus().equals(Workstation.Status.PROCESSING)
               && (wrk.evalProcess() < minProcess || minProcess == 0))
            minProcess = wrk.evalProcess();
      }
      return minProcess;
   }

   @Override
   public long process(long i) {
      long minProcessTime = 0;
      for (WorkCell w : workCells) {
         long eval = w.evalProcess(i);
         if (eval > 0 && (eval < minProcessTime || minProcessTime == 0))
            minProcessTime = eval;
      }
      for (WorkCell workCell : workCells) {
         workCell.process(minProcessTime);
      }
      return minProcessTime;
   }

   private int countWorkstationIdleCells(Workstation w) {
      if (w instanceof WorkCell)
         return (w.getStatus().equals(Workstation.Status.IDLE)) ? 1 : 0;
      else if (w instanceof WorkGroup) {
         int count = 0;
         Iterator<WorkCell> iterator = ((WorkGroup) w).getWorkCells().iterator();
         while (iterator.hasNext()) {
            if (iterator.next().getStatus().equals(Workstation.Status.IDLE))
               count++;
         }
         return count;
      }
      // should never get here
      return 0;
   }

   @Override
   public boolean evalBlockedStatus() {
      boolean evalBlocked = false;
      // Set<Workstation> nextWorkstation = new HashSet<>();
      Map<Workstation, Integer> nextWorkstationsCount = new HashMap<>();
      for (WorkCell cell : workCells) {
         if (!cell.evalBlockedStatus()) {
            // in workgroups we have to consider another blocking condition: when more than
            // one cell within the same workgroup ends at the same moment (the cell status
            // will be set to IDLE) and both have the same next
            // operation. In this case we have to consider how many idle workcell will be
            // available on the next workstation
            if (cell.getStatus().equals(Workstation.Status.IDLE) && cell.getLatestOperation() != null
                  && cell.getLatestOperation().getNextOperation() != null) {
               Workstation nextWorkstation = cell.getLatestOperation().getNextOperation().getRequiredWorkstation();
               int nextWorkstationIdleCells = countWorkstationIdleCells(
                     nextWorkstation);
               if (nextWorkstationsCount.keySet().contains(nextWorkstation)) {
                  // if work group cells exceed the number of idle cells in the next workstation
                  if (nextWorkstationsCount.get(nextWorkstation) >= nextWorkstationIdleCells) {
                     cell.forceBlocked();
                     evalBlocked = true;
                  }
                  nextWorkstationsCount.put(nextWorkstation, nextWorkstationsCount.get(nextWorkstation) + 1);
                  // the cell can be considered idle as there are still ilde cells in the next
                  // workstation as counted through nextWorkstationIdleCell
               } else {
                  nextWorkstationsCount.put(nextWorkstation, 1);
               }
            }

         } else {
            evalBlocked = true;
         }
      }
      return evalBlocked;
   }

   @Override
   public Set<Operator> assignOperators(Collection<? extends Operator> operators) {
      Set<Operator> assignedSet = new HashSet<>();
      Set<Operator> remainSet = new HashSet<>(operators);
      for (WorkCell workCell : getWorkCells()) {
         Set<Operator> cellAssignedSet = workCell.assignOperators(remainSet);
         remainSet.removeAll(cellAssignedSet);
         assignedSet.addAll(cellAssignedSet);
      }
      return assignedSet;
   }

   @Override
   public Set<Operator> unassignOperators() {
      Set<Operator> unassignedSet = new HashSet<>();
      for (WorkCell workCell : getWorkCells()) {
         unassignedSet.addAll(workCell.unassignOperators());
      }
      return unassignedSet;
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      notifySimObjectObservers(observableSimObject);
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
   }

   @Override
   public void onEndSimulation() {
      // TODO Auto-generated method stub

   }

}
