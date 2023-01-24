package com.flowshop;

import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class WorkCell {

   @EqualsAndHashCode.Include
   @Getter
   private final String id;

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING, BLOCKED
   };

   @ToString.Exclude
   @Getter
   private Operation currentOperation;

   @ToString.Exclude
   @Getter
   private Set<Operator> assignedOperators = new WorkstationOperatorsSet(this);

   @ToString.Exclude
   @Getter(value = AccessLevel.PACKAGE)
   private Operation latestOperation;

   public Integer getRequiredOperators() {
      return (null == currentOperation) ? 0 : currentOperation.getRequiredOperators();
   }

   public Status getStatus() {
      if (null == currentOperation)
         return Status.IDLE;
      if (assignedOperators.size() < currentOperation.getRequiredOperators())
         return Status.WAITING_FOR_OPERATOR;
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime())
         return Status.BLOCKED;
      return Status.PROCESSING;
   }

   public long process(long i) {
      if (this.getStatus() != Status.PROCESSING
            && this.getStatus() != Status.BLOCKED)
         return 0l;
      long processTime = Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
      currentOperation.setProcessedTime(processTime + currentOperation.getProcessedTime());
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime()) {
         this.latestOperation = currentOperation;
         this.currentOperation = null;
      }
      return processTime;
   }

   // this method should be called after all workstations are processed
   public boolean evalBlockedStatus() {
      boolean isBlocked = wasBlocked();
      if (isBlocked)
         this.currentOperation = this.latestOperation;
      return isBlocked;
   }

   private boolean wasBlocked() {
      return latestOperation != null && latestOperation.getNextOperation() != null
            && !latestOperation.getNextOperation().getRequiredWorkstation().getStatus()
                  .equals(WorkCell.Status.IDLE);
   }

   public boolean assignOperation(Operation op) {
      if (this.currentOperation == null) {
         this.currentOperation = op;
         return true;
      }
      return false;
   }

   public boolean unassignOperators() {
      if (this.getStatus().equals(Status.PROCESSING) || this.getAssignedOperators().isEmpty())
         return false;
      this.getAssignedOperators().clear();
      return true;

   }

}
