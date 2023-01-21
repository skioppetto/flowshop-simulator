package com.flowshop;

import lombok.Data;

@Data
public class Operator {

   public enum Status {
      IDLE, PROCESSING
   };

   private final String id;
   private Workstation assignedWorkstation;

   public Status getStatus() {
      return (null == assignedWorkstation) ? Status.IDLE : Status.PROCESSING;
   }
}
