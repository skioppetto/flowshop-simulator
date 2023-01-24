package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.flowshop.WorkCell.Status;

public class WorkstationTest {

   @Test
   // this test will push a new opearation to the workstation and retrieve the
   // current operation. Let's suppose Operation has a string ID

   void assignOperation() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("opId", 100l, wst, null);
      assertNotNull(wst.assignOperation(op));
      assertNotNull(wst.getCurrentOperation());
      assertEquals(op.getId(), wst.getCurrentOperation().getId());
   }

   @Test
   // after setting the operation then I need to assign one or more operators based
   // on the operation requirement to get the work done.
   // This test
   void requiredOperators() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getRequiredOperators());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(2);
      assertTrue(wst.assignOperation(op));
      assertEquals(2, wst.getRequiredOperators());
   }

   @Test
   // now that I know how many operators I can assign needed operators in order to
   // get it work.
   // I can assign even more or less operators, in this way I can book some
   // operators waiting for some others,
   // or I can book more operators as I know that in the next future I will require
   // some more operators
   void assignOperators() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getAssignedOperators());
      Operator operator = new Operator("operatorId");
      wst.assignOperators(operator);
      assertEquals(1, wst.getAssignedOperators());
      assertEquals(wst, operator.getAssignedWorkstation());
   }

   @Test
   void unassignOperatorsEnabled() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getAssignedOperators());
      Operator operator = new Operator("operatorId");
      wst.assignOperators(operator);
      Set<Operator> releasedOperators = wst.unassignOperators();
      assertTrue(releasedOperators.contains(operator));
      assertEquals(1, releasedOperators.size());
      assertNull(releasedOperators.iterator().next().getAssignedWorkstation());
   }

   @Test
   void unassignOperatorsDisabledStatusProcessing() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getAssignedOperators());
      Operator operator = new Operator("operatorId");
      wst.assignOperators(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      wst.assignOperation(op);
      Set<Operator> releasedOperators = wst.unassignOperators();
      assertTrue(releasedOperators.isEmpty());
      assertEquals(1, wst.getAssignedOperators());
      assertEquals(wst, operator.getAssignedWorkstation());
   }

   @Test
   void unassignOperatorsDisabledStatusWaitingForOp() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getAssignedOperators());
      Operation op = new Operation("opId", 100l, wst, null);
      wst.assignOperation(op);
      Set<Operator> releasedOperators = wst.unassignOperators();
      assertTrue(releasedOperators.isEmpty());
      assertEquals(0, wst.getAssignedOperators());
   }

   // let's give a look to all different statuses now
   @Test
   // IDLE: means that the workstation is currently stopped due to lack of
   // operation
   void statusIdleAtCreation() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   // status is IDLE even if I bind some operators but I've not set the operation
   // on the workstation
   void statusIdleSetOnlyOperators() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("operator");
      wst.assignOperators(operator);
      assertEquals(Status.IDLE, wst.getStatus());
   }

   @Test
   // WAIT_FOR_OPERATOR: means that there is an operation assigned but still not
   // enough operators binded
   void statusWaitForOperator() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, wst.getStatus());

   }

   @Test
   // WAIT_FOR_OPEATOR: let's test the case where the operation needs more than one
   // operator and I bind a lower number than required
   void statusWaitForOpeatorLowerThanRequired() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(2);
      assertNotNull(wst.assignOperation(op));
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   // PROCESSING: this status happen when a workstation has on operation and at
   // least the required operators assigned
   void statusProcessing() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
   }

   // PROCESSING: this status happen when a workstation has on operation and at
   // least the required operators assigned
   void statusProcessingBackToWaitingForOperator() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
      wst.unassignOperators();
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorAssignedWorkstationUpdated() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      wst.assignOperators(operator);
      assertEquals(Operator.Status.PROCESSING, operator.getStatus());
      assertEquals(wst, operator.getAssignedWorkstation());
      wst.unassignOperators();
      assertEquals(Operator.Status.IDLE, operator.getStatus());
      assertNull(operator.getAssignedWorkstation());
   }

   @Test
   // let's simplify status management by automatically set the assigned
   // workstation to the operator when added to assigned operators
   void operatorsCollectionAssignedWorkstationUpdated() {
      WorkCell wst = new WorkCell("wst");
      Operator operator1 = new Operator("idOperator1");
      Operator operator2 = new Operator("idOperator2");
      Operator operator3 = new Operator("idOperator3");
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.IDLE, operator3.getStatus());

      wst.assignOperators(operator1, operator2, operator3);
      assertEquals(Operator.Status.PROCESSING, operator1.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator2.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator3.getStatus());
      assertEquals(wst, operator1.getAssignedWorkstation());
      assertEquals(wst, operator2.getAssignedWorkstation());
      assertEquals(wst, operator3.getAssignedWorkstation());

      wst.unassignOperators();

      wst.assignOperators(operator3);
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
      WorkCell wst = new WorkCell("wst");
      Operator operator1 = new Operator("idOperator1");
      Operator operator2 = new Operator("idOperator2");
      Operator operator3 = new Operator("idOperator3");
      assertEquals(Operator.Status.IDLE, operator1.getStatus());
      assertEquals(Operator.Status.IDLE, operator2.getStatus());
      assertEquals(Operator.Status.IDLE, operator3.getStatus());

      wst.assignOperators(operator1, operator2, operator3);
      assertEquals(Operator.Status.PROCESSING, operator1.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator2.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator3.getStatus());
      assertEquals(wst, operator1.getAssignedWorkstation());
      assertEquals(wst, operator2.getAssignedWorkstation());
      assertEquals(wst, operator3.getAssignedWorkstation());

      wst.unassignOperators();
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
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
      long processedTime = wst.process(10);
      assertEquals(10, processedTime);
      assertEquals(10, op.getProcessedTime());
   }

   @Test
   void processOperationMoreTimes() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
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
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
      wst.process(100);
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   void processOperationExeedCycleTime() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
      long processedTime;
      processedTime = wst.process(120);
      assertEquals(100, processedTime);
      assertEquals(100, op.getProcessedTime());
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   void processOperationOnIdleStatus() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("idOperator");
      wst.assignOperators(operator);
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
      long processedTime = wst.process(10);
      assertEquals(0, processedTime);
   }

   @Test
   void processOperationOnWaitingForOperatorStatus() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("opId", 100l, wst, null);
      op.setRequiredOperators(1);
      assertNotNull(wst.assignOperation(op));
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, wst.getStatus());
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
      WorkCell wst1 = new WorkCell("wst1");
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("opId2", 100l, wst2, null);
      Operation op1 = new Operation("opId1", 100l, wst1, op2);
      Operation prevOp = new Operation("prevOpId2", 100l, wst2, null);
      // let's suppose wst2 is still running the prevOp
      assertNotNull(wst2.assignOperation(prevOp));
      wst2.process(10);
      // no operators are needed so the status should be in processing
      assertEquals(WorkCell.Status.PROCESSING, wst2.getStatus());
      // now let's suppose the op1 on wst2 was finished
      assertNotNull(wst1.assignOperation(op1));
      wst1.process(100);
      wst1.evalBlockedStatus();
      assertEquals(WorkCell.Status.BLOCKED, wst1.getStatus());
   }

   @Test
   // this information is needed to unrelease the latest worked operation in case
   // of blocking status. Blocking status must be calculated after all workstations
   // are processed.
   void latestOperationSaved() {
      WorkCell wst2 = new WorkCell("wst1");
      Operation op2 = new Operation("opId2", 100l, wst2, null);
      assertNotNull(wst2.assignOperation(op2));
      wst2.process(100l);
      assertEquals(WorkCell.Status.IDLE, wst2.getStatus());
      assertEquals(null, wst2.getCurrentOperation());
      assertEquals(op2, wst2.getLatestOperation());

   }

}
