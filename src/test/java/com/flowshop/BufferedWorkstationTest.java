package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BufferedWorkstationTest {

   @Test
   void bufferBeforeEnqueueOperation() {
      WorkCell cell1 = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell1, 0, 1);
      Operation op2 = new Operation("op2", 10, bw, null);
      Operation op1 = new Operation("op1", 10, bw, op2);
      assertTrue(bw.assignOperation(op1));
      assertTrue(bw.assignOperation(op2));
      assertEquals(1, bw.getBeforeBuffer().size());
      assertTrue(bw.getBeforeBuffer().contains(op2));
      assertEquals(op1, cell1.getCurrentOperation());
   }

   @Test
   void bufferBeforeEnqueueOperationExceedBufferSize() {
      WorkCell cell1 = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell1, 0, 1);
      Operation op3 = new Operation("op3", 10, bw, null);
      Operation op2 = new Operation("op2", 10, bw, op3);
      Operation op1 = new Operation("op1", 10, bw, op2);
      assertTrue(bw.assignOperation(op1));
      assertTrue(bw.assignOperation(op2));
      assertFalse(bw.assignOperation(op3));
      assertEquals(1, bw.getBeforeBuffer().size());
      assertTrue(bw.getBeforeBuffer().contains(op2));
      assertEquals(op1, cell1.getCurrentOperation());
   }

   @Test
   void bufferBeforeDequeueProcessEnds() {
      WorkCell cell1 = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell1, 0, 1);
      Operation op2 = new Operation("op2", 10, bw, null);
      Operation op1 = new Operation("op1", 10, bw, op2);
      assertTrue(bw.assignOperation(op1));
      assertTrue(bw.assignOperation(op2));
      assertEquals(1, bw.getBeforeBuffer().size());
      assertTrue(bw.getBeforeBuffer().contains(op2));
      bw.process(10);
      bw.evalBlockedStatus();
      assertTrue(bw.getBeforeBuffer().isEmpty());
      assertEquals(op2, cell1.getCurrentOperation());
   }

   @Test
   void bufferBeforeDequeueProcessEndsEmptyBuffer() {
      WorkCell cell1 = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell1, 0, 1);
      Operation op2 = new Operation("op2", 10, bw, null);
      Operation op1 = new Operation("op1", 10, bw, op2);
      assertTrue(bw.assignOperation(op1));
      assertTrue(bw.getBeforeBuffer().isEmpty());
      bw.process(10);
      bw.evalBlockedStatus();
      assertNull(cell1.getCurrentOperation());
   }

   @Test
   void bufferAfterEnqueue() {
      WorkCell cell1 = new WorkCell("cell1");
      WorkCell cell2 = new WorkCell("cell2");
      BufferedWorkstation bw = new BufferedWorkstation(cell1, 1, 0);
      Operation op2 = new Operation("op2", 20, cell2, null);
      Operation op1 = new Operation("op1", 10, bw, op2);
      bw.assignOperation(op1);
      cell2.assignOperation(op2);
      bw.process(10);
      cell2.process(10);
      bw.evalBlockedStatus();
      cell2.evalBlockedStatus();
      assertFalse(bw.evalBlockedStatus());
      assertFalse(cell2.evalBlockedStatus());
      assertEquals(1, bw.getAfterBuffer().size());
      assertTrue(bw.getAfterBuffer().contains(op1));
      assertNull(cell1.getCurrentOperation());

   }

}
