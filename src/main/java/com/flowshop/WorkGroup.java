package com.flowshop;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

   @Override
   public WorkCell.Status getStatus() {
      for (WorkCell workCell : workCells) {
         if (!workCell.getStatus().equals(WorkCell.Status.PROCESSING))
            return workCell.getStatus();
      }
      return WorkCell.Status.PROCESSING;
   }

   @Override
   public long process(long i) {
      long minProcessTime = 0;
      for (WorkCell workCell : workCells) {
         long processTime = workCell.evalProcess(i);
         if (minProcessTime == 0 || processTime < minProcessTime)
            minProcessTime = processTime;
      }
      for (WorkCell workCell : workCells) {
         workCell.process(minProcessTime);
      }
      return minProcessTime;
   }

   @Override
   public boolean evalBlockedStatus() {
      for (WorkCell workCell : workCells) {
         if (workCell.evalBlockedStatus())
            return true;
      }
      return false;
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
