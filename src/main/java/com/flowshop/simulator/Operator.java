package com.flowshop.simulator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Operator {

   public enum Status {
      IDLE, PROCESSING
   };

   @EqualsAndHashCode.Include
   private final String id;

   private final String group;

   private WorkCell assignedWorkstation;

   public Operator(String id) {
      this.id = id;
      this.group = null;
   }

   public Status getStatus() {
      return (null == assignedWorkstation) ? Status.IDLE : Status.PROCESSING;
   }
}
