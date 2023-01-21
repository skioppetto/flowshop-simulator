package com.flowshop;

import java.util.Set;

import lombok.Data;
import lombok.ToString;

@Data
public class Workstation {

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING
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
      return Status.PROCESSING;
   }

}
