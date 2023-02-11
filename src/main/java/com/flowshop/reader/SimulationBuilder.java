package com.flowshop.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;

public class SimulationBuilder {

   private final IConfigurationReader configuration;
   private final Map<String, Workstation> workstationsMap = new HashMap<>();
   private final List<Operator> operators = new ArrayList<>();
   private final List<Order> orders = new ArrayList<>();

   public SimulationBuilder(IConfigurationReader configurationReader) {
      this.configuration = configurationReader;
   }

   public Simulation build() {
      buildOperators();
      buildWorkstationMap();
      buildOrders();
      return new Simulation(orders, operators);
   }

   // TODO: check mandatory values
   // TODO: check unique identifier for order
   private void buildOrders() {
      OrderRequirements[] orderRequirements = configuration.getOrdersRequirements();
      for (OrderRequirements requirement : orderRequirements) {
         List<Operation> operations = new ArrayList<>();
         Operation nextOperation = null;
         for (int i = requirement.getOperations().length - 1; i >= 0; i--) {
            OperationRequrements opRequirement = requirement.getOperations()[i];
            Workstation workstation = workstationsMap.getOrDefault(opRequirement.getWorkstation(),
                  new WorkCell(opRequirement.getWorkstation()));
            Operation op = new Operation(opRequirement.getOperationId(), opRequirement.getCycleTime(), workstation,
                  nextOperation);
            operations.add(op);
            nextOperation = op;

         }
         orders.add(new Order(requirement.getOrderId(), operations));
      }
   }

   // TODO: check unique identifiers for operators
   // TODO: check name is not null
   private void buildOperators() {
      OperatorRequirements[] operatorRequirements = configuration.getOperatorRequirements();
      if (operatorRequirements != null) {
         for (OperatorRequirements requirement : operatorRequirements) {
            operators.add(new Operator(requirement.getName(), requirement.getGroup()));
         }
      }
   }

   private void buildWorkstationMap() {
      WorkstationRequirements[] workstationRequirements = configuration.getWorkstationsRequirements();
      if (workstationRequirements != null) {
         for (WorkstationRequirements requirement : workstationRequirements) {
            workstationsMap.put(requirement.getName(), buildWorkstation(requirement));
         }
      }
   }

   private Workstation buildWorkstation(WorkstationRequirements requirement) {
      Workstation workstation;
      if (requirement.getWorkGroupCells() > 1) {
         Set<WorkCell> workcells = new HashSet<>();
         for (int i = 0; i < requirement.getWorkGroupCells(); i++)
            workcells.add(new WorkCell(requirement.getName() + String.format(".%02d", i + 1)));
         workstation = new WorkGroup(requirement.getName(), workcells);
      } else {
         workstation = new WorkCell(requirement.getName());
      }
      if (requirement.getBufferAfter() > 0 || requirement.getBufferBefore() > 0) {
         return new BufferedWorkstation(workstation, requirement.getBufferAfter(), requirement.getBufferBefore());
      } else {
         return workstation;
      }

   }

}
