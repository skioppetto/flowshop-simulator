package com.flowshop.simulator;

import static com.flowshop.simulator.SimulatorTestUtils.buildOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorTwoStationsTest {

   @Test
   void twoStationsStartSimulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));
      sim.start();

      // only the first workstation should be in processing
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());

      // the second workstation is idle
      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(0).getStatus());

      // all the other operations should be in TODO state
      Arrays.asList(ord1.getOperations().get(1), ord2.getOperations().get(0), ord2.getOperations().get(1),
            ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

   @Test
   void twoStationsProgress1Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start(); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());

      // assigned operations
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress2Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());

      // assigned operations
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.BLOCKED, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress3Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());

      // assigned operations
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.BLOCKED, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress4Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());

      // assigned operations
      assertEquals(ord2.getOperations().get(1), workstations[1].getCurrentOperation());
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress5Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());

      // assigned operations
      assertNull(workstations[1].getCurrentOperation());
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress6Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());

      // assigned operations
      assertNull(workstations[0].getCurrentOperation());
      assertEquals(ord3.getOperations().get(1), workstations[1].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(1).getStatus());
   }

   @Test
   void twoStationsProgress7Simulation() {
      WorkCell[] workstations = new WorkCell[] { new WorkCell("wst1"), new WorkCell("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // workstations
      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());

      // assigned operations
      assertNull(workstations[0].getCurrentOperation());
      assertNull(workstations[1].getCurrentOperation());

      // operation status
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(1).getStatus());

   }

}
