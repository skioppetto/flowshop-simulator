package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class OperatorTest {

   // the operator can be idle, waiting for an assignment on can be in a processing
   // state if he is working

   @Test
   // let's test the assigment
   void assignWorkstation() {
      Operator operator = new Operator("operator");
      assertNull(operator.getAssignedWorkstation());
      WorkCell wst = new WorkCell("wst");
      operator.setAssignedWorkstation(wst);
      assertEquals(wst, operator.getAssignedWorkstation());
   }

   @Test
   void statusIdleAtCreation() {
      Operator operator = new Operator("operator");
      assertEquals(Operator.Status.IDLE, operator.getStatus());
   }

   @Test
   void statusProcessing() {
      Operator operator = new Operator("operator");
      assertNull(operator.getAssignedWorkstation());
      WorkCell wst = new WorkCell("wst");
      operator.setAssignedWorkstation(wst);
      assertEquals(Operator.Status.PROCESSING, operator.getStatus());
   }

   @Test
   void statusBackToIdle() {
      Operator operator = new Operator("operator");
      assertNull(operator.getAssignedWorkstation());
      WorkCell wst = new WorkCell("wst");
      operator.setAssignedWorkstation(wst);
      assertEquals(Operator.Status.PROCESSING, operator.getStatus());
      operator.setAssignedWorkstation(null);
      assertEquals(Operator.Status.IDLE, operator.getStatus());

   }
}
