package com.flowshop.simulator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.flowshop.writer.AbstractEventsWriter;

import lombok.Getter;

public class Simulation implements ISimulationTimer {

   @Getter
   private final List<Order> orders;
   @Getter
   private final Set<Workstation> workstations = new HashSet<>();
   @Getter
   private final Set<Operator> availableOperators = new HashSet<>();
   private final Set<Operator> operators = new HashSet<>();

   private final Set<AbstractEventsWriter> eventsWriters = new HashSet<>();

   private final Set<Thread> writingThreads = new HashSet<>();

   private long simulationTime = 0;

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

   public void addEventsWriter(AbstractEventsWriter eventsWriter) {
      this.eventsWriters.add(eventsWriter);
   }

   public void start() {
      startWritingThreads();
      for (Order order : orders) {
         Operation firstOperation = order.getOperations().get(0);
         Workstation workstation = firstOperation.getRequiredWorkstation();
         if (workstation.assignOperation(firstOperation)) {
            assignOperators(workstation);
         }
      }
   }

   public void stop(){
      stopWritingThreads();   
   }

   private void startWritingThreads() {
      for (AbstractEventsWriter eWriter : eventsWriters) {
         Thread writerThread = new Thread(eWriter);
         writerThread.start();
         writingThreads.add(writerThread);
      }
   }

   private void stopWritingThreads() {
      for (AbstractEventsWriter eWriter : eventsWriters) {
         eWriter.setStopped(true);
      }
      for (Thread writingThread : writingThreads) {
         try {
            writingThread.join(10);
            writingThread.interrupt();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   public void process(int i) {
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

}
