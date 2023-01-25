package com.flowshop;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class WorkCell implements Workstation {

   @EqualsAndHashCode.Include
   @Getter
   private final String id;

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING, BLOCKED
   };

   @ToString.Exclude
   @Getter
   private Operation currentOperation;

   @ToString.Exclude
   private Set<Operator> assignedOperators = new WorkstationOperatorsSet(this);

   @ToString.Exclude
   @Getter(value = AccessLevel.PACKAGE)
   private Operation latestOperation;

   public int getRequiredOperators() {
      Status status = getStatus();
      return (status == Status.IDLE || status == Status.BLOCKED) ? 0
            : (currentOperation.getRequiredOperators() - getAssignedOperators());
   }

   public Status getStatus() {
      if (null == currentOperation)
         return Status.IDLE;
      if (assignedOperators.size() < currentOperation.getRequiredOperators())
         return Status.WAITING_FOR_OPERATOR;
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime())
         return Status.BLOCKED;
      return Status.PROCESSING;
   }

   public long evalProcess(long i) {
      Status status = this.getStatus();
      if (status != Status.PROCESSING
            && status != Status.BLOCKED)
         return 0;
      return Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
   }

   public long process(long i) {
      Status status = this.getStatus();
      if (status != Status.PROCESSING
            && status != Status.BLOCKED)
         return 0;
      long processTime = Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
      currentOperation.setProcessedTime(processTime + currentOperation.getProcessedTime());
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime()) {
         this.latestOperation = currentOperation;
         this.currentOperation = null;
      }
      return processTime;
   }

   // this method should be called after all workstations are processed
   public boolean evalBlockedStatus() {
      boolean isBlocked = wasBlocked();
      if (isBlocked)
         this.currentOperation = this.latestOperation;
      return isBlocked;
   }

   private boolean wasBlocked() {
      return latestOperation != null && latestOperation.getNextOperation() != null
            && !latestOperation.getNextOperation().getRequiredWorkstation().getStatus()
                  .equals(WorkCell.Status.IDLE);
   }

   public boolean assignOperation(Operation op) {
      if (this.currentOperation == null) {
         this.currentOperation = op;
         return true;
      }
      return false;
   }

   public Set<Operator> assignOperators(Collection<? extends Operator> operators) {
      Set<Operator> returnAssigned = new HashSet<>();
      if (this.getStatus().equals(Status.WAITING_FOR_OPERATOR) && operators.size() >= getRequiredOperators()) {
         Iterator<? extends Operator> operatorsIt = operators.iterator();
         while (operatorsIt.hasNext() && getRequiredOperators() > 0) {
            Operator op = operatorsIt.next();
            if (assignedOperators.add(op))
               returnAssigned.add(op);
         }
      }
      return returnAssigned;
   }

   public Set<Operator> unassignOperators() {
      if (this.getStatus().equals(Status.PROCESSING) || assignedOperators.isEmpty())
         return Collections.emptySet();
      HashSet<Operator> returnSet = new HashSet<>(assignedOperators);
      assignedOperators.clear();
      return returnSet;

   }

   public int getAssignedOperators() {
      return assignedOperators.size();
   }

}
