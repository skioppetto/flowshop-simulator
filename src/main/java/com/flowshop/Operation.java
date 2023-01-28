package com.flowshop;

import lombok.Data;
import lombok.ToString;

@Data
public class Operation {
   public enum Status {
      TODO, PROGRESS, DONE, BLOCKED
   };

   private final String id;
   private int requiredOperators;
   private final long cycleTime;
   private long processedTime = -1;
   private final Workstation requiredWorkstation;
   @ToString.Exclude
   private final Operation nextOperation;
   private boolean blocked;

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
      if (processedTime == -1)
         return Status.TODO;
      else if (processedTime >= 0 && processedTime < cycleTime)
         return Status.PROGRESS;
      else if (!isBlocked())
         return Status.DONE;
      else
         return Status.BLOCKED;
   }

   public void start() {
      this.processedTime = 0l;
   }
}
