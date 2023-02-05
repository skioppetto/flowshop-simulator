package com.flowshop.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorBufferedTwoStations {

   private Order buildOrder(String orderName, Workstation wst1, Workstation wst2) {
      Operation op2 = new Operation(orderName + ".slow.op2", 30, wst2, null);
      Operation op1 = new Operation(orderName + ".fast.op1", 10, wst1, op2);
      return new Order("order", Arrays.asList(op1, op2));

   }

   @Test
   void bufferedTwoStationsStart() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      // orders status
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.TODO, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.TODO, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.PROCESSING, bCell1.getStatus());
      assertEquals(Workstation.Status.IDLE, cell2.getStatus());
      // operation status
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(0).getStatus());
   }

   @Test
   void bufferedTwoStationsProcess1() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      // orders status
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.TODO, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.PROCESSING, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());
   }

   @Test
   void bufferedTwoStationsProcess2() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.PROCESSING, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertEquals(1, bCell1.getAfterBuffer().size());
      bCell1.getAfterBuffer().contains(sim.getOrders().get(1).getOperations().get(0));

   }

   @Test
   void bufferedTwoStationsProcess3() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.IDLE, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertEquals(2, bCell1.getAfterBuffer().size());
      bCell1.getAfterBuffer().contains(sim.getOrders().get(1).getOperations().get(0));
      bCell1.getAfterBuffer().contains(sim.getOrders().get(2).getOperations().get(0));
   }

   @Test
   void bufferedTwoStationsProcess4() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.DONE, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.IDLE, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      // TODO: make Operation Status PROGRESS even if processed time is equal to 0
      // when the operation is assigned to a workstation. In this way I'll get a
      // transaction TODO -> PROGRESS -> DONE even if the progress lasts only one
      // process step.
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertEquals(1, bCell1.getAfterBuffer().size());
      bCell1.getAfterBuffer().contains(sim.getOrders().get(2).getOperations().get(0));
   }

   @Test
   void bufferedTwoStationsProcess5() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.DONE, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.IDLE, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      // TODO: make Operation Status PROGRESS even if processed time is equal to 0
      // when the operation is assigned to a workstation. In this way I'll get a
      // transaction TODO -> PROGRESS -> DONE even if the progress lasts only one
      // process step.
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertEquals(1, bCell1.getAfterBuffer().size());
      bCell1.getAfterBuffer().contains(sim.getOrders().get(2).getOperations().get(0));
   }

   @Test
   void bufferedTwoStationsProcess6() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.DONE, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.IDLE, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      // TODO: make Operation Status PROGRESS even if processed time is equal to 0
      // when the operation is assigned to a workstation. In this way I'll get a
      // transaction TODO -> PROGRESS -> DONE even if the progress lasts only one
      // process step.
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertEquals(1, bCell1.getAfterBuffer().size());
      bCell1.getAfterBuffer().contains(sim.getOrders().get(2).getOperations().get(0));
   }

   @Test
   void bufferedTwoStationsProcess7() {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      // orders status
      assertEquals(Order.Status.DONE, sim.getOrders().get(0).getStatus());
      assertEquals(Order.Status.DONE, sim.getOrders().get(1).getStatus());
      assertEquals(Order.Status.PROGRESS, sim.getOrders().get(2).getStatus());
      // workstation status
      assertEquals(Workstation.Status.IDLE, bCell1.getStatus());
      assertEquals(Workstation.Status.PROCESSING, cell2.getStatus());
      // operation status
      // TODO: make Operation Status PROGRESS even if processed time is equal to 0
      // when the operation is assigned to a workstation. In this way I'll get a
      // transaction TODO -> PROGRESS -> DONE even if the progress lasts only one
      // process step.
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(2).getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(0).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.DONE, sim.getOrders().get(1).getOperations().get(1).getStatus());
      assertEquals(Operation.Status.PROGRESS, sim.getOrders().get(2).getOperations().get(1).getStatus());

      // buffers
      assertTrue(bCell1.getAfterBuffer().isEmpty());
   }
}
