package com.flowshop.simulator;

import java.util.List;

import lombok.Data;

@Data
public class Order {

   public enum Status {
      TODO, PROGRESS, DONE
   }

   private final String id;
   private final List<Operation> operations;
   private int nextOperationIdx = 0;

   public Order(String id, List<Operation> operations) {
      this.id = id;
      this.operations = operations;
      for (Operation op : operations)
         op.setOrder(this);
   }

   public Operation getNextOperation() {
      Status orderStatus = getStatus();
      if (orderStatus.equals(Status.DONE)) {
         return null;
      } else {
         Operation.Status nextOperationStatus = operations.get(this.nextOperationIdx).getStatus();
         if (nextOperationStatus.equals(Operation.Status.PROGRESS))
            return null;
         else if (nextOperationStatus.equals(Operation.Status.DONE))
            this.nextOperationIdx++;
      }
      return operations.get(nextOperationIdx);
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
