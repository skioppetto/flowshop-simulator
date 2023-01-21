package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.flowshop.Workstation.Status;

public class WorkstationTest {

   @Test
   // this test will push a new opearation to the workstation and retrieve the
   // current operation. Let's suppose Operation has a string ID
   // TODO: what happen if it's busy and I try to push an operation?

   void pushOperation() {
      Workstation wst = new Workstation();
      Operation op = new Operation("opId");
      wst.setCurrentOperation(op);
      assertNotNull(wst.getCurrentOperation());
      assertEquals(op.getId(), wst.getCurrentOperation().getId());
   }

   @Test
   // after setting the operation then I need to assign one or more operators based
   // on the operation requirement to get the work done.
   // This test
   void requiredOperators() {
      Workstation wst = new Workstation();
      assertEquals(0, wst.getRequiredOperators());
      Operation op = new Operation("opId");
      op.setRequiredOperators(2);
      wst.setCurrentOperation(op);
      assertEquals(2, wst.getRequiredOperators());

      Operation op1 = new Operation("opID2");
      op1.setRequiredOperators(3);
      wst.setCurrentOperation(op1);
      assertEquals(3, wst.getRequiredOperators());
   }

   @Test
   // now that I know how many operators I can assign needed operators in order to
   // get it work.
   // I can assign even more or less operators, in this way I can book some
   // operators waiting for some others,
   // or I can book more operators as I know that in the next future I will require
   // some more operators
   void assignOperators() {
      Workstation wst = new Workstation();
      assertTrue(wst.getAssignedOperators().isEmpty());
      Operator operator = new Operator("operatorId");
      wst.getAssignedOperators().add(operator);
      assertEquals(1, wst.getAssignedOperators().size());
      wst.getAssignedOperators().remove(operator);
      assertTrue(wst.getAssignedOperators().isEmpty());
   }

   // let's give a look to all different statuses now
   @Test
   // IDLE: means that the workstation is currently stopped due to lack of
   // operation
   void statusIdleAtCreation() {
      Workstation wst = new Workstation();
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
   }

   @Test
   // status is IDLE even if I bind some operators but I've not set the operation
   // on the workstation
   void statusIdleSetOnlyOperators() {
      Workstation wst = new Workstation();
      Operator operator = new Operator("operator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Status.IDLE, wst.getStatus());
   }

   @Test
   // WAIT_FOR_OPERATOR: means that there is an operation assigned but still not
   // enough operators binded
   void statusWaitForOperator() {
      Workstation wst = new Workstation();
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId");
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());

   }

   @Test
   // WAIT_FOR_OPEATOR: let's test the case where the operation needs more than one
   // operator and I bind a lower number than required
   void statusWaitForOpeatorLowerThanRequired() {
      Workstation wst = new Workstation();
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId");
      op.setRequiredOperators(2);
      wst.setCurrentOperation(op);
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   // PROCESSING: this status happen when a workstation has on operation and at
   // least the required operators assigned
   void statusProcessing() {
      Workstation wst = new Workstation();
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId");
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
   }

   // PROCESSING: this status happen when a workstation has on operation and at
   // least the required operators assigned
   void statusProcessingBackToWaitingForOperator() {
      Workstation wst = new Workstation();
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId");
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      wst.getAssignedOperators().remove(operator);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorAssignedWorkstationUpdated() {
      Workstation wst = new Workstation();
      Operator operator = new Operator("idOperator");
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      wst.getAssignedOperators().add(operator);
      assertEquals(Operator.Status.PROCESSING, operator.getStatus());
      assertEquals(wst, operator.getAssignedWorkstation());
      wst.getAssignedOperators().remove(operator);
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      assertNull(operator.getAssignedWorkstation());
   }

   // TODO: how can I remove an operation? In this case the status should go back
   // to IDLE
}
