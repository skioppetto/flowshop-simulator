package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.flowshop.SimulatorTestUtils.buildOrder;
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

      // TODO: ony the first operation of the first order should be in progress -->
      // create a new status when the opearation is ready to run on the workstation,
      // look at currentOperation equal to the operation and processed time is equal
      // to 0
      assertEquals(Operation.Status.TODO, ord1.getOperations().get(0).getStatus());

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

      // first workstation should be in processing as the first operation of the first
      // order has finished and suddenly the first op of the second order should be
      // run
      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // now it's time for processing the second operation of the fist order for the
      // second workstation
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord1.getOperations().get(1), ord2.getOperations().get(0), ord2.getOperations().get(1),
            ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
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

      // first operation of second order should be done on the first workstation but
      // it won't be release in favour of the third order as the workstation is
      // blocked
      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // second operation of first order should still be in processin on the second
      // workstation
      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord2.getOperations().get(1), ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));

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

      // the same as before as second op of first order need another step
      // TODO: suggest the time units to process until something change on the line
      // status

      assertEquals(WorkCell.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord2.getOperations().get(1), ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
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

      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());

      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());
      assertEquals(ord2.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
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

      assertEquals(WorkCell.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(0).getStatus());
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());

      // now the second station is idle as second op of second order was finished
      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(1).getStatus());

      Arrays.asList(ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
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

      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(0).getStatus());

      assertEquals(WorkCell.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(1).getStatus());
      assertEquals(ord3.getOperations().get(1), workstations[1].getCurrentOperation());
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

      assertEquals(WorkCell.Status.IDLE, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(0).getStatus());

      assertEquals(WorkCell.Status.IDLE, workstations[1].getStatus());
      assertEquals(Operation.Status.DONE, ord3.getOperations().get(1).getStatus());

      // all operations were closed successfully
      for (Order order : sim.getOrders()) {
         for (Operation operation : order.getOperations()) {
            assertEquals(Operation.Status.DONE, operation.getStatus());
         }
      }
   }

}
