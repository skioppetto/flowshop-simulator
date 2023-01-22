package com.flowshop;

import java.util.List;

import lombok.Data;

@Data
public class Order {

   public enum Status {
      TODO, PROGRESS, DONE
   }

   private final List<Operation> operations;

   public Operation getNextOperation() {
      Status orderStatus = getStatus();
      if (orderStatus.equals(Status.TODO))
         return operations.get(0);
      else if (orderStatus.equals(Status.DONE))
         return null;
      else {
         for (Operation operation : operations) {
            if (Operation.Status.TODO.equals(operation.getStatus()))
               return operation;
            else if (Operation.Status.PROGRESS.equals(operation.getStatus()))
               return null;
         }
      }
      // this should never happen as it means that the status order is DONE
      return null;
   }

   public Status getStatus() {
      if (operations.get(0).getStatus().equals(Operation.Status.TODO))
         return Status.TODO;
      else if (operations.get(operations.size() - 1).getStatus().equals(Operation.Status.DONE))
         return Status.DONE;
      else
         return Status.PROGRESS;
   }
}
