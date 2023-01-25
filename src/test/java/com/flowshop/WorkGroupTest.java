package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class WorkGroupTest {

   // let's focus on which is the expected behavoir for a work group looking at the
   // methods implemented for a single workcell

   @Test
   // operation will be assigned to the first idle workcell
   void assignAnOperation() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));

      Operation op1 = new Operation("op1", 10, group, null);
      assertTrue(group.assignOperation(op1));
      assertTrue(op1.equals(cell1.getCurrentOperation()) || op1.equals(cell2.getCurrentOperation()));
   }

   @Test
   // operation will be assigned to the first idle workcell
   void assignAnOperationForEachCell() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));

      Operation op1 = new Operation("op1", 10, group, null);
      Operation op2 = new Operation("op2", 10, group, null);
      assertTrue(group.assignOperation(op1));
      assertTrue(group.assignOperation(op2));
      assertTrue(op1.equals(cell1.getCurrentOperation()) || op1.equals(cell2.getCurrentOperation()));
      assertTrue(op2.equals(cell1.getCurrentOperation()) || op2.equals(cell2.getCurrentOperation()));
   }

   @Test
   // operation won't be assigned and the method assignOperation will return false
   void exceedAssignableOperations() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));

      Operation op1 = new Operation("op1", 10, group, null);
      Operation op2 = new Operation("op2", 10, group, null);
      Operation op3 = new Operation("op3", 10, group, null);
      assertTrue(group.assignOperation(op1));
      assertTrue(group.assignOperation(op2));
      assertFalse(group.assignOperation(op3));
      assertTrue(op1.equals(cell1.getCurrentOperation()) || op1.equals(cell2.getCurrentOperation()));
      assertTrue(op2.equals(cell1.getCurrentOperation()) || op2.equals(cell2.getCurrentOperation()));
   }

   @Test
   // required operators is alway equal to the needed operators by the workgroup,
   // sum of required operators for WAITING_FOR_OPERATOR workcells.
   void requiredOperators() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));
      Operation op1 = new Operation("op1", 10, group, null);
      op1.setRequiredOperators(2);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(1);
      assertTrue(group.assignOperation(op1));
      assertTrue(group.assignOperation(op2));
      assertEquals(3, group.getRequiredOperators());

      Operator operator = new Operator("oper1");
      Operator operator2 = new Operator("oper2");
      Operator operator3 = new Operator("oper3");

      Set<Operator> assignSet = group.assignOperators(Arrays.asList(operator, operator2, operator3));
      assertEquals(3, assignSet.size());
      assertTrue(assignSet.contains(operator3));
      assertTrue(assignSet.contains(operator2));
      assertTrue(assignSet.contains(operator));
      assertTrue(group.getWorkCells().contains(operator3.getAssignedWorkstation()));
      assertTrue(group.getWorkCells().contains(operator2.getAssignedWorkstation()));
      assertTrue(group.getWorkCells().contains(operator.getAssignedWorkstation()));
      assertEquals(0, group.getRequiredOperators());

   }

   @Test
   // assign operators will try to bind operators to each workstation that require
   // them: WAITING_FOR_OPERATOR. PROCESSING state, IDLE and BLOCKED will be
   // ignored, in this way
   // operators will be available for stations that really needs them
   void assignOperatorsLess() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));
      Operation op1 = new Operation("op1", 10, group, null);
      op1.setRequiredOperators(1);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(1);
      assertTrue(group.assignOperation(op1));
      Operator operator = new Operator("oper1");
      WorkCell processingWorkstation = group.getWorkCells().stream()
            .filter(cell -> cell.getStatus().equals(WorkCell.Status.WAITING_FOR_OPERATOR)).findFirst().get();
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator));
      assertEquals(1, assignedSet.size());
      assertTrue(assignedSet.contains(operator));
      assertEquals(WorkCell.Status.PROCESSING, processingWorkstation.getStatus());
      assertEquals(1, processingWorkstation.getAssignedOperators());
      assertEquals(processingWorkstation, operator.getAssignedWorkstation());
   }

   @Test
   void assignOperatorsCorrect() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));
      Operation op1 = new Operation("op1", 10, group, null);
      op1.setRequiredOperators(1);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(1);
      assertTrue(group.assignOperation(op1));
      assertTrue(group.assignOperation(op2));
      Operator operator1 = new Operator("oper1");
      Operator operator2 = new Operator("oper2");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator1, operator2));
      assertEquals(2, assignedSet.size());
      assertTrue(assignedSet.contains(operator1));
      assertTrue(assignedSet.contains(operator2));
      assertEquals(WorkCell.Status.PROCESSING, cell1.getStatus());
      assertEquals(WorkCell.Status.PROCESSING, cell2.getStatus());
      assertEquals(1, cell1.getAssignedOperators());
      assertEquals(1, cell2.getAssignedOperators());
   }

   @Test
   void exceedAssignableOperators() {
      WorkCell cell1 = new WorkCell("grp1.cell1");
      WorkCell cell2 = new WorkCell("grp1.cell2");
      WorkGroup group = new WorkGroup("grp1", new HashSet<>(Arrays.asList(cell1, cell2)));
      Operation op1 = new Operation("op1", 10, group, null);
      op1.setRequiredOperators(1);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(1);
      assertTrue(group.assignOperation(op1));
      assertTrue(group.assignOperation(op2));
      Operator operator1 = new Operator("oper1");
      Operator operator2 = new Operator("oper2");
      Operator operator3 = new Operator("oper3");
      Set<Operator> assignedSet = group.assignOperators(Arrays.asList(operator1, operator2, operator3));
      assertEquals(2, assignedSet.size());
      assertEquals(WorkCell.Status.PROCESSING, cell1.getStatus());
      assertEquals(WorkCell.Status.PROCESSING, cell2.getStatus());
      assertEquals(1, cell1.getAssignedOperators());
      assertEquals(1, cell2.getAssignedOperators());
      assertTrue(operator3.getAssignedWorkstation() == null || operator2.getAssignedWorkstation() == null
            || operator1.getAssignedWorkstation() == null);
   }

   @Test
   // let's suppose that this method will return operators count assigned to all
   // workcells
   void getAssignedOperators() {
      WorkGroup group = new WorkGroup("grp1",
            new HashSet<>(Arrays.asList(new WorkCell("grp1.cell1"), new WorkCell("grp1.cell2"))));
      Operation op = new Operation("op1", 10, group, null);
      op.setRequiredOperators(1);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(2);
      group.assignOperation(op);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      Operator operator3 = new Operator("operator3");
      Set<Operator> assignedSet;
      assignedSet = group.assignOperators(Arrays.asList(operator1, operator2, operator3));
      assertEquals(3, assignedSet.size());
      assertEquals(3, group.getAssignedOperators());

   }

   @Test
   void unassignOperatorsDisabledWhenProcessing() {
      WorkGroup group = new WorkGroup("grp1",
            new HashSet<>(Arrays.asList(new WorkCell("grp1.cell1"), new WorkCell("grp1.cell2"))));
      Operation op = new Operation("op1", 10, group, null);
      op.setRequiredOperators(1);
      Operation op2 = new Operation("op2", 10, group, null);
      op2.setRequiredOperators(1);
      group.assignOperation(op);
      group.assignOperation(op2);
      Operator operator1 = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      group.assignOperators(Arrays.asList(operator1, operator2));
      assertTrue(group.unassignOperators().isEmpty());
      assertEquals(Operator.Status.PROCESSING, operator1.getStatus());
      assertEquals(Operator.Status.PROCESSING, operator2.getStatus());

   }
}
