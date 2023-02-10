package com.flowshop.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.flowshop.SimulatorTestUtils;
import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.Workstation;
import com.flowshop.simulator.WorkstationBuffer;

public class WorkstationBufferListenerTest {

   Queue<Object> queue = new LinkedList<>();

   @Test
   void enqueueOnBeforeBuffer() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(0l);
      replay(timer);
      Workstation cell = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell, 2, 2);
      WorkstationBufferListener observer = new WorkstationBufferListener(timer, queue);
      bw.getAfterBuffer().addSimObjectObserver(observer);
      bw.getBeforeBuffer().addSimObjectObserver(observer);
      Operation op2 = new Operation("ope2", 10, bw, null);
      Operation op1 = new Operation("ope1", 20, bw, null);
      bw.assignOperation(op1);
      bw.assignOperation(op2);
      verify(timer);
      WorkstationBufferEvent event = (WorkstationBufferEvent) queue.poll();
      assertNotNull(event);
      assertEquals("cell1", event.getWorkstationId());
      assertEquals(WorkstationBuffer.Type.BEFORE, event.getBufferType());
      assertEquals(WorkstationBufferEvent.EventType.ENQUEUE, event.getEventType());
      assertEquals(1, event.getSize());
      assertEquals(0, event.getTime());
      assertNull((WorkstationBufferEvent) queue.poll());
   }

   @Test
   void dequeueOnBeforeBuffer() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(0l);
      expect(timer.getSimulationTime()).andReturn(10l);
      replay(timer);
      Workstation cell = new WorkCell("cell1");
      BufferedWorkstation bw = new BufferedWorkstation(cell, 2, 2);
      WorkstationBufferListener observer = new WorkstationBufferListener(timer, queue);
      bw.getAfterBuffer().addSimObjectObserver(observer);
      bw.getBeforeBuffer().addSimObjectObserver(observer);
      Operation op2 = new Operation("ope2", 20, bw, null);
      Operation op1 = new Operation("ope1", 10, bw, null);
      bw.assignOperation(op1);
      bw.assignOperation(op2);
      SimulatorTestUtils.simulateProcess(10, bw);
      verify(timer);
      WorkstationBufferEvent event;
      event = (WorkstationBufferEvent) queue.poll();
      assertNotNull(event);
      assertEquals("cell1", event.getWorkstationId());
      assertEquals(WorkstationBuffer.Type.BEFORE, event.getBufferType());
      assertEquals(WorkstationBufferEvent.EventType.ENQUEUE, event.getEventType());
      assertEquals(1, event.getSize());
      assertEquals(0, event.getTime());
      event = (WorkstationBufferEvent) queue.poll();
      assertNotNull(event);
      assertEquals("cell1", event.getWorkstationId());
      assertEquals(WorkstationBuffer.Type.BEFORE, event.getBufferType());
      assertEquals(WorkstationBufferEvent.EventType.DEQUEUE, event.getEventType());
      assertEquals(0, event.getSize());
      assertEquals(10, event.getTime());
      assertNull((WorkstationBufferEvent) queue.poll());
   }
}
