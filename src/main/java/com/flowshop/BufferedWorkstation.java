package com.flowshop;

import java.util.Collection;
import java.util.Set;

import com.flowshop.WorkstationBuffer.Type;

import lombok.Getter;

public class BufferedWorkstation implements Workstation {

   private final Workstation workstation;
   @Getter
   private final int afterBufferMaxSize;
   @Getter
   private final int beforeBufferMaxSize;
   @Getter
   private final WorkstationBuffer beforeBuffer = new WorkstationBuffer(Type.BEFORE, this);
   @Getter
   private final WorkstationBuffer afterBuffer = new WorkstationBuffer(Type.AFTER, this);

   public BufferedWorkstation(Workstation cell, int afterSize, int beforeSize) {
      this.workstation = cell;
      this.afterBufferMaxSize = afterSize;
      this.beforeBufferMaxSize = beforeSize;
   }

   // return true if the workcell is not blocked and it's not needed any
   // movement to buffer or if the workcell is blocked and the operation is
   // correctly moved to buffer
   private boolean tryMoveBlockedOperationToBuffer(WorkCell cell) {
      if (!cell.getStatus().equals(WorkCell.Status.BLOCKED))
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
      if (!assignOperation && beforeBuffer.size() < beforeBufferMaxSize) {
         beforeBuffer.offer(op);
         return true;
      }
      return assignOperation;
   }

   public WorkCell.Status getStatus() {
      return workstation.getStatus();
   }

   public long process(long i) {
      long processTime = workstation.process(i);
      if (WorkCell.Status.IDLE.equals(workstation.getStatus())) {
         Operation waitingOperation = beforeBuffer.poll();
         if (null != waitingOperation)
            workstation.assignOperation(waitingOperation);
      }
      return processTime;
   }

   public boolean evalBlockedStatus() {
      boolean eval = workstation.evalBlockedStatus();
      if (eval)
         // if I'm able to move all blocked operations to an after buffer the workstation
         // will no longer be blocked
         return !tryMoveBlockedOperationsToBuffer();
      return eval;
   }

   public int getAssignedOperators() {
      return workstation.getAssignedOperators();
   }

   public int getRequiredOperators() {
      return workstation.getRequiredOperators();
   }

   public Set<Operator> assignOperators(Collection<? extends Operator> operators) {
      return workstation.assignOperators(operators);
   }

   public Set<Operator> unassignOperators() {
      return workstation.unassignOperators();
   }

}
