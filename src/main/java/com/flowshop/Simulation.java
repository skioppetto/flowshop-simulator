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
      Operation firstOperation = orders.get(0).getOperations().get(0);
      Workstation workstation = firstOperation.getRequiredWorkstation();
      workstation.setCurrentOperation(firstOperation);
      workstation.process(1);
   }

   public void process(int i) {
      // process all workstations
      workstations.forEach(workstation -> workstation.process(i));

      for (Order order : orders) {
         Operation op = order.getNextOperation();
         if (null != op && null == op.getRequiredWorkstation().getCurrentOperation()) {
            op.getRequiredWorkstation().setCurrentOperation(op);
         }
      }
   }

}
