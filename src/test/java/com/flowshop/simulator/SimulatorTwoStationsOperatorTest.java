package com.flowshop.simulator;

import static com.flowshop.SimulatorTestUtils.buildOrder;
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
      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(ord1.getOperations().get(0), workstations[0].getCurrentOperation());
      
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

      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());
      
      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
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

      assertEquals(Workstation.Status.IDLE, workstations[0].getStatus());
      
      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(ord2.getOperations().get(1), workstations[1].getCurrentOperation());

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

      
      assertEquals(Workstation.Status.IDLE, workstations[1].getStatus());
      assertEquals(Workstation.Status.IDLE, workstations[0].getStatus());
   }


}
