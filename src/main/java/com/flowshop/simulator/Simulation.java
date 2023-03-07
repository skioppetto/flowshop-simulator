package com.flowshop.simulator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.Getter;

public class Simulation implements ISimulationTimer {

   @Getter
   private final List<Order> orders;
   @Getter
   private final Set<Workstation> workstations = new HashSet<>();
   @Getter
   private final Set<Operator> availableOperators = new HashSet<>();
   private final Set<Operator> operators = new HashSet<>();

   private long simulationTime = 0;

   private Set<SimObserver> simulationObservers = new HashSet<>();

   public Simulation(List<Order> orders, Collection<? extends Operator> operators) {
      this(orders);
      this.availableOperators.addAll(operators);
      this.operators.addAll(operators);
   }

   public Simulation(List<Order> orders) {
      this.orders = orders;
      for (Order order : orders) {
         for (Operation operation : order.getOperations()) {
            workstations.add(operation.getRequiredWorkstation());
         }
      }
   }

   public Set<Operator> getOperators() {
      return operators;
   }

   public void start() {
      notifySimulationObservers(SimObserver::onStartSimulation);
   }

   public void stop() {
      notifySimulationObservers(SimObserver::onStopSimulation);
   }

   public void process(int i) {
      notifySimulationObservers(SimObserver::onProcessStart);
      simulationTime += i;
      // process all workstations
      workstations.stream().parallel().forEach(workstation -> workstation.process(i));
      workstations.stream().parallel().forEach(workstation -> workstation.evalBlockedStatus());
      // all operators that are not working are released from the workstation
      workstations.stream().parallel()
            .forEach(this::unassignOperators);
      // operators assigment priority to WAIT_FOR_OPERATOR workstation
      if (!this.availableOperators.isEmpty())
         workstations.stream()
               .filter(workstation -> Workstation.Status.WAITING_FOR_OPERATOR
                     .equals(workstation.getStatus()))
               .forEach(this::assignOperators);
      // try to push new operations
      for (Order order : orders) {
         Operation op = order.getNextOperation();
         if (null != op && op.getRequiredWorkstation().assignOperation(op)) {
            assignOperators(op.getRequiredWorkstation());
         }
      }
      notifySimulationObservers(SimObserver::onProcessEnd);
   }

   // this method will work with single workstations
   private void assignOperators(Workstation workstation) {
      Set<Operator> assignedOperators = workstation.assignOperators(availableOperators);
      this.availableOperators.removeAll(assignedOperators);
   }

   // this method will work with single workstations
   private void unassignOperators(Workstation workstation) {
      Set<Operator> unassignedOperators = workstation.unassignOperators();
      this.availableOperators.addAll(unassignedOperators);
   }

   @Override
   public long getSimulationTime() {
      return simulationTime;
   }

   public void addSimulationObserver(SimObserver observer) {
      this.simulationObservers.add(observer);
   }

   private void notifySimulationObservers(BiConsumer<SimObserver, Simulation> method){
      for (SimObserver observer : simulationObservers) {
         method.accept(observer, this);         
      }
   }

}
