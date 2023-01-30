package com.flowshop.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class WorkstationTest {

   @Test
   void getCurrentOpearationNullWhenCreated() {
      WorkCell wst = new WorkCell("wst");
      assertNull(wst.getCurrentOperation());
   }

   @Test
   void getCurrentOpearationAfterAssignOp() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(op, wst.getCurrentOperation());
   }

   @Test
   void getStatusIdle() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   void getStatusWaitingForOperator() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 2);
      wst.assignOperation(op);
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, wst.getStatus());
   }

   @Test
   void getStatusProcessing() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 1);
      wst.assignOperation(op);
      Operator operator = new Operator("operator");
      wst.assignOperators(Arrays.asList(operator));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
   }

   @Test
   void getStatusProcessingNoRequiredOperators() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
   }

   @Test
   void getStatusBlocked() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2);
      wst1.assignOperation(op1);

      wst1.process(10);
      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();

      assertEquals(WorkCell.Status.BLOCKED, wst1.getStatus());
   }

   @Test
   void evalBlockedStatus() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2);
      wst1.assignOperation(op1);

      wst1.process(10);
      wst2.process(10);
      assertTrue(wst1.evalBlockedStatus());
      assertFalse(wst2.evalBlockedStatus());
   }

   @Test
   void getLatestOperation() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      wst.process(10);
      assertEquals(op, wst.getLatestOperation());
   }

   @Test
   void processIdle() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.process(10));
   }

   @Test
   void processWaitingForOperator() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 1);
      wst.assignOperation(op);
      assertEquals(0, wst.process(10));
   }

   @Test
   void processProcessing() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(5, wst.process(5));
      assertEquals(WorkCell.Status.PROCESSING, wst.getStatus());
   }

   @Test
   void processProcessingEndOperation() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(10, wst.process(10));
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   void processProcessingExceedCycleTime() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(10, wst.process(20));
      assertEquals(WorkCell.Status.IDLE, wst.getStatus());
   }

   @Test
   void processBlocked() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2);
      wst1.assignOperation(op1);

      wst1.process(10);
      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();

      assertEquals(0, wst1.process(20));
   }

   @Test
   void assignOperatorsIdle() {
      WorkCell wst = new WorkCell("wst");
      Operator operator = new Operator("opeartor");
      assertTrue(wst.assignOperators(Arrays.asList(operator)).isEmpty());
   }

   @Test
   void assignOperatorsWaitingForOperators() {
      WorkCell wst = new WorkCell("wst");
      Operation op1 = new Operation("operation1", 10, wst, null, 1);
      wst.assignOperation(op1);
      Operator operator = new Operator("opeartor");
      Set<Operator> assignedSet = wst.assignOperators(Arrays.asList(operator));
      assertEquals(1, assignedSet.size());
      assertTrue(assignedSet.contains(operator));
   }

   @Test
   void assignOperatorsWaitingForOperatorsExceed() {
      WorkCell wst = new WorkCell("wst");
      Operation op1 = new Operation("operation1", 10, wst, null, 1);
      wst.assignOperation(op1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      Set<Operator> assignedSet = wst.assignOperators(Arrays.asList(operator1, operator2));
      assertEquals(1, assignedSet.size());
      assertTrue(assignedSet.contains(operator1));
   }

   @Test
   void assignOperatorsWaitingForOperatorsLess() {
      WorkCell wst = new WorkCell("wst");
      Operation op1 = new Operation("operation1", 10, wst, null, 2);
      wst.assignOperation(op1);
      Operator operator1 = new Operator("operator1");
      Set<Operator> assignedSet = wst.assignOperators(Arrays.asList(operator1));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void assignOperatorsProcessing() {
      WorkCell wst = new WorkCell("wst");
      Operation op1 = new Operation("operation1", 10, wst, null, 1);
      wst.assignOperation(op1);
      Operator operator1 = new Operator("opeartor1");
      wst.assignOperators(Arrays.asList(operator1));
      wst.process(5);

      Operator operator2 = new Operator("opeartor2");
      Set<Operator> assignedSet = wst.assignOperators(Arrays.asList(operator2));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void assignOperatorsBlocked() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2, 1);
      wst1.assignOperation(op1);
      Operator operator1 = new Operator("opeartor1");
      wst1.assignOperators(Arrays.asList(operator1));

      wst1.process(10);
      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();

      Operator operator2 = new Operator("opeartor2");
      Set<Operator> assignedSet = wst1.assignOperators(Arrays.asList(operator2));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void unassignOperators() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      wst.assignOperation(op);
      wst.assignOperators(Arrays.asList(operator1, operator2));
      wst.process(10);
      Set<Operator> unassignSet = wst.unassignOperators();
      assertEquals(2, unassignSet.size());
      assertTrue(unassignSet.contains(operator1));
      assertTrue(unassignSet.contains(operator2));
   }

   @Test
   void unassignOperatorsProcessing() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      wst.assignOperation(op);
      wst.assignOperators(Arrays.asList(operator1, operator2));
      wst.process(5);
      assertTrue(wst.unassignOperators().isEmpty());
   }

   @Test
   void getAssignedOperators() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null, 2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      wst.assignOperation(op);
      wst.assignOperators(Arrays.asList(operator1, operator2));
      assertEquals(2, wst.getAssignedOperators());
   }

   @Test
   void getAssignedOperatorsIdle() {
      WorkCell wst = new WorkCell("wst");
      assertEquals(0, wst.getAssignedOperators());
   }

   @Test
   void getOperationStatusToDo() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      assertEquals(Operation.Status.TODO, op.getStatus());
   }

   @Test
   void getOperationStatusStartProgress() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void getOperationStatusProgress() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      wst.process(5);
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void getOperationStatusDone() {
      WorkCell wst = new WorkCell("wst");
      Operation op = new Operation("operation", 10, wst, null);
      wst.assignOperation(op);
      wst.process(10);
      assertEquals(Operation.Status.DONE, op.getStatus());
   }

   @Test
   void getOperationStatusBlocked() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2);
      wst1.assignOperation(op1);

      wst1.process(10);
      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();
      assertEquals(Operation.Status.BLOCKED, op1.getStatus());
   }

   @Test
   void getOperationStatusUnBlocked() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);

      WorkCell wst1 = new WorkCell("wst1");
      Operation op1 = new Operation("operation1", 10, wst1, op2);
      wst1.assignOperation(op1);

      wst1.process(10);
      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();
      assertEquals(Operation.Status.BLOCKED, op1.getStatus());

      wst2.process(10);
      wst1.evalBlockedStatus();
      wst2.evalBlockedStatus();
      assertEquals(Operation.Status.DONE, op1.getStatus());
   }

   @Test
   void unassignOperation() {
      WorkCell wst2 = new WorkCell("wst2");
      Operation op2 = new Operation("operation", 20, wst2, null);
      wst2.assignOperation(op2);
      wst2.process(10);
      assertEquals(WorkCell.Status.PROCESSING, wst2.getStatus());
      assertEquals(op2, wst2.unassignOperation());
      assertEquals(WorkCell.Status.IDLE, wst2.getStatus());

   }

   @Test
   void getStatusWaitingForOperatorGroupsRequirement() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      cell.assignOperation(op);
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, cell.getStatus());
   }

   @Test
   void assignOperatorsGroupsRequirement() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      cell.assignOperation(op);
      Operator op1 = new Operator("op1", "group1");
      Operator op2 = new Operator("op2", "group1");
      Operator op3 = new Operator("op3", "group2");
      Set<Operator> assignee = new HashSet<>(Arrays.asList(op1, op2, op3));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.containsAll(assignee));
      assertEquals(3, assigned.size());
      assertEquals(cell, op1.getAssignedWorkstation());
      assertEquals(cell, op2.getAssignedWorkstation());
      assertEquals(cell, op3.getAssignedWorkstation());
      assertEquals(WorkCell.Status.PROCESSING, cell.getStatus());
   }

   @Test
   void assignOperatorsGroupsAndGenericRequirement() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      op.setRequiredOperators(2);
      cell.assignOperation(op);
      Operator op1 = new Operator("op1", "group1");
      Operator op2 = new Operator("op2", "group1");
      Operator op3 = new Operator("op3", "group2");
      Operator op4 = new Operator("op4");
      Operator op5 = new Operator("op5");
      Set<Operator> assignee = new HashSet<>(Arrays.asList(op1, op2, op3, op4, op5));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.containsAll(assignee));
      assertEquals(5, assigned.size());
      assertEquals(cell, op1.getAssignedWorkstation());
      assertEquals(cell, op2.getAssignedWorkstation());
      assertEquals(cell, op3.getAssignedWorkstation());
      assertEquals(cell, op4.getAssignedWorkstation());
      assertEquals(cell, op5.getAssignedWorkstation());
      assertEquals(WorkCell.Status.PROCESSING, cell.getStatus());
   }

   @Test
   void assignOperatorsGroupsAndGenericRequirementUnusedGroupOperator() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      op.setRequiredOperators(2);
      cell.assignOperation(op);
      Operator op1 = new Operator("op1", "group1");
      Operator op2 = new Operator("op2", "group1");
      Operator op3 = new Operator("op3", "group2");
      // this will be unused for group2 as it require only one operator and will be
      // assigned to the generic group
      Operator op4 = new Operator("op4", "group2");
      Operator op5 = new Operator("op5");
      Set<Operator> assignee = new HashSet<>(Arrays.asList(op1, op2, op3, op4, op5));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.containsAll(assignee));
      assertEquals(5, assigned.size());
      assertEquals(cell, op1.getAssignedWorkstation());
      assertEquals(cell, op2.getAssignedWorkstation());
      assertEquals(cell, op3.getAssignedWorkstation());
      assertEquals(cell, op4.getAssignedWorkstation());
      assertEquals(cell, op5.getAssignedWorkstation());
      assertEquals(WorkCell.Status.PROCESSING, cell.getStatus());
   }

   @Test
   void assignOperatorsGroupsAndGenericRequirementNotEnoughGroupOperator() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      op.setRequiredOperators(2);
      cell.assignOperation(op);
      // operation require 2 operators from group 1, assignee set contains only one.
      // Nobody will be assigned to the workstation
      Operator op1 = new Operator("op1", "group1");
      Operator op2 = new Operator("op2", "group2");
      Operator op3 = new Operator("op3", "group2");
      Operator op4 = new Operator("op4", "group2");
      Operator op5 = new Operator("op5");
      Set<Operator> assignee = new HashSet<>(Arrays.asList(op1, op2, op3, op4, op5));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.isEmpty());
      assertNull(op1.getAssignedWorkstation());
      assertNull(op2.getAssignedWorkstation());
      assertNull(op3.getAssignedWorkstation());
      assertNull(op4.getAssignedWorkstation());
      assertNull(op5.getAssignedWorkstation());
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, cell.getStatus());
   }

   @Test
   void assignOperatorsWrongGroupsRequirement() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      cell.assignOperation(op);
      Set<Operator> assignee = new HashSet<>();
      assignee.add(new Operator("op1", "group2"));
      assignee.add(new Operator("op2", "group2"));
      assignee.add(new Operator("op3", "group2"));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.isEmpty());
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, cell.getStatus());
   }

   @Test
   void unassignOperatorsGroups() {
      WorkCell cell = new WorkCell("cell1");
      Operation op = new Operation("op1", 10, cell, null);
      op.getRequiredOperatorsGroups().put("group1", 2);
      op.getRequiredOperatorsGroups().put("group2", 1);
      op.setRequiredOperators(2);
      cell.assignOperation(op);
      Operator op1 = new Operator("op1", "group1");
      Operator op2 = new Operator("op2", "group1");
      Operator op3 = new Operator("op3", "group2");
      // this will be unused for group2 as it require only one operator and will be
      // assigned to the generic group
      Operator op4 = new Operator("op4", "group2");
      Operator op5 = new Operator("op5");
      Set<Operator> assignee = new HashSet<>(Arrays.asList(op1, op2, op3, op4, op5));
      Set<Operator> assigned = cell.assignOperators(assignee);
      assertTrue(assigned.containsAll(assignee));
      assertEquals(5, assigned.size());
      assertEquals(cell, op1.getAssignedWorkstation());
      assertEquals(cell, op2.getAssignedWorkstation());
      assertEquals(cell, op3.getAssignedWorkstation());
      assertEquals(cell, op4.getAssignedWorkstation());
      assertEquals(cell, op5.getAssignedWorkstation());
      assertEquals(WorkCell.Status.PROCESSING, cell.getStatus());
      cell.process(10);
      Set<Operator> unassigned = cell.unassignOperators();
      assertTrue(unassigned.containsAll(assignee));
      assertNull(op1.getAssignedWorkstation());
      assertNull(op2.getAssignedWorkstation());
      assertNull(op3.getAssignedWorkstation());
      assertNull(op4.getAssignedWorkstation());
      assertNull(op5.getAssignedWorkstation());
      assertEquals(WorkCell.Status.IDLE, cell.getStatus());
   }

}