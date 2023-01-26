package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class WorkGroupTest {

   @Test
   void getStatusIdle() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      assertEquals(WorkCell.Status.IDLE, group.getStatus());
   }

   @Test
   void getStatusIdlePartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op = new Operation("operation", 10, group, null, 2);
      group.assignOperation(op);
      assertEquals(WorkCell.Status.IDLE, group.getStatus());
   }

   @Test
   void getStatusWaitingForOperator() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, group.getStatus());
   }

   @Test
   void getStatusWaitingForOperatorPartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      Operator operator1 = new Operator("operator1");
      group.assignOperation(op1);
      group.assignOperation(op2);
      group.assignOperators(Arrays.asList(operator1));
      assertEquals(WorkCell.Status.WAITING_FOR_OPERATOR, group.getStatus());
   }

   @Test
   void getStatusProcessing() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperation(op1);
      group.assignOperation(op2);
      group.assignOperators(Arrays.asList(operator1));
      group.assignOperators(Arrays.asList(operator2));
      assertEquals(WorkCell.Status.PROCESSING, group.getStatus());
   }

   @Test
   void getStatusProcessingNoRequiredOperators() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null);
      Operation op2 = new Operation("operation2", 10, group, null);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(WorkCell.Status.PROCESSING, group.getStatus());
   }

   @Test
   void getStatusBlocked() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkCell stn = new WorkCell("stn.end");
      Operation op3 = new Operation("operation.end", 20, stn, null);
      Operation op1 = new Operation("operation1", 10, group, op3);
      Operation op2 = new Operation("operation2", 10, group, op3);
      group.assignOperation(op1);
      group.assignOperation(op2);
      stn.assignOperation(op3);

      group.process(10);
      stn.process(10);
      group.evalBlockedStatus();
      stn.evalBlockedStatus();

      assertEquals(WorkCell.Status.BLOCKED, group.getStatus());
   }

   @Test
   void getStatusBlockedPartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkCell stn = new WorkCell("stn.end");
      Operation op3 = new Operation("operation.end", 20, stn, null);
      Operation op1 = new Operation("operation1", 20, group, op3);
      Operation op2 = new Operation("operation2", 10, group, op3);
      group.assignOperation(op1);
      group.assignOperation(op2);
      stn.assignOperation(op3);

      group.process(10);
      stn.process(10);
      group.evalBlockedStatus();
      stn.evalBlockedStatus();

      assertEquals(WorkCell.Status.BLOCKED, group.getStatus());
   }

   @Test
   void getRequiredOperatorsIdle() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      assertEquals(0, group.getRequiredOperators());
   }

   @Test
   void getRequiredOperatorsWaitingForOperators() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 10);
      Operation op2 = new Operation("operation2", 10, group, null, 10);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(20, group.getRequiredOperators());
   }

   @Test
   void getRequiredOperatorsWaitingForOperatorsPartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 10);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperation(op1);
      group.assignOperation(op2);
      group.assignOperators(Arrays.asList(operator1, operator2));
      assertEquals(10, group.getRequiredOperators());
   }

   @Test
   void evalBlockedStatus() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation.end", 20, group2, null);
      Operation op3 = new Operation("operation.end", 20, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3);
      Operation op2 = new Operation("operation2", 10, group1, op3);
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group2.assignOperation(op3);
      group2.assignOperation(op4);
      group1.process(10);
      group2.process(10);
      assertTrue(group1.evalBlockedStatus());
      assertFalse(group2.evalBlockedStatus());

   }

   @Test
   void getRequiredOperatorsBlocked() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation.end", 20, group2, null);
      Operation op3 = new Operation("operation.end", 20, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3, 1);
      Operation op2 = new Operation("operation2", 10, group1, op3, 1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group2.assignOperation(op3);
      group2.assignOperation(op4);
      group1.assignOperators(Arrays.asList(operator1, operator2));
      group1.process(10);
      group2.process(10);
      group1.evalBlockedStatus();
      group2.evalBlockedStatus();
      assertEquals(0, group1.getRequiredOperators());
   }

   @Test
   void processIdle() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      assertEquals(0, group.process(10));
   }

   @Test
   void processWaitingForOperator() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(0, group.process(10));
   }

   @Test
   void processProcessing() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null);
      Operation op2 = new Operation("operation2", 10, group, null);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(5, group.process(5));
      assertEquals(WorkCell.Status.PROCESSING, group.getStatus());
   }

   @Test
   void processProcessingPartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null);
      group.assignOperation(op1);
      assertEquals(0, group.process(5));
      assertEquals(WorkCell.Status.IDLE, group.getStatus());
   }

   @Test
   void processProcessingEndOperation() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null);
      Operation op2 = new Operation("operation2", 10, group, null);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(10, group.process(10));
      assertEquals(WorkCell.Status.IDLE, group.getStatus());
   }

   @Test
   void processProcessingExceedCycleTime() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null);
      Operation op2 = new Operation("operation2", 10, group, null);
      group.assignOperation(op1);
      group.assignOperation(op2);
      assertEquals(10, group.process(20));
      assertEquals(WorkCell.Status.IDLE, group.getStatus());
   }

   @Test
   void processBlocked() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation.end", 20, group2, null);
      Operation op3 = new Operation("operation.end", 20, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3, 1);
      Operation op2 = new Operation("operation2", 10, group1, op3, 1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group2.assignOperation(op3);
      group2.assignOperation(op4);
      group1.assignOperators(Arrays.asList(operator1, operator2));
      group1.process(10);
      group2.process(10);
      group1.evalBlockedStatus();
      group2.evalBlockedStatus();

      assertEquals(0, group1.process(20));
   }

   @Test
   void assignOperatorsIdle() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operator operator = new Operator("opeartor");
      assertTrue(group.assignOperators(Arrays.asList(operator)).isEmpty());
   }

   @Test
   void assignOperatorsWaitingForOperators() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator = new Operator("opeartor");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator));
      assertEquals(1, assignedSet.size());
      assertTrue(assignedSet.contains(operator));
   }

   @Test
   void assignOperatorsWaitingForOperatorsExceed() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      Operator operator3 = new Operator("operator3");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator1, operator2, operator3));
      assertEquals(2, assignedSet.size());
   }

   @Test
   void assignOperatorsWaitingForOperatorsLess() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 2);
      Operation op2 = new Operation("operation2", 10, group, null, 2);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator1));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void assignOperatorsProcessing() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      group.process(5);

      Operator operator3 = new Operator("opeartor3");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator3));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void assignOperatorsBlocked() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation.end", 20, group2, null);
      Operation op3 = new Operation("operation.end", 20, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3, 1);
      Operation op2 = new Operation("operation2", 10, group1, op3, 1);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group2.assignOperation(op3);
      group2.assignOperation(op4);
      group1.assignOperators(Arrays.asList(operator1, operator2));
      group1.process(10);
      group2.process(10);
      group1.evalBlockedStatus();
      group2.evalBlockedStatus();

      Operator operator3 = new Operator("opeartor3");
      Set<Operator> assignedSet = group1.assignOperators(Arrays.asList(operator3));
      assertTrue(assignedSet.isEmpty());
   }

   @Test
   void unassignOperators() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 10, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      group.process(10);
      Set<Operator> unassignSet = group.unassignOperators();
      assertEquals(2, unassignSet.size());
      assertTrue(unassignSet.contains(operator1));
      assertTrue(unassignSet.contains(operator2));
   }

   @Test
   void unassignOperatorsPartial() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 20, group, null, 1);
      Operation op2 = new Operation("operation2", 10, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      group.process(10);
      Set<Operator> unassignSet = group.unassignOperators();
      assertEquals(1, unassignSet.size());
      assertTrue(unassignSet.contains(operator2));
   }

   @Test
   void unassignOperatorsProcessing() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 20, group, null, 1);
      Operation op2 = new Operation("operation2", 20, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      group.process(10);
      assertTrue(group.unassignOperators().isEmpty());
   }

   @Test
   void getAssignedOperators() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op1 = new Operation("operation1", 20, group, null, 1);
      Operation op2 = new Operation("operation2", 20, group, null, 1);
      group.assignOperation(op1);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      assertEquals(2, group.getAssignedOperators());
   }

   @Test
   void getAssignedOperatorsIdle() {
      WorkCell wst1 = new WorkCell("group.wst1");
      WorkCell wst2 = new WorkCell("group.wst2");
      WorkGroup group = new WorkGroup("group", new HashSet<>(Arrays.asList(wst1, wst2)));
      assertEquals(0, group.getAssignedOperators());
   }

}