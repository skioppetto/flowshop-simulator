package com.flowshop.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.WorkCell;

public class OperatorListenerTest {

   Queue<Object> queue = new LinkedList<>();

   @Test
   void idleStatusTest() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      replay(timer);
      Operator operator = new Operator("operator1");
      OperatorListener operatorListener = new OperatorListener(timer, queue);
      operator.addSimObjectObserver(operatorListener);
      WorkCell cell = new WorkCell("cell");
      Operation op = new Operation("op1", 10, cell, null, 1);
      cell.assignOperation(op);
      cell.assignOperators(Arrays.asList(operator));
      verify(timer);
      OperatorEvent event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals(Operator.Status.IDLE, event.getStatus());
      assertEquals("operator1", event.getOperatorId());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertNull(event.getOperationId());
      assertNull(event.getWorkstationId());
      assertNull(event.getOrderId());
      assertNull(queue.poll());
   }

   @Test
   void idleStatusMultipleOperatorsTest() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l).times(2);
      expect(timer.getSimulationTime()).andReturn(20l).times(2);
      replay(timer);
      Operator operator = new Operator("operator1");
      Operator operator2 = new Operator("operator2");
      OperatorListener operatorListener = new OperatorListener(timer, queue);
      operator.addSimObjectObserver(operatorListener);
      operator2.addSimObjectObserver(operatorListener);
      WorkCell cell = new WorkCell("cell");
      Operation op = new Operation("op1", 10, cell, null, 2);
      cell.assignOperation(op);
      cell.assignOperators(Arrays.asList(operator, operator2));
      verify(timer);
      OperatorEvent event;
      event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals(Operator.Status.IDLE, event.getStatus());
      assertEquals("operator1", event.getOperatorId());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertNull(event.getOperationId());
      assertNull(event.getWorkstationId());
      assertNull(event.getOrderId());
      event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals(Operator.Status.IDLE, event.getStatus());
      assertEquals("operator2", event.getOperatorId());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertNull(event.getOperationId());
      assertNull(event.getWorkstationId());
      assertNull(event.getOrderId());
      assertNull(queue.poll());
   }

   @Test
   void processingStatusTest() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(30l);
      expect(timer.getSimulationTime()).andReturn(50l);
      replay(timer);
      Operator operator = new Operator("operator1");
      OperatorListener operatorListener = new OperatorListener(timer, queue);
      operator.addSimObjectObserver(operatorListener);
      WorkCell cell = new WorkCell("cell");
      WorkCell cell2 = new WorkCell("cell2");
      Operation op = new Operation("op1", 10, cell, null, 1);
      Operation op2 = new Operation("op2", 10, cell2, null, 1);
      cell.assignOperation(op);
      // assign operators, should enqueue the idle status
      cell.assignOperators(Arrays.asList(operator));
      cell.process(10);
      // unassign operators, should enqueue the processing status
      cell.unassignOperators();
      cell2.assignOperation(op2);
      // assign operators, should enqueue the idle status
      cell2.assignOperators(Arrays.asList(operator));
      verify(timer);
      OperatorEvent event;
      event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals("operator1", event.getOperatorId());
      assertEquals(Operator.Status.IDLE, event.getStatus());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertNull(event.getOperationId());
      assertNull(event.getWorkstationId());
      assertNull(event.getOrderId());
      event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals(Operator.Status.PROCESSING, event.getStatus());
      assertEquals("operator1", event.getOperatorId());
      assertEquals(10l, event.getDuration());
      assertEquals(20l, event.getStartTime());
      assertEquals("op1", event.getOperationId());
      assertEquals("cell", event.getWorkstationId());
      // assertNull(event.getOrderId());
      event = (OperatorEvent) queue.poll();
      assertNotNull(event);
      assertEquals(Operator.Status.IDLE, event.getStatus());
      assertEquals("operator1", event.getOperatorId());
      assertEquals(20l, event.getDuration());
      assertEquals(30l, event.getStartTime());
      assertNull(event.getOperationId());
      assertNull(event.getWorkstationId());
      assertNull(event.getOrderId());
      assertNull(queue.poll());
   }

}
