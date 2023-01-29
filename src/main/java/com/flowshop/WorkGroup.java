package com.flowshop;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.flowshop.WorkCell.Status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkGroup implements Workstation {

   @Getter
   private final String id;
   @Getter
   private final Set<WorkCell> workCells;

   @Override
   public boolean assignOperation(Operation op) {
      for (WorkCell workCell : workCells) {
         if (WorkCell.Status.IDLE.equals(workCell.getStatus())) {
            workCell.assignOperation(op);
            return true;
         }
      }
      return false;
   }

   static Map<WorkCell.Status, Integer> statusWeights = new HashMap<>();
   static {
      statusWeights.put(Status.IDLE, 1);
      statusWeights.put(Status.WAITING_FOR_OPERATOR, 2);
      statusWeights.put(Status.BLOCKED, 3);
      statusWeights.put(Status.PROCESSING, 4);
   }

   @Override
   public WorkCell.Status getStatus() {
      Status lower = Status.PROCESSING;
      for (WorkCell workCell : workCells) {
         if (statusWeights.get(workCell.getStatus()) < statusWeights.get(lower))
            lower = workCell.getStatus();
      }
      return lower;
   }

   @Override
   public long process(long i) {
      long minProcessTime = workCells.stream().mapToLong(workCell -> workCell.evalProcess(i)).min().orElse(0);
      for (WorkCell workCell : workCells) {
         workCell.process(minProcessTime);
      }
      return minProcessTime;
   }

   private int countWorkstationIdleCells(Workstation w) {
      if (w instanceof WorkCell)
         return (w.getStatus().equals(WorkCell.Status.IDLE)) ? 1 : 0;
      else if (w instanceof WorkGroup) {
         int count = 0;
         Iterator<WorkCell> iterator = ((WorkGroup) w).getWorkCells().iterator();
         while (iterator.hasNext()) {
            if (iterator.next().getStatus().equals(WorkCell.Status.IDLE))
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
            if (cell.getStatus().equals(WorkCell.Status.IDLE) && cell.getLatestOperation() != null
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
   public int getAssignedOperators() {
      return workCells.stream().mapToInt(workCell -> workCell.getAssignedOperators()).sum();
   }

   @Override
   public int getRequiredOperators() {
      return workCells.stream().mapToInt(workCell -> workCell.getRequiredOperators()).sum();
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
}
