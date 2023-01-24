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
   private final Set<WorkCell> workstations = new HashSet<>();
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
         WorkCell workstation = firstOperation.getRequiredWorkstation();
         // TODO: what happen if the workstation is in reality is a workgroup?
         // currentOperation should be bind to each workstation within the group
         if (workstation.assignOperation(firstOperation)) {
            assignOperators(workstation);
         }
      }
   }

   public void process(int i) {
      Collection<WorkCell.Status> blockedOrIdleStatus = Arrays.asList(WorkCell.Status.BLOCKED,
            WorkCell.Status.IDLE);
      // release operators for idle and blocked workstations
      workstations.stream().parallel()
            .filter(workstation -> blockedOrIdleStatus
                  .contains(workstation.getStatus()))
            .forEach(this::unassignOperators);
      // operators assigment priority to WAIT_FOR_OPERATOR workstations
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
   private void assignOperators(WorkCell workstation) {
      Operation assignedOperation = workstation.getCurrentOperation();
      while (!this.availableOperators.isEmpty() && !workstation.getStatus().equals(WorkCell.Status.IDLE)
            && workstation.getAssignedOperators() < assignedOperation.getRequiredOperators()) {
         Operator op = this.availableOperators.iterator().next();
         workstation.assignOperators(op);
         this.assignedOperators.add(op);
         this.availableOperators.remove(op);
      }
   }

   // this method will work with single workstations
   private void unassignOperators(WorkCell workstation) {
      this.availableOperators.addAll(workstation.unassignOperators());
   }

}
