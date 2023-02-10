package com.flowshop.simulator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@RequiredArgsConstructor
public class Operator extends ObservableSimObject {

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

   public void setAssignedWorkstation(WorkCell cell) {
      this.assignedWorkstation = cell;
      notifySimObjectObservers();
   }

   public Status getStatus() {
      return (null == assignedWorkstation) ? Status.IDLE : Status.PROCESSING;
   }
}
