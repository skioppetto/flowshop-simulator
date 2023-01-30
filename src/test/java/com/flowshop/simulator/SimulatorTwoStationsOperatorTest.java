package com.flowshop.simulator;

import static com.flowshop.simulator.SimulatorTestUtils.buildOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorTwoStationsOperatorTest {
   @Test
   // in this test I'll try a simulation with an operator shared between two
   // stations
   void twoStationsOperatorStartSimulation() {
      WorkCell[] workstations = { new WorkCell("wrl1"), new WorkCell("wrk2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      ord1.getOperations().get(0).setRequiredOperators(1);
      ord1.getOperations().get(1).setRequiredOperators(1);
      Operator[] operators = { new Operator("oper1") };
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2), Arrays.asList(operators));
      sim.start();

      // I expect that the first operator will be moved to the first workstation where
      // the first operation of the first order will be processed
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(ord1.getOperations().get(0), workstations[0].getCurrentOperation());
      assertEquals(1, workstations[0].getAssignedOperators());

   }

   @Test
   void twoStationsOperatorProcess1Simulation() {
      WorkCell[] workstations = { new WorkCell("wrl1"), new WorkCell("wrk2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      ord1.getOperations().get(0).setRequiredOperators(1);
      ord1.getOperations().get(1).setRequiredOperators(1);
      Operator[] operators = { new Operator("oper1") };
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2), Arrays.asList(operators));
      sim.start();
      sim.process(1);

      // Now the first operation has ended and the operator should be released
      // the operator now can be assigned to the second workstation to process the
      // second operation of the first order or
      // assigned to the first workstation to process the first operation of the
      // second order? who knows?
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());
      assertEquals(1, workstations[0].getAssignedOperators());

      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, workstations[1].getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());
   }

   @Test
   void twoStationsOperatorProcess2Simulation() {
      WorkCell[] workstations = { new WorkCell("wrl1"), new WorkCell("wrk2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      ord1.getOperations().get(0).setRequiredOperators(1);
      ord1.getOperations().get(1).setRequiredOperators(1);
      Operator[] operators = { new Operator("oper1") };
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2), Arrays.asList(operators));
      sim.start();
      sim.process(1);
      sim.process(1);

      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());
      assertEquals(1, workstations[0].getAssignedOperators());

      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, workstations[1].getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

   }

   @Test
   void twoStationsOperatorProcess3Simulation() {
      WorkCell[] workstations = { new WorkCell("wrl1"), new WorkCell("wrk2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      ord1.getOperations().get(0).setRequiredOperators(1);
      ord1.getOperations().get(1).setRequiredOperators(1);
      Operator[] operators = { new Operator("oper1") };
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2), Arrays.asList(operators));
      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);

      
      // the operation that was waiting for the operator receive the operator at the
      // beginning of this unit and process it
      assertEquals(1l, ord1.getOperations().get(1).getProcessedTime());
      // now the current operation is the one that was blocked
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(ord2.getOperations().get(1), workstations[1].getCurrentOperation());
      assertEquals(1, workstations[1].getAssignedOperators());

      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
   }

   @Test
   void twoStationsOperatorProcess4Simulation() {
      WorkCell[] workstations = { new WorkCell("wrl1"), new WorkCell("wrk2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      ord1.getOperations().get(0).setRequiredOperators(1);
      ord1.getOperations().get(1).setRequiredOperators(1);
      Operator[] operators = { new Operator("oper1") };
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2), Arrays.asList(operators));
      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);

      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());

   }

}
