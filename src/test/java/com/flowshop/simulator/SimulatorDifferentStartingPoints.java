package com.flowshop.simulator;

import static com.flowshop.simulator.SimulatorTestUtils.buildOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorDifferentStartingPoints {
   // a test to check how the system works when orders have different starting
   // points wothin the line.

   @Test
   void differentStatingPointsStartSimulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2"),
            new WorkCell("wst3") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1, 1 });
      Order ord2 = buildOrder("ord2", new WorkCell[] { workstations[1], workstations[2] }, new long[] { 3, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2));
      sim.start();
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(ord1.getOperations().get(0), workstations[0].getCurrentOperation());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[1].getCurrentOperation());
      assertEquals(WorkCell.Status.IDLE, workstations[2].getStatus());
   }

   @Test
   void differentStatingPointsProcess1Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2"),
            new WorkCell("wst3") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1, 1 });
      Order ord2 = buildOrder("ord2", new WorkCell[] { workstations[1], workstations[2] }, new long[] { 3, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2));
      sim.start();
      sim.process(1);

      // first station is blocked as the next station is still processing
      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(ord1.getOperations().get(0), workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[1].getCurrentOperation());

      assertEquals(WorkCell.Status.IDLE, workstations[2].getStatus());
   }

   @Test
   void differentStatingPointsProcess2Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2"),
            new WorkCell("wst3") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1, 1 });
      Order ord2 = buildOrder("ord2", new WorkCell[] { workstations[1], workstations[2] }, new long[] { 3, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2));
      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);

      // first station is no more blocked as second station has finished, will be idle
      // as there are no other orders to process
      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertNull(workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[2].getStatus());
      assertEquals(ord2.getOperations().get(1), workstations[2].getCurrentOperation());

   }

   @Test
   void differentStatingPointsProcess3Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2"),
            new WorkCell("wst3") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1, 1 });
      Order ord2 = buildOrder("ord2", new WorkCell[] { workstations[1], workstations[2] }, new long[] { 3, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2));
      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);

      // first station is no more blocked as second station has finished, will be idle
      // as there are no other orders to process
      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertNull(workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertNull(workstations[1].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[2].getStatus());
      assertEquals(ord1.getOperations().get(2), workstations[2].getCurrentOperation());

   }

   @Test
   void differentStatingPointsProcess4Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2"),
            new WorkCell("wst3") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 1, 1 });
      Order ord2 = buildOrder("ord2", new WorkCell[] { workstations[1], workstations[2] }, new long[] { 3, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2));
      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);

      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertNull(workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertNull(workstations[1].getCurrentOperation());

      assertEquals(WorkCell.Status.IDLE, workstations[2].getStatus());
      assertNull(workstations[2].getCurrentOperation());

   }
}
