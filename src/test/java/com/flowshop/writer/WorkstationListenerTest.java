package com.flowshop.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import com.flowshop.SimulatorTestUtils;
import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;

import static org.easymock.EasyMock.*;

public class WorkstationListenerTest {

   @Test
   void idleStatusRecordSingleWorkCell() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      replay(timer);
      Workstation ws = new WorkCell("cell1");
      WorkstationListener wl = new WorkstationListener(timer);
      ws.addSimObjectObserver(wl);
      Operation op = new Operation("ope1", 5, ws, null);
      ws.assignOperation(op);
      verify(timer);
      WorkstationEvent we = wl.dequeue();
      assertEquals("cell1", we.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we.getStatus());
      assertEquals(10l, we.getDuration());
      assertEquals(10l, we.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void idleStatusRecordMultipleWorkCells() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(40l);
      expect(timer.getSimulationTime()).andReturn(60l);
      replay(timer);
      Workstation ws1 = new WorkCell("cell1");
      Workstation ws2 = new WorkCell("cell2");
      WorkstationListener wl = new WorkstationListener(timer);
      ws1.addSimObjectObserver(wl);
      ws2.addSimObjectObserver(wl);
      Operation op1 = new Operation("ope1", 5, ws1, null);
      Operation op2 = new Operation("ope2", 5, ws2, null);
      ws1.assignOperation(op1);
      ws2.assignOperation(op2);
      verify(timer);
      WorkstationEvent we1 = wl.dequeue();
      assertEquals("cell1", we1.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we1.getStatus());
      assertEquals(30l, we1.getDuration());
      assertEquals(10l, we1.getStartTime());
      WorkstationEvent we2 = wl.dequeue();
      assertEquals("cell2", we2.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we2.getStatus());
      assertEquals(40l, we2.getDuration());
      assertEquals(20l, we2.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void waitingForOperatorsStatusRecordWorkCell() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(50l);
      replay(timer);
      Workstation ws = new WorkCell("cell1");
      WorkstationListener wl = new WorkstationListener(timer);
      ws.addSimObjectObserver(wl);
      Operation op = new Operation("ope1", 5, ws, null, 1);
      ws.assignOperation(op);
      ws.assignOperators(Arrays.asList(new Operator("operator")));
      verify(timer);
      WorkstationEvent we1 = wl.dequeue();
      assertEquals("cell1", we1.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we1.getStatus());
      assertEquals(10l, we1.getDuration());
      assertEquals(10l, we1.getStartTime());
      WorkstationEvent we2 = wl.dequeue();
      assertNotNull(we2);
      assertEquals("cell1", we2.getWorkstationId());
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, we2.getStatus());
      assertEquals(30l, we2.getDuration());
      assertEquals(20l, we2.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void waitingForOperatorStatusRecordMultipleWorkCells() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(40l);
      expect(timer.getSimulationTime()).andReturn(60l);
      expect(timer.getSimulationTime()).andReturn(100l);
      expect(timer.getSimulationTime()).andReturn(120l);
      replay(timer);
      Workstation ws1 = new WorkCell("cell1");
      Workstation ws2 = new WorkCell("cell2");
      WorkstationListener wl = new WorkstationListener(timer);
      ws1.addSimObjectObserver(wl);
      ws2.addSimObjectObserver(wl);
      Operation op1 = new Operation("ope1", 5, ws1, null, 1);
      Operation op2 = new Operation("ope2", 5, ws2, null, 1);
      ws1.assignOperation(op1);
      ws2.assignOperation(op2);
      ws1.assignOperators(Arrays.asList(new Operator("operator1")));
      ws2.assignOperators(Arrays.asList(new Operator("operator2")));
      verify(timer);
      WorkstationEvent we1 = wl.dequeue();
      assertEquals("cell1", we1.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we1.getStatus());
      assertEquals(30l, we1.getDuration());
      assertEquals(10l, we1.getStartTime());
      WorkstationEvent we2 = wl.dequeue();
      assertEquals("cell2", we2.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we2.getStatus());
      assertEquals(40l, we2.getDuration());
      assertEquals(20l, we2.getStartTime());
      WorkstationEvent we3 = wl.dequeue();
      assertEquals("cell1", we3.getWorkstationId());
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, we3.getStatus());
      assertEquals(60l, we3.getDuration());
      assertEquals(40l, we3.getStartTime());
      WorkstationEvent we4 = wl.dequeue();
      assertEquals("cell2", we4.getWorkstationId());
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, we4.getStatus());
      assertEquals(60l, we4.getDuration());
      assertEquals(60l, we4.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void processingStatusRecordWorkCell() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(50l);
      expect(timer.getSimulationTime()).andReturn(60l); // this will depend on the cycle time of the operation
      replay(timer);
      Workstation ws = new WorkCell("cell1");
      WorkstationListener wl = new WorkstationListener(timer);
      ws.addSimObjectObserver(wl);
      Operation op = new Operation("ope1", 10, ws, null, 1);
      ws.assignOperation(op);
      ws.assignOperators(Arrays.asList(new Operator("operator")));
      SimulatorTestUtils.simulateProcess(5, ws);
      SimulatorTestUtils.simulateProcess(5, ws);
      verify(timer);
      WorkstationEvent we1 = wl.dequeue();
      assertEquals("cell1", we1.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we1.getStatus());
      assertEquals(10l, we1.getDuration());
      assertEquals(10l, we1.getStartTime());
      WorkstationEvent we2 = wl.dequeue();
      assertNotNull(we2);
      assertEquals("cell1", we2.getWorkstationId());
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, we2.getStatus());
      assertEquals(30l, we2.getDuration());
      assertEquals(20l, we2.getStartTime());
      WorkstationEvent we3 = wl.dequeue();
      assertNotNull(we3);
      assertEquals("cell1", we3.getWorkstationId());
      assertEquals(Workstation.Status.PROCESSING, we3.getStatus());
      assertEquals(10l, we3.getDuration());
      assertEquals(50l, we3.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void blockedStatusRecordWorkCell() {
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l);
      expect(timer.getSimulationTime()).andReturn(20l);
      expect(timer.getSimulationTime()).andReturn(50l);
      expect(timer.getSimulationTime()).andReturn(60l).times(2); // one for process, one for eval
      expect(timer.getSimulationTime()).andReturn(80l); // this will depend on the cycle time of the next operation
      replay(timer);
      Workstation ws1 = new WorkCell("cell1");
      Workstation ws2 = new WorkCell("cell2");
      WorkstationListener wl = new WorkstationListener(timer);
      ws1.addSimObjectObserver(wl);
      Operation op2 = new Operation("ope2", 20, ws2, null);
      Operation op1 = new Operation("ope1", 10, ws1, op2, 1);
      ws1.assignOperation(op1);
      ws2.assignOperation(op2);
      ws1.assignOperators(Arrays.asList(new Operator("operator")));
      SimulatorTestUtils.simulateProcess(5, ws1, ws2);
      SimulatorTestUtils.simulateProcess(5, ws1, ws2);
      SimulatorTestUtils.simulateProcess(10, ws1, ws2);

      verify(timer);
      WorkstationEvent we1 = wl.dequeue();
      assertEquals("cell1", we1.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, we1.getStatus());
      assertEquals(10l, we1.getDuration());
      assertEquals(10l, we1.getStartTime());
      WorkstationEvent we2 = wl.dequeue();
      assertNotNull(we2);
      assertEquals("cell1", we2.getWorkstationId());
      assertEquals(Workstation.Status.WAITING_FOR_OPERATOR, we2.getStatus());
      assertEquals(30l, we2.getDuration());
      assertEquals(20l, we2.getStartTime());
      WorkstationEvent we3 = wl.dequeue();
      assertNotNull(we3);
      assertEquals("cell1", we3.getWorkstationId());
      assertEquals(Workstation.Status.PROCESSING, we3.getStatus());
      assertEquals(10l, we3.getDuration());
      assertEquals(50l, we3.getStartTime());
      WorkstationEvent we4 = wl.dequeue();
      assertNotNull(we4);
      assertEquals("cell1", we4.getWorkstationId());
      assertEquals(Workstation.Status.BLOCKED, we4.getStatus());
      assertEquals(20l, we4.getDuration());
      assertEquals(60l, we4.getStartTime());
      assertNull(wl.dequeue());
   }

   @Test
   void workGroupListener() {
      WorkCell cell1 = new WorkCell("wg1.cell1");
      WorkCell cell2 = new WorkCell("wg1.cell2");
      WorkGroup wg = new WorkGroup("wg1", new HashSet<>(Arrays.asList(cell1, cell2)));
      ISimulationTimer timer = mock(ISimulationTimer.class);
      expect(timer.getSimulationTime()).andReturn(10l); // start idle for all cells
      expect(timer.getSimulationTime()).andReturn(20l); // end idle for cell2
      expect(timer.getSimulationTime()).andReturn(20l); // end idle for cell1
      expect(timer.getSimulationTime()).andReturn(30l); // end progress for op1 (+10 of simulation process equals to
                                                        // cycle time) assigned to cell2
      replay(timer);
      WorkstationListener wl = new WorkstationListener(timer);
      wg.addSimObjectObserver(wl);
      Operation op1 = new Operation("op1", 10, wg, null);
      Operation op2 = new Operation("op2", 20, wg, null);
      wg.assignOperation(op1);
      wg.assignOperation(op2);
      SimulatorTestUtils.simulateProcess(10, wg);
      verify(timer);
      WorkstationEvent event;
      event = wl.dequeue();
      assertNotNull(event);
      assertEquals("wg1.cell2", event.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, event.getStatus());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertEquals("wg1", event.getWorkGroupId());
      event = wl.dequeue();
      assertNotNull(event);
      assertEquals("wg1.cell1", event.getWorkstationId());
      assertEquals(Workstation.Status.IDLE, event.getStatus());
      assertEquals(10l, event.getDuration());
      assertEquals(10l, event.getStartTime());
      assertEquals("wg1", event.getWorkGroupId());
      event = wl.dequeue();
      assertNotNull(event);
      assertEquals("wg1.cell2", event.getWorkstationId());
      assertEquals(Workstation.Status.PROCESSING, event.getStatus());
      assertEquals(10l, event.getDuration());
      assertEquals(20l, event.getStartTime());
      assertEquals("wg1", event.getWorkGroupId());
   }
}
