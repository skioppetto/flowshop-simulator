package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.flowshop.SimulatorTestUtils.simulateProcess;
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
      simulateProcess(10, group, stn);
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

      simulateProcess(10, group, stn);
      assertEquals(WorkCell.Status.BLOCKED, group.getStatus());
   }

   @Test
   // this blocking condition is due to the single workcell blocking condition as the next operation is still running
   void evalBlockedStatusNextCellCondition() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("wst3");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op4 = new Operation("operation1.end", 30, wst3, null);
      Operation op3 = new Operation("operation2.end", 30, wst3, null);
      Operation op1 = new Operation("operation1", 10, group1, op3);
      Operation op2 = new Operation("operation2", 20, group1, op4);
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      wst3.assignOperation(op3);
      group1.process(10);
      wst3.process(10);
      assertTrue(group1.evalBlockedStatus());
      assertFalse(wst3.evalBlockedStatus());
   }

   @Test
   // this blocking condition is due to the workgroup blocking condition as op1 and op2 ends together and one of them will be blocked even if the next workstation is idle.
   // This is because there's no room in the next operation for both.
   void evalBlockedStatusConcurrentEndCondition() {
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation1.end", 30, group2, null);
      Operation op3 = new Operation("operation2.end", 30, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3);
      Operation op2 = new Operation("operation2", 10, group1, op4);
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      // I'm not assigning operations to the group2 so both workcells should be idle and op1 and op2 can go ahead
      group1.process(10);
      assertFalse(group1.evalBlockedStatus());
   }

   @Test
   // things change if the next workstation is a worgroup as the number of concurrent operation done that can go to the next workstation depend on the number of idle worcells on the next workgroup
   void evalBlockedStatusConcurrentEndNextIsAWorgroup1(){
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst5 = new WorkCell("group1.wst3");
      WorkCell wst3 = new WorkCell("group2.wst1");
      WorkCell wst4 = new WorkCell("group2.wst2");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2, wst5)));
      WorkGroup group2 = new WorkGroup("group2", new HashSet<>(Arrays.asList(wst3, wst4)));
      Operation op4 = new Operation("operation1.end", 30, group2, null);
      Operation op3 = new Operation("operation2.end", 30, group2, null);
      Operation op6 = new Operation("operation3.end", 20, group2, null);
      Operation op1 = new Operation("operation1", 10, group1, op3);
      Operation op2 = new Operation("operation2", 10, group1, op4);
      Operation op5 = new Operation("operation3", 10, group1, op6);
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group1.assignOperation(op5);
      group1.process(10);
      assertTrue(group1.evalBlockedStatus());
   }

   @Test
   // things change if the next workstation is a worgroup as the number of concurrent operation done that can go to the next workstation depend on the number of idle worcells on the next workgroup
   void evalBlockedStatusConcurrentEndNextIsAWorgroup2(){
      WorkCell wst1 = new WorkCell("group1.wst1");
      WorkCell wst2 = new WorkCell("group1.wst2");
      WorkCell wst3 = new WorkCell("wst3");
      WorkGroup group1 = new WorkGroup("group1", new HashSet<>(Arrays.asList(wst1, wst2)));
      Operation op4 = new Operation("operation1.end", 30, wst3, null);
      Operation op3 = new Operation("operation2.end", 30, wst3, null);
      Operation op1 = new Operation("operation1", 10, group1, op3);
      Operation op2 = new Operation("operation2", 10, group1, op4);
      group1.assignOperation(op1);
      group1.assignOperation(op2);
      group1.process(10);
      assertTrue(group1.evalBlockedStatus());
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
      simulateProcess(10, group1, group2);
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
      simulateProcess(10, group1, group2);
      
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
      simulateProcess(10, group);
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
      simulateProcess(10, group);
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
      simulateProcess(10, group);
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