package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorTest {

   private Order buildOrder(String orderId, Workstation[] workstations, long[] cycletimes) {
      Operation[] operations = new Operation[workstations.length];
      for (int i = workstations.length - 1; i >= 0; i--) {
         Operation nextOperation = (i == workstations.length - 1) ? null : operations[i + 1];
         operations[i] = new Operation(orderId + " op" + (i + 1), cycletimes[i], workstations[i], nextOperation);
      }
      return new Order(Arrays.asList(operations));

   }

   @Test
   void twoStationsStartSimulation() {
      Workstation[] workstations = new Workstation[] { new Workstation("wst1"), new Workstation("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));
      sim.start();

      // only the first workstation should be in processing
      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());

      // the second workstation is idle
      assertEquals(Workstation.Status.IDLE, workstations[1].getStatus());

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
      Workstation[] workstations = new Workstation[] { new Workstation("wst1"), new Workstation("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start(); // look at previous test for expected states
      sim.process(1);

      // first workstation should be in processing as the first operation of the first
      // order has finished and suddenly the first op of the second order should be
      // run
      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // now it's time for processing the second operation of the fist order for the
      // second workstation
      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord1.getOperations().get(1), ord2.getOperations().get(0), ord2.getOperations().get(1),
            ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

   @Test
   void twoStationsProgress2Simulation() {
      Workstation[] workstations = new Workstation[] { new Workstation("wst1"), new Workstation("wst2") };
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
      assertEquals(Workstation.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      // second operation of first order should still be in processin on the second
      // workstation
      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord2.getOperations().get(1), ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));

   }

   @Test
   void twoStationsProgress3Simulation() {
      Workstation[] workstations = new Workstation[] { new Workstation("wst1"), new Workstation("wst2") };
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

      assertEquals(Workstation.Status.BLOCKED, workstations[0].getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord2.getOperations().get(1), ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

   @Test
   void twoStationsProgress4Simulation() {
      Workstation[] workstations = new Workstation[] { new Workstation("wst1"), new Workstation("wst2") };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 3 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1);
      sim.process(1);
      sim.process(1); // look at previous test for expected states
      sim.process(1);
      // sim.process(1);

      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(0).getStatus());
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());

      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());
      assertEquals(ord2.getOperations().get(1), workstations[1].getCurrentOperation());

      Arrays.asList(ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

}
