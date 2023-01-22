package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OperationTest {

   @Test
   void statusToDoAtCreation() {
      Operation op = new Operation("opId", 100l, null, null);
      assertEquals(Operation.Status.TODO, op.getStatus());
   }

   @Test
   void statusProgress() {
      Operation op = new Operation("opId", 100l, null, null);
      op.setProcessedTime(20);
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void statusDone() {
      Operation op = new Operation("opId", 100l, null, null);
      op.setProcessedTime(100);
      assertEquals(Operation.Status.DONE, op.getStatus());
   }

   @Test
   // if required operators is not set is expected that no operators are required
   // to run the operation
   void defaultRequiredOperators() {
      Operation op = new Operation("opId", 50, null, null);
      assertEquals(0, op.getRequiredOperators());
   }

}
