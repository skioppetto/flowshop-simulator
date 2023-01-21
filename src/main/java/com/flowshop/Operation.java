package com.flowshop;

import lombok.Data;

@Data
public class Operation {
   public enum Status {
      TODO, PROGRESS, DONE
   };

   private final String id;
   private int requiredOperators;
   private final long cycleTime;
   private long processedTime = 0;

   public Status getStatus() {
      if (processedTime == 0)
         return Status.TODO;
      else if (processedTime < cycleTime)
         return Status.PROGRESS;
      else
         return Status.DONE;
   }
}
