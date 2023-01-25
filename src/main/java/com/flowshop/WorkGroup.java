package com.flowshop;

import java.util.List;
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
   public Object getStatus() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public long process(long i) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public boolean evalBlockedStatus() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public int getAssignedOperators() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public int getRequiredOperators() {
      return workCells.stream()
            .filter(workCell -> workCell.getStatus().equals(WorkCell.Status.WAITING_FOR_OPERATOR))
            .mapToInt(workCell -> (workCell.getRequiredOperators() - workCell.getAssignedOperators())).sum();
   }

   @Override
   // TODO: I should return a set with all the really assigned operators as some of
   // them could remain available.
   public void assignOperators(Operator... op) {
      int opCount = 0;
      List<WorkCell> cellsWaiting = workCells.stream()
            .filter(workCell -> workCell.getStatus().equals(WorkCell.Status.WAITING_FOR_OPERATOR)).toList();
      for (WorkCell workCell : cellsWaiting) {
         while (opCount < op.length && workCell.getAssignedOperators() < workCell.getRequiredOperators()) {
            workCell.assignOperators(op[opCount++]);
         }
      }
   }

   @Override
   public Set<Operator> unassignOperators() {
      // TODO Auto-generated method stub
      return null;
   }

   private void assignOperators(WorkCell workcell) {
   }
}
