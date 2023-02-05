package com.flowshop.simulator;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import lombok.Getter;

public class WorkstationBuffer extends ObservableSimObject {

   public enum Type {
      BEFORE, AFTER
   };

   @Getter
   private final Queue<Operation> queue;
   @Getter
   private final Type type;
   @Getter
   private final int size;

   public WorkstationBuffer(Type type, int size) {
      if (size > 0)
         this.queue = new ArrayBlockingQueue<>(size);
      else
         this.queue = null;
      this.type = type;
      this.size = size;
   }

   public boolean enqueue(Operation op) {
      if (size == 0)
         return false;
      boolean enqueued = queue.offer(op);
      if (enqueued)
         notifySimObjectObservers();
      return enqueued;
   }

   public Operation dequeue() {
      if (size == 0)
         return null;
      Operation op = queue.poll();
      if (op != null)
         notifySimObjectObservers();
      return op;
   }

   public Operation peek() {
      if (size == 0)
         return null;
      return queue.peek();
   }

   public int size() {
      if (size > 0)
         return queue.size();
      return 0;
   }

   public boolean contains(Operation op2) {
      if (size == 0)
         return false;
      return queue.contains(op2);
   }

   public boolean isEmpty() {
      if (size == 0)
         return true;
      return queue.isEmpty();
   }

}
