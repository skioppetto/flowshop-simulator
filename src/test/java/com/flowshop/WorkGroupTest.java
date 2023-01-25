package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import com.flowshop.WorkCell.Status;

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
      group.assignOperators(operator);
      assertEquals(2, group.getRequiredOperators());

      Operator operator2 = new Operator("oper2");
      group.assignOperators(operator2);
      assertEquals(1, group.getRequiredOperators());

      Operator operator3 = new Operator("oper3");
      group.assignOperators(operator3);
      assertEquals(0, group.getRequiredOperators());

   }

   @Test
   // assign operators will try to bind operators to each workstation that require
   // them: WAITING_FOR_OPERATOR, PROCESSING state, IDLE and BLOCKED will be
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
      group.assignOperators(operator);
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
      group.assignOperators(operator1, operator2);
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
      group.assignOperators(operator1, operator2);
      assertEquals(WorkCell.Status.PROCESSING, cell1.getStatus());
      assertEquals(WorkCell.Status.PROCESSING, cell2.getStatus());
      assertEquals(1, cell1.getAssignedOperators());
      assertEquals(1, cell2.getAssignedOperators());
      assertNull(operator3.getAssignedWorkstation());
   }

   @Test
   // let's suppose that this method will return operators count assigned to all
   // workcells
   void getAssignedOperators() {
      WorkGroup group = new WorkGroup("grp1",
            new HashSet<>(Arrays.asList(new WorkCell("grp1.cell1"), new WorkCell("grp1.cell2"))));

      Operation op = new Operation("op", 10, group, null);
      group.assignOperation(op);
   }
}
