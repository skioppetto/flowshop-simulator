package com.flowshop.simulator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Simulation {

   @Getter
   private final List<Order> orders;
   @Getter
   private final Set<Workstation> workstations = new HashSet<>();
   @Getter
   private final Set<Operator> availableOperators = new HashSet<>();
   
   public Simulation(List<Order> orders, Collection<? extends Operator> operators) {
      this(orders);
      this.availableOperators.addAll(operators);
   }

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
         if (workstation.assignOperation(firstOperation)) {
            assignOperators(workstation);
         }
      }
   }

   public void process(int i) {
      // all operators that are not working are released from the workstation
      workstations.stream().parallel()
            .forEach(this::unassignOperators);
      // operators assigment priority to WAIT_FOR_OPERATOR workstation
      if (!this.availableOperators.isEmpty())
         workstations.stream().parallel()
               .filter(workstation -> WorkCell.Status.WAITING_FOR_OPERATOR
                     .equals(workstation.getStatus()))
               .forEach(this::assignOperators);
      // process all workstations
      workstations.stream().parallel().forEach(workstation -> workstation.process(i));
      workstations.stream().parallel().forEach(workstation -> workstation.evalBlockedStatus());
      // try to push new operations
      for (Order order : orders) {
         Operation op = order.getNextOperation();
         if (null != op && op.getRequiredWorkstation().assignOperation(op)) {
            assignOperators(op.getRequiredWorkstation());
         }
      }
   }

   // this method will work with single workstations
   private void assignOperators(Workstation workstation) {
      this.availableOperators.removeAll(workstation.assignOperators(availableOperators));
   }

   // this method will work with single workstations
   private void unassignOperators(Workstation workstation) {
      this.availableOperators.addAll(workstation.unassignOperators());
   }

}
