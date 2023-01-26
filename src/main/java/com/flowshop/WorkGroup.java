package com.flowshop;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

   @Override
   public boolean evalBlockedStatus() {
      return workCells.stream().map(workCell -> workCell.evalBlockedStatus()).reduce(false, Boolean::logicalOr);
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
