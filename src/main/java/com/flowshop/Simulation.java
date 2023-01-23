package com.flowshop;

import java.util.Arrays;
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
   private final Set<Operator> assignedOperators = new HashSet<>();

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
         if (workstation.getCurrentOperation() == null) {
            workstation.setCurrentOperation(firstOperation);
            assignOperators(workstation);
         }
      }
   }

   public void process(int i) {
      Collection<Workstation.Status> blockedOrIdleStatus = Arrays.asList(Workstation.Status.BLOCKED,
            Workstation.Status.IDLE);
      // release operators for idle and blocked workstations
      workstations.stream()
            .filter(workstation -> blockedOrIdleStatus
                  .contains(workstation.getStatus()))
            .forEach(this::unassignOperators);
      // operators assigment priority to WAIT_FOR_OPERATOR workstations
      if (!this.availableOperators.isEmpty())
         workstations.stream()
               .filter(workstation -> Workstation.Status.WAITING_FOR_OPERATOR
                     .equals(workstation.getStatus()))
               .forEach(this::assignOperators);
      // process all workstations
      workstations.stream().parallel().forEach(workstation -> workstation.process(i));
      workstations.stream().parallel().forEach(workstation -> workstation.evalBlockedStatus());
      // try to push new operations
      for (Order order : orders) {
         Operation op = order.getNextOperation();
         if (null != op && null == op.getRequiredWorkstation().getCurrentOperation()) {
            op.getRequiredWorkstation().setCurrentOperation(op);
            assignOperators(op.getRequiredWorkstation());
         }
      }
   }

   private void assignOperators(Workstation workstation) {
      while (!this.availableOperators.isEmpty() && workstation.getCurrentOperation() != null
            && workstation.getAssignedOperators().size() < workstation.getCurrentOperation().getRequiredOperators()) {
         Operator op = this.availableOperators.iterator().next();
         workstation.assignedOperators.add(op);
         this.assignedOperators.add(op);
         this.availableOperators.remove(op);
      }
   }

   private void unassignOperators(Workstation workstation) {
      this.availableOperators.addAll(workstation.getAssignedOperators());
      workstation.getAssignedOperators().clear();
   }

}
