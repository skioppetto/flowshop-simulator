package com.flowshop.reader;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;

public class SimulationBuilderTest {

   private OrderRequirements buildSimpleOrderRequirement(String orderId) {
      OrderRequirements ord1 = new OrderRequirements();
      ord1.setOrderId(orderId);
      OperationRequrements op1 = new OperationRequrements();
      OperationRequrements op2 = new OperationRequrements();
      op1.setCycleTime(10);
      op1.setOperationId(orderId + ".op1");
      op1.setWorkstation("wrk1");
      op2.setCycleTime(10);
      op2.setOperationId(orderId + ".op2");
      op2.setWorkstation("wrk2");
      ord1.setOperations(new OperationRequrements[] { op1, op2 });
      return ord1;
   }

   private OrderRequirements buildComplexOrderRequirement(String orderId) {
      OrderRequirements ord1 = new OrderRequirements();
      ord1.setOrderId(orderId);
      OperationRequrements op1 = new OperationRequrements();
      OperationRequrements op2 = new OperationRequrements();
      OperationRequrements op3 = new OperationRequrements();
      OperationRequrements op4 = new OperationRequrements();
      op1.setCycleTime(10);
      op1.setOperationId(orderId + ".op1");
      op1.setWorkstation("wrk1");
      op2.setCycleTime(10);
      op2.setOperationId(orderId + ".op2");
      op2.setWorkstation("wrk2");
      op3.setCycleTime(10);
      op3.setOperationId(orderId + ".op3");
      op3.setWorkstation("wrk3");
      op4.setCycleTime(10);
      op4.setOperationId(orderId + ".op4");
      op4.setWorkstation("wrk4");
      ord1.setOperations(new OperationRequrements[] { op1, op2, op3, op4 });
      return ord1;
   }

   @Test
   void operationsCreationTest() {
      IConfigurationReader configMock = mock(IConfigurationReader.class);
      expect(configMock.getWorkstationsRequirements()).andReturn(null);
      expect(configMock.getOperatorRequirements()).andReturn(null);
      expect(configMock.getOrdersRequirements()).andReturn(new OrderRequirements[] {
            buildComplexOrderRequirement("firstOrder"), buildSimpleOrderRequirement("secondOrder") });
      replay(configMock);
      Simulation sim = (new SimulationBuilder(configMock)).build();
      verify(configMock);
      assertNotNull(sim);
      Order ord1 = sim.getOrders().get(0);
      assertEquals("firstOrder", ord1.getId());
      assertNull(ord1.getOperations().get(3).getNextOperation());
      assertEquals(ord1.getOperations().get(3), ord1.getOperations().get(2).getNextOperation());
      assertEquals(ord1.getOperations().get(2), ord1.getOperations().get(1).getNextOperation());
      assertEquals(ord1.getOperations().get(1), ord1.getOperations().get(0).getNextOperation());
   }

   @Test
   void operatorsCreationTest() {
      OperatorRequirements reqOp1 = new OperatorRequirements();
      reqOp1.setName("operator1");
      reqOp1.setGroup("users");
      OperatorRequirements reqOp2 = new OperatorRequirements();
      reqOp2.setName("operator2");
      reqOp2.setGroup("users");
      IConfigurationReader configMock = mock(IConfigurationReader.class);
      expect(configMock.getWorkstationsRequirements()).andReturn(null);
      expect(configMock.getOperatorRequirements()).andReturn(new OperatorRequirements[] { reqOp1, reqOp2 });
      expect(configMock.getOrdersRequirements()).andReturn(new OrderRequirements[] {
            buildSimpleOrderRequirement("firstOrder"), buildSimpleOrderRequirement("secondOrder") });
      replay(configMock);
      Simulation sim = (new SimulationBuilder(configMock)).build();
      verify(configMock);
      assertNotNull(sim);
      assertEquals(2, sim.getAvailableOperators().size());
   }

   @Test
   // if no configuration about workstations is set through
   // workstationrequirements, all the stations references on ordersrequirements
   // shouldbe automatically created as simple workcell
   void workcellCreationTest() {
      IConfigurationReader configMock = mock(IConfigurationReader.class);
      expect(configMock.getWorkstationsRequirements()).andReturn(null);
      expect(configMock.getOperatorRequirements()).andReturn(null);
      expect(configMock.getOrdersRequirements()).andReturn(new OrderRequirements[] {
            buildSimpleOrderRequirement("firstOrder"), buildSimpleOrderRequirement("secondOrder") });
      replay(configMock);
      Simulation sim = (new SimulationBuilder(configMock)).build();
      verify(configMock);
      assertNotNull(sim);
      assertNotNull(sim.getWorkstations());
      assertEquals(2, sim.getWorkstations().size());
      for (Workstation w : sim.getWorkstations())
         assertEquals(WorkCell.class, w.getClass());
   }

   @Test
   // to build workgroups I need to get some details from workstationRequirements
   void workgroupCreationTest() {
      WorkstationRequirements workgroupRequirement = new WorkstationRequirements();
      workgroupRequirement.setName("wrk1");
      workgroupRequirement.setWorkGroupCells(5);
      IConfigurationReader configMock = mock(IConfigurationReader.class);
      expect(configMock.getWorkstationsRequirements())
            .andReturn(new WorkstationRequirements[] { workgroupRequirement });
      expect(configMock.getOperatorRequirements()).andReturn(null);
      expect(configMock.getOrdersRequirements()).andReturn(new OrderRequirements[] {
            buildSimpleOrderRequirement("firstOrder"), buildSimpleOrderRequirement("secondOrder") });
      replay(configMock);
      Simulation sim = (new SimulationBuilder(configMock)).build();
      verify(configMock);
      assertNotNull(sim);
      assertNotNull(sim.getWorkstations());
      assertEquals(2, sim.getWorkstations().size());
      Set<String> uniqueCells = new HashSet<>();
      for (Workstation w : sim.getWorkstations()) {
         if (w.getId().equals("wrk1")) {
            assertEquals(WorkGroup.class, w.getClass());
            assertEquals(5, ((WorkGroup) w).getWorkCells().size());
            for (WorkCell cell : ((WorkGroup) w).getWorkCells())
               // I could also chek naming wor workcells id?? should be "wrk1.01"... let's just
               // look at unique names
               assertTrue(uniqueCells.add(cell.getId()));
         }
      }
   }

   @Test
   void bufferedWorkstationCreationTest() {
      WorkstationRequirements workgroupRequirement = new WorkstationRequirements();
      workgroupRequirement.setName("wrk1");
      workgroupRequirement.setBufferAfter(5);
      IConfigurationReader configMock = mock(IConfigurationReader.class);
      expect(configMock.getWorkstationsRequirements())
            .andReturn(new WorkstationRequirements[] { workgroupRequirement });
      expect(configMock.getOperatorRequirements()).andReturn(null);
      expect(configMock.getOrdersRequirements()).andReturn(new OrderRequirements[] {
            buildSimpleOrderRequirement("firstOrder"), buildSimpleOrderRequirement("secondOrder") });
      replay(configMock);
      Simulation sim = (new SimulationBuilder(configMock)).build();
      verify(configMock);
      assertNotNull(sim);
      assertNotNull(sim.getWorkstations());
      assertEquals(2, sim.getWorkstations().size());
      for (Workstation w : sim.getWorkstations()) {
         if (w.getId().equals("wrk1")) {
            assertEquals(BufferedWorkstation.class, w.getClass());
            assertEquals(5, ((BufferedWorkstation) w).getAfterBufferMaxSize());
         }
      }
   }

}
