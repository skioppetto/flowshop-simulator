package com.flowshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.easymock.EasyMock.*;
import org.junit.jupiter.api.Test;

public class OperationTest {

   @Test
   void statusToDoAtCreation() {
      Operation op = new Operation("opId", 100l, null, null);
      assertEquals(Operation.Status.TODO, op.getStatus());
   }

   @Test
   void statusProgressAfterStart() {
      Operation op = new Operation("opId", 100l, null, null);
      op.start();
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void statusProgressAfterProcess() {
      Operation op = new Operation("opId", 100l, null, null);
      op.setProcessedTime(20);
      assertEquals(Operation.Status.PROGRESS, op.getStatus());
   }

   @Test
   void statusDone() {
      Operation op = new Operation("opId", 100l, null, null);
      op.setProcessedTime(100);
      assertEquals(Operation.Status.DONE, op.getStatus());
   }

   @Test
   // if required operators is not set is expected that no operators are required
   // to run the operation
   void defaultRequiredOperators() {
      Operation op = new Operation("opId", 50, null, null);
      assertEquals(0, op.getRequiredOperators());
   }

   @Test
   void statusBlocked() {
      Operation op = new Operation("opId", 100l, null, null);
      op.setProcessedTime(100);
      op.setBlocked(true);
      assertEquals(Operation.Status.BLOCKED, op.getStatus());
   }

   @Test
   void notifyOnStart() {
      Operation op = new Operation("opId", 100l, null, null);
      SimObjectObserver observerMock = mock(SimObjectObserver.class);
      op.addSimObjectObserver(observerMock);

      observerMock.onChange(op);
      expectLastCall();
      replay(observerMock);

      op.start();

      verify(observerMock);
   }

   @Test
   void notifyOnProgress() {
      Operation op = new Operation("opId", 100l, null, null);
      SimObjectObserver observerMock = mock(SimObjectObserver.class);
      op.addSimObjectObserver(observerMock);

      observerMock.onChange(op);
      expectLastCall().times(2);
      replay(observerMock);

      op.setProcessedTime(10);
      op.setProcessedTime(20);

      verify(observerMock);
   }

   @Test
   void skipNotifyOnNoProgress() {
      Operation op = new Operation("opId", 100l, null, null);
      SimObjectObserver observerMock = mock(SimObjectObserver.class);
      op.addSimObjectObserver(observerMock);

      observerMock.onChange(op);
      expectLastCall().once();
      replay(observerMock);

      op.setProcessedTime(10);
      op.setProcessedTime(10);

      verify(observerMock);
   }

   @Test
   void notifyOnBlocked() {
      Operation op = new Operation("opId", 100l, null, null);
      SimObjectObserver observerMock = mock(SimObjectObserver.class);
      op.addSimObjectObserver(observerMock);

      observerMock.onChange(op);
      expectLastCall().times(2);
      replay(observerMock);

      op.setBlocked(true);
      op.setBlocked(false);

      verify(observerMock);
   }

   @Test
   void skipNotifyOnBlockedUnchanged() {
      Operation op = new Operation("opId", 100l, null, null);
      SimObjectObserver observerMock = mock(SimObjectObserver.class);
      op.addSimObjectObserver(observerMock);

      observerMock.onChange(op);
      expectLastCall().once();
      replay(observerMock);

      op.setBlocked(true);
      op.setBlocked(true);

      verify(observerMock);
   }

}
