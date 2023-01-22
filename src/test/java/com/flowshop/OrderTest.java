package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class OrderTest {

   // an order is a list of operations that must be performed in a strict order.
   // I can ask to an order to get the next TODO operation: in this case I'll get
   // null if there are no more operations to process or if the current operation
   // is in PROGRESS
   // order has also a status: it can be TODO, PROGRESS or DONE based on the number
   // of operations DONE

   @Test
   void statusTodo() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      assertEquals(Order.Status.TODO, order.getStatus());
   }

   @Test
   void statusProgress() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(5);
      assertEquals(Order.Status.PROGRESS, order.getStatus());
   }

   @Test
   void statusProgress2() {
      Operation op2 = new Operation("op2", 20, null, null);
      Operation op1 = new Operation("op1", 10, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(10);
      op2.setProcessedTime(10);
      assertEquals(Order.Status.PROGRESS, order.getStatus());
   }

   @Test
   void statusDone() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(10);
      op2.setProcessedTime(20);
      assertEquals(Order.Status.DONE, order.getStatus());
   }

   @Test
   void nextOperationFirst() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      assertEquals(op1, order.getNextOperation());
   }

   @Test
   void nextOperationProgress() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(5);
      assertNull(order.getNextOperation());
   }

   @Test
   void nextOperation() {
      Operation op2 = new Operation("op2", 20, null, null);
      Operation op1 = new Operation("op1", 10, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(10);
      assertEquals(op2, order.getNextOperation());
   }

   @Test
   void nextOperationDone() {
      Operation op2 = new Operation("op2", 10, null, null);
      Operation op1 = new Operation("op1", 20, null, op2);
      Order order = new Order(Arrays.asList(op1, op2));
      op1.setProcessedTime(10);
      op2.setProcessedTime(20);
      assertNull(order.getNextOperation());
   }
}
