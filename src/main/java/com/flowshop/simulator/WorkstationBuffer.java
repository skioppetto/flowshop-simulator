package com.flowshop.simulator;

import java.util.concurrent.ArrayBlockingQueue;

import lombok.Getter;

public class WorkstationBuffer extends ObservableSimObject {

   public enum Type {
      BEFORE, AFTER
   };

   @Getter
   private final BufferedWorkstation workstation;
   
   private final ArrayBlockingQueue<Operation> queue;
   @Getter
   private final Type type;
   @Getter
   private final int maxSize;

   public WorkstationBuffer(BufferedWorkstation workstation, Type type, int size) {
      if (size > 0)
         this.queue = new ArrayBlockingQueue<>(size);
      else
         this.queue = null;
      this.type = type;
      this.maxSize = size;
      this.workstation = workstation;
   }

   public boolean enqueue(Operation op) {
      if (maxSize == 0 || queue.contains(op))
         return false;
      boolean enqueued = queue.offer(op);
      if (enqueued)
         notifySimObjectObservers();
      return enqueued;
   }

   public void remove(Operation op) {
      if (maxSize > 0)
         if (queue.removeIf(queueOp -> queueOp.equals(op)))
            notifySimObjectObservers();
   }

   public Operation dequeue() {
      if (maxSize == 0)
         return null;
      Operation op = queue.poll();
      if (op != null)
         notifySimObjectObservers();
      return op;
   }

   public Operation peek() {
      if (maxSize == 0)
         return null;
      return queue.peek();
   }

   public int size() {
      if (maxSize > 0)
         return queue.size();
      return 0;
   }

   public boolean contains(Operation op2) {
      if (maxSize == 0)
         return false;
      return queue.contains(op2);
   }

   public boolean isEmpty() {
      if (maxSize == 0)
         return true;
      return queue.isEmpty();
   }

}
