package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OperationTest {

   @Test
   void statusToDoAtCreation() {
      Operation op = new Operation("opIf", 100l, null);
      assertEquals(Operation.Status.TODO, op.getStatus());
   }

   @Test
   void statusProgress() {
      Operation op = new Operation("opIf", 100l, null);
      op.setProcessedTime(20);
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void statusDone() {
      Operation op = new Operation("opIf", 100l, null);
      op.setProcessedTime(100);
      assertEquals(Operation.Status.DONE, op.getStatus());
   }

}
