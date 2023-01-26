package com.flowshop;

import lombok.Data;
import lombok.ToString;

@Data
public class Operation {
   public enum Status {
      TODO, PROGRESS, DONE
   };

   private final String id;
   private int requiredOperators;
   private final long cycleTime;
   private long processedTime = 0;
   private final Workstation requiredWorkstation;
   @ToString.Exclude
   private final Operation nextOperation;

   public Operation(String id, long cycleTime, Workstation requiredWorkstation, Operation nextOperation,
         int requiredOperators) {
      this(id, cycleTime, requiredWorkstation, nextOperation);
      this.requiredOperators = requiredOperators;
   }

   public Operation(String id, long cycleTime, Workstation requiredWorkstation, Operation nextOperation) {
      this.id = id;
      this.cycleTime = cycleTime;
      this.requiredWorkstation = requiredWorkstation;
      this.nextOperation = nextOperation;
   }

   public Status getStatus() {
      if (processedTime == 0)
         return Status.TODO;
      else if (processedTime < cycleTime)
         return Status.PROGRESS;
      else
         return Status.DONE;
   }
}
