package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SimulatorTest {

   private Order buildOrder(String orderId, Workstation[] workstations, long[] cycletimes) {
      List<Operation> operations = new ArrayList<>();
      for (int i = 0; i < workstations.length; i++) {
         operations.add(new Operation(orderId + " op" + (i + 1), cycletimes[i], workstations[i]));
      }
      return new Order(operations);

   }

   @Test
   void twoStationsStartSimulation() {
      Workstation[] workstations = new Workstation[] { new Workstation(), new Workstation() };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 50, 100 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 70, 65 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 45, 100 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));
      sim.start();

      // only the first workstation should be in processing
      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());

      // the second workstation is idle
      assertEquals(Workstation.Status.IDLE, workstations[1].getStatus());

      // ony the first operation of the first order should be in progress
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(0).getStatus());

      // all the other operations should be in TODO state
      Arrays.asList(ord1.getOperations().get(1), ord2.getOperations().get(0), ord2.getOperations().get(1),
            ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

   @Test
   void twoStationsProgress1Simulation() {
      Workstation[] workstations = new Workstation[] { new Workstation(), new Workstation() };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 2 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start(); // look at previous test for expected states
      sim.process(1);

      // first workstation should be in processing as the first operation of the first
      // order has finished and suddenly the first op of the second order should be
      // run
      assertEquals(Workstation.Status.PROCESSING, workstations[0].getStatus());

      // now it's time for processing the second operation of the fist order for the
      // second workstation
      assertEquals(Workstation.Status.PROCESSING, workstations[1].getStatus());
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());

      // second operation of first order should still start but now it's the current
      // operation for the second workstation
      assertEquals(Operation.Status.TODO, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      // first operation of second order should still start but now it's the current
      // operation for the first workstation
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(0).getStatus());
      assertEquals(ord2.getOperations().get(0), workstations[0].getCurrentOperation());

      Arrays.asList(ord2.getOperations().get(1),
            ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));
   }

   @Test
   void twoStationsProgress2Simulation() {
      Workstation[] workstations = new Workstation[] { new Workstation(), new Workstation() };
      Order ord1 = buildOrder("ord1", workstations, new long[] { 1, 2 });
      Order ord2 = buildOrder("ord2", workstations, new long[] { 1, 1 });
      Order ord3 = buildOrder("ord3", workstations, new long[] { 2, 1 });
      Simulation sim = new Simulation(Arrays.asList(ord1, ord2, ord3));

      sim.start();
      sim.process(1); // look at previous test for expected states
      sim.process(1);

      // first operation of second order should be done on the first workstation
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      // first operation of third order should be ready on first workstation
      assertEquals(ord3.getOperations().get(0), workstations[0].getCurrentOperation());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());

      // second operation of first order should still be in processin on the second
      // workstation
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(ord1.getOperations().get(1), workstations[1].getCurrentOperation());

      // as second workstation is still busy as it's processing the second operation
      // of the first order,
      // second operation of the second order should wait as its workstation is busy
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());

      Arrays.asList(ord3.getOperations().get(0), ord3.getOperations().get(1))
            .forEach(op -> assertEquals(Operation.Status.TODO, op.getStatus()));

   }

}
