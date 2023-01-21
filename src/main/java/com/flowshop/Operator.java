package com.flowshop;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Operator {

   public enum Status {
      IDLE, PROCESSING
   };

   @EqualsAndHashCode.Include
   private final String id;
   
   private Workstation assignedWorkstation;

   public Status getStatus() {
      return (null == assignedWorkstation) ? Status.IDLE : Status.PROCESSING;
   }
}
