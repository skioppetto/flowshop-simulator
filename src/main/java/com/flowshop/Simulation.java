package com.flowshop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Simulation {

   private final List<Order> orders;
   private final Set<Workstation> workstations = new HashSet<>();

   public Simulation(List<Order> orders) {
      this.orders = orders;
      for (Order order : orders) {
         for (Operation operation : order.getOperations()) {
            workstations.add(operation.getRequiredWorkstation());
         }
      }
   }

   public void start() {
      for (Order order : orders) {
         Operation firstOperation = order.getOperations().get(0);
         Workstation workstation = firstOperation.getRequiredWorkstation();
         if (workstation.getCurrentOperation() == null)
            workstation.setCurrentOperation(firstOperation);
      }
   }

   public void process(int i) {
      // process all workstations
      workstations.forEach(workstation -> workstation.process(i));
      workstations.forEach(workstation -> workstation.evalBlockedStatus());

      for (Order order : orders) {
         Operation op = order.getNextOperation();
         if (null != op && null == op.getRequiredWorkstation().getCurrentOperation()) {
            op.getRequiredWorkstation().setCurrentOperation(op);
         }
      }
   }

}
