package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.flowshop.Workstation.Status;

public class WorkstationTest {

   @Test
   // this test will push a new opearation to the workstation and retrieve the
   // current operation. Let's suppose Operation has a string ID
   // TODO: what happen if it's busy and I try to push an operation?

   void pushOperation() {
      Workstation wst = new Workstation("wst");
      Operation op = new Operation("opId", 100l, wst, null);
      wst.setCurrentOperation(op);
      assertNotNull(wst.getCurrentOperation());
      assertEquals(op.getId(), wst.getCurrentOperation().getId());
   }

   @Test
   // after setting the operation then I need to assign one or more operators based
   // on the operation requirement to get the work done.
   // This test
   void requiredOperators() {
      Workstation wst = new Workstation("wst");
      assertEquals(0, wst.getRequiredOperators());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(2);
      wst.setCurrentOperation(op);
      assertEquals(2, wst.getRequiredOperators());

      Operation op1 = new Operation("opId2", 100, wst, null);
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
      Workstation wst = new Workstation("wst");
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
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
   }

   @Test
   // status is IDLE even if I bind some operators but I've not set the operation
   // on the workstation
   void statusIdleSetOnlyOperators() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("operator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Status.IDLE, wst.getStatus());
   }

   @Test
   // WAIT_FOR_OPERATOR: means that there is an operation assigned but still not
   // enough operators binded
   void statusWaitForOperator() {
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());

   }

   @Test
   // WAIT_FOR_OPEATOR: let's test the case where the operation needs more than one
   // operator and I bind a lower number than required
   void statusWaitForOpeatorLowerThanRequired() {
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
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
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
   }

   // PROCESSING: this status happen when a workstation has on operation and at
   // least the required operators assigned
   void statusProcessingBackToWaitingForOperator() {
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      wst.getAssignedOperators().remove(operator);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   void statusProcessingBackToIdle() {
      Workstation wst = new Workstation("wst");
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      wst.setCurrentOperation(null);
      assertEquals(Workstation.Status.IDLE, wst.getStatus());

   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorAssignedWorkstationUpdated() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      wst.getAssignedOperators().add(operator);
      assertEquals(Operator.Status.PROCESSING, operator.getStatus());
      assertEquals(wst, operator.getAssignedWorkstation());
      wst.getAssignedOperators().remove(operator);
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      assertNull(operator.getAssignedWorkstation());
   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorsCollectionAssignedWorkstationUpdated() {
      Workstation wst = new Workstation("wst");
      Operator operator1 = new Operator("idOperator1");
      Operator operator2 = new Operator("idOperator2");
      Operator operator3 = new Operator("idOperator3");
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.IDLE, operator3.getStatus());
      
      wst.getAssignedOperators().addAll(Arrays.asList(operator1, operator2, operator3));
      assertEquals(Operator.Status.PROCESSING, operator1.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator2.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator3.getStatus());
      assertEquals(wst, operator1.getAssignedWorkstation());
      assertEquals(wst, operator2.getAssignedWorkstation());
      assertEquals(wst, operator3.getAssignedWorkstation());

      wst.getAssignedOperators().removeAll(Arrays.asList(operator1, operator2));
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator3.getStatus());
      assertNull(operator1.getAssignedWorkstation());
      assertNull(operator2.getAssignedWorkstation());
      assertEquals(wst, operator3.getAssignedWorkstation());
   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorsCollectionClearAssignedWorkstations() {
      Workstation wst = new Workstation("wst");
      Operator operator1 = new Operator("idOperator1");
      Operator operator2 = new Operator("idOperator2");
      Operator operator3 = new Operator("idOperator3");
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.IDLE, operator3.getStatus());
      
      wst.getAssignedOperators().addAll(Arrays.asList(operator1, operator2, operator3));
      assertEquals(Operator.Status.PROCESSING, operator1.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator2.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator3.getStatus());
      assertEquals(wst, operator1.getAssignedWorkstation());
      assertEquals(wst, operator2.getAssignedWorkstation());
      assertEquals(wst, operator3.getAssignedWorkstation());

      wst.getAssignedOperators().clear();
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.IDLE, operator3.getStatus());
      assertNull(operator1.getAssignedWorkstation());
      assertNull(operator2.getAssignedWorkstation());
      assertNull(operator3.getAssignedWorkstation());
   }


   @Test
   // I can ask to a workstation to process some unit of time,
   // it will work only in PROCESSING status
   // the workstation will set some processed time to the operation, until there's
   // no more time to process and the operation will be "released" and then the
   // workstation will go to IDLE state
   void processOperation() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      long processedTime = wst.process(10);
      assertEquals(10, processedTime);
      assertEquals(10, op.getProcessedTime());
   }

   @Test
   void processOperationMoreTimes() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      long processedTime;
      processedTime = wst.process(10);
      assertEquals(10, processedTime);
      assertEquals(10, op.getProcessedTime());
      processedTime = wst.process(10);
      assertEquals(10, processedTime);
      assertEquals(20, op.getProcessedTime());
   }

   @Test
   void processOperationFinishOperation() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      wst.process(100);
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
   }

   @Test
   void processOperationExeedCycleTime() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.PROCESSING, wst.getStatus());
      long processedTime;
      processedTime = wst.process(120);
      assertEquals(100, processedTime);
      assertEquals(100, op.getProcessedTime());
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
   }

   @Test
   void processOperationOnIdleStatus() {
      Workstation wst = new Workstation("wst");
      Operator operator = new Operator("idOperator");
      wst.getAssignedOperators().add(operator);
      assertEquals(Workstation.Status.IDLE, wst.getStatus());
      long processedTime = wst.process(10);
      assertEquals(0, processedTime);
   }

   @Test
   void processOperationOnWaitingForOperatorStatus() {
      Workstation wst = new Workstation("wst");
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      wst.setCurrentOperation(op);
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, wst.getStatus());
      long processedTime = wst.process(10);
      assertEquals(0, processedTime);
   }

   @Test
   // I get a blocked status whenever I'm DONE with the operation
   // (@processOperationFinishOperation) but the workstation on the next operation
   // is still busy,
   // in this case the operation won't be released, it will be always visibile as
   // currentOperation even if the processed time is equal to the cycle time
   void blockedStatus() {
      Workstation wst1 = new Workstation("wst1");
      Workstation wst2 = new Workstation("wst2");
      Operation op2 = new Operation("opId2", 100l, wst2, null);
      Operation op1 = new Operation("opId1", 100l, wst1, op2);
      Operation prevOp = new Operation("prevOpId2", 100l, wst2, null);
      // let's suppose wst2 is still running the prevOp
      wst2.setCurrentOperation(prevOp);
      wst2.process(10);
      // no operators are needed so the status should be in processing
      assertEquals(Workstation.Status.PROCESSING, wst2.getStatus());
      // now let's suppose the op1 on wst2 was finished
      wst1.setCurrentOperation(op1);
      wst1.process(100);
      wst1.evalBlockedStatus();
      assertEquals(Workstation.Status.BLOCKED, wst1.getStatus());
   }

   @Test
   // this information is needed to unrelease the latest worked operation in case
   // of blocking status. Blocking status must be calculated after all workstations
   // are processed.
   void latestOperationSaved() {
      Workstation wst2 = new Workstation("wst1");
      Operation op2 = new Operation("opId2", 100l, wst2, null);
      wst2.setCurrentOperation(op2);
      wst2.process(100l);
      assertEquals(Workstation.Status.IDLE, wst2.getStatus());
      assertEquals(null, wst2.getCurrentOperation());
      assertEquals(op2, wst2.getLatestOperation());

   }

}
