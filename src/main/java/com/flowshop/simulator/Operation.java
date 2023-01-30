package com.flowshop.simulator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Operation extends ObservableSimObject {
   public enum Status {
      TODO, PROGRESS, DONE, BLOCKED
   };

   @Getter
   private final String id;
   @Getter
   @Setter
   private int requiredOperators;
   @Getter
   private final long cycleTime;
   @Getter
   private long processedTime = -1;
   @Getter
   private final Workstation requiredWorkstation;
   @ToString.Exclude
   @Getter
   private final Operation nextOperation;
   @Getter
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
      notifySimObjectObservers();
   }

   public void setProcessedTime(long processedTime) {
      boolean notify = false;
      if (processedTime > this.processedTime)
         notify = true;
      this.processedTime = processedTime;
      if (notify)
         notifySimObjectObservers();
   }

   public void setBlocked(boolean blocked) {
      boolean notify = false;
      if (blocked != this.blocked)
         notify = true;
      this.blocked = blocked;
      if (notify)
         notifySimObjectObservers();
   }

}
