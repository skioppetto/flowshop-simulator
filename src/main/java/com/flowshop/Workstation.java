package com.flowshop;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Workstation {

   @EqualsAndHashCode.Include
   private final String id;

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING, BLOCKED
   };

   Operation currentOperation;

   @ToString.Exclude
   Set<Operator> assignedOperators = new WorkstationOperatorsSet(this);

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
            || currentOperation.getProcessedTime() >= currentOperation.getCycleTime())
         return 0l;
      long processTime = Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
      currentOperation.setProcessedTime(processTime + currentOperation.getProcessedTime());
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime() && !isBlocked())
         this.currentOperation = null;
      return processTime;
   }

   private boolean isBlocked() {
      return currentOperation.getNextOperation() != null
            && !currentOperation.getNextOperation().getRequiredWorkstation().getStatus()
                  .equals(Workstation.Status.IDLE);
   }

}
