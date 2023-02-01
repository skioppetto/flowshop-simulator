package com.flowshop.simulator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import lombok.Getter;

public class BufferedWorkstation extends Workstation implements SimObjectObserver {

   private final Workstation workstation;
   @Getter
   private final int afterBufferMaxSize;
   @Getter
   private final int beforeBufferMaxSize;
   @Getter
   private final LinkedList<Operation> beforeBuffer = new LinkedList<>();
   @Getter
   private final LinkedList<Operation> afterBuffer = new LinkedList<>();

   public BufferedWorkstation(Workstation cell, int afterSize, int beforeSize) {
      this.workstation = cell;
      this.afterBufferMaxSize = afterSize;
      this.beforeBufferMaxSize = beforeSize;
   }

   // return true if the workcell is not blocked and it's not needed any
   // movement to buffer or if the workcell is blocked and the operation is
   // correctly moved to buffer
   private boolean tryMoveBlockedOperationToBuffer(WorkCell cell) {
      if (!cell.getStatus().equals(Workstation.Status.BLOCKED))
         return true;
      else if (afterBuffer.size() < afterBufferMaxSize) {
         afterBuffer.offer(cell.unassignOperation());
         return true;
      } else
         return false;
   }

   private boolean tryMoveBlockedOperationsToBuffer() {
      // this is dirty: I need to free the workcell from the current operation or it
      // appears blocked even if the operation is stored into the buffer.
      // if I'm able to move all blocked operations to buffer the evalBlockedStatus
      // should return false
      if (workstation instanceof WorkCell) {
         return tryMoveBlockedOperationToBuffer((WorkCell) workstation);
      } else if (workstation instanceof WorkGroup) {
         boolean allMoved = true;
         for (WorkCell cell : ((WorkGroup) workstation).getWorkCells())
            allMoved &= tryMoveBlockedOperationToBuffer(cell);
         return allMoved;
      } else {
         return false;
      }
   }

   public boolean assignOperation(Operation op) {
      boolean assignOperation = workstation.assignOperation(op);
      if (assignOperation && op.getNextOperation() != null) {
         op.getNextOperation().addSimObjectObserver(this);
      } else if (!assignOperation && beforeBuffer.size() < beforeBufferMaxSize) {
         beforeBuffer.offer(op);
         return true;
      }
      return assignOperation;
   }

   public Workstation.Status getStatus() {
      return workstation.getStatus();
   }

   public long process(long i) {
      long processTime = workstation.process(i);
      return processTime;
   }

   public boolean evalBlockedStatus() {
      boolean eval = workstation.evalBlockedStatus();
      if (eval)
         // if I'm able to move all blocked operations to an after buffer the workstation
         // will no longer be blocked
         eval = !tryMoveBlockedOperationsToBuffer();
      // try to flush before buffer. This will happen if assign operation will permit
      // it
      while (beforeBuffer.peek() != null) {
         if (workstation.assignOperation(beforeBuffer.peek()))
            beforeBuffer.poll();
      }
      return eval;
   }

   public Set<Operator> assignOperators(Collection<? extends Operator> operators) {
      return workstation.assignOperators(operators);
   }

   public Set<Operator> unassignOperators() {
      return workstation.unassignOperators();
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof Operation) {
         Operation nextOperation = (Operation) observableSimObject;
         if (nextOperation.getStatus().equals(Operation.Status.PROGRESS)) {
            afterBuffer.removeIf(op -> nextOperation.equals(op.getNextOperation()));
            nextOperation.removeSimObjectObserver(this);
         }
      }
   }

   @Override
   public String getId() {
      return workstation.getId();
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
      
   }

   

}
