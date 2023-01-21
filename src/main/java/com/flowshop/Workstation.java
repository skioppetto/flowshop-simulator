package com.flowshop;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class Workstation {

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING
   };

   Operation currentOperation;
   Set<Operator> assignedOperators = new HashSet<>();

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
