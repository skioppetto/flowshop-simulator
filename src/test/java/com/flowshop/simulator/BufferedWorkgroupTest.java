package com.flowshop.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class BufferedWorkgroupTest {

   BufferedWorkstation grp1;
   WorkCell wrk2;
   Order ord1, ord2, ord3, ord4;
   Simulation sim;

   private void buildLine() {
      grp1 = new BufferedWorkstation(new WorkGroup("grp1",
            new HashSet<>(Arrays.asList(new WorkCell("grp1.cell1"), new WorkCell("grp1.cell2")))), 2, 2);
      wrk2 = new WorkCell("cell2");
      Operation ord1op2 = new Operation("ord1.op2", 20, wrk2, null);
      Operation ord1op1 = new Operation("ord1.op1", 10, grp1, ord1op2);
      Operation ord2op2 = new Operation("ord2.op2", 20, wrk2, null);
      Operation ord2op1 = new Operation("ord2.op1", 10, grp1, ord2op2);
      Operation ord3op1 = new Operation("ord3.op1", 10, grp1, null);
      Operation ord4op1 = new Operation("ord4.op1", 10, grp1, null);

      ord1 = new Order(Arrays.asList(ord1op1, ord1op2));
      ord2 = new Order(Arrays.asList(ord2op1, ord2op2));
      ord3 = new Order(Arrays.asList(ord3op1));
      ord4 = new Order(Arrays.asList(ord4op1));

      sim = new Simulation(Arrays.asList(ord1, ord2, ord3, ord4));
   }

   @Test
   void bufferedWorkgroupStart() {
      buildLine();
      sim.start();

      // orders
      assertEquals(Order.Status.PROGRESS, ord1.getStatus());
      assertEquals(Order.Status.PROGRESS, ord2.getStatus());
      assertEquals(Order.Status.TODO, ord3.getStatus());
      assertEquals(Order.Status.TODO, ord4.getStatus());

      // workstations
      assertEquals(WorkCell.Status.PROCESSING, grp1.getStatus());
      assertEquals(WorkCell.Status.IDLE, wrk2.getStatus());

      // operations
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord4.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.TODO, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());

      // buffers
      assertTrue(grp1.getAfterBuffer().isEmpty());
      assertEquals(2, grp1.getBeforeBuffer().size());
      assertTrue(grp1.getBeforeBuffer().contains(ord3.getOperations().get(0)));
      assertTrue(grp1.getBeforeBuffer().contains(ord4.getOperations().get(0)));
   }

   @Test
   void bufferedWorkgroupProcess1() {
      buildLine();
      sim.start();
      sim.process(10);

      // orders
      assertEquals(Order.Status.PROGRESS, ord1.getStatus());
      assertEquals(Order.Status.PROGRESS, ord2.getStatus());
      assertEquals(Order.Status.PROGRESS, ord3.getStatus());
      assertEquals(Order.Status.PROGRESS, ord4.getStatus());

      // workstations
      assertEquals(WorkCell.Status.PROCESSING, grp1.getStatus());
      assertEquals(WorkCell.Status.PROCESSING, wrk2.getStatus());

      // operations
      assertEquals(Operation.Status.DONE, ord1.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.DONE, ord2.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord3.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord4.getOperations().get(0).getStatus());
      assertEquals(Operation.Status.PROGRESS, ord1.getOperations().get(1).getStatus());
      assertEquals(Operation.Status.TODO, ord2.getOperations().get(1).getStatus());

      // buffers
      assertTrue(grp1.getBeforeBuffer().isEmpty());
      assertEquals(1, grp1.getAfterBuffer().size());
      assertTrue(grp1.getAfterBuffer().contains(ord2.getOperations().get(0)));
   }

}
