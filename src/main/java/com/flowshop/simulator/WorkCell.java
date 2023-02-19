package com.flowshop.simulator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@RequiredArgsConstructor
public class WorkCell extends Workstation {

   @EqualsAndHashCode.Include
   @Getter
   private final String id;

   @ToString.Exclude
   @Getter
   private Operation currentOperation;

   @ToString.Exclude
   private Set<Operator> assignedOperators = new WorkstationOperatorsSet(this);

   @ToString.Exclude
   private Map<String, Set<Operator>> assignedOperatorsGroups = new HashMap<>();

   @ToString.Exclude
   @Getter(value = AccessLevel.PACKAGE)
   private Operation latestOperation;

   @ToString.Exclude
   @Getter
   @Setter
   private WorkGroup workGroup;

   private int calculateOperationRequiredOperators() {
      if (currentOperation == null)
         return 0;
      int requiredOperators = currentOperation.getRequiredOperators();
      for (int operatorsPerGroup : currentOperation.getRequiredOperatorsGroups().values())
         requiredOperators += operatorsPerGroup;
      return requiredOperators;
   }

   private int calculateAssignedOperators() {
      int assignedOperatorsCount = assignedOperators.size();
      for (Set<Operator> operatorsGroup : assignedOperatorsGroups.values())
         assignedOperatorsCount += operatorsGroup.size();
      return assignedOperatorsCount;
   }

   @Override
   public Workstation.Status getStatus() {
      if (null == currentOperation)
         return Workstation.Status.IDLE;
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime())
         return Workstation.Status.BLOCKED;
      if (calculateAssignedOperators() < calculateOperationRequiredOperators())
         return Workstation.Status.WAITING_FOR_OPERATOR;
      return Workstation.Status.PROCESSING;
   }

   public long evalProcess(long i) {
      Workstation.Status status = this.getStatus();
      if (status != Workstation.Status.PROCESSING
            && status != Workstation.Status.BLOCKED)
         return 0;
      return Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
   }

   public long evalProcess() {
      Workstation.Status status = this.getStatus();
      if (status == Workstation.Status.PROCESSING)
         return currentOperation.getCycleTime() - currentOperation.getProcessedTime();
      else
         return 0;
   }

   public long process(long i) {
      Workstation.Status status = this.getStatus();
      if (status != Workstation.Status.PROCESSING
            && status != Workstation.Status.BLOCKED)
         return 0;
      long processTime = Math.min(i, currentOperation.getCycleTime() - currentOperation.getProcessedTime());
      currentOperation.setProcessedTime(processTime + currentOperation.getProcessedTime());
      if (currentOperation.getCycleTime() <= currentOperation.getProcessedTime()) {
         this.latestOperation = currentOperation;
         this.currentOperation = null;

      }
      notifySimObjectObservers();
      return processTime;
   }

   // this method should be called after all workstations are processed
   public boolean evalBlockedStatus() {
      boolean isBlocked = wasBlocked();
      if (latestOperation != null)
         latestOperation.setBlocked(isBlocked);
      if (isBlocked) {
         currentOperation = latestOperation;
      } else {
         latestOperation = null;
      }
      notifySimObjectObservers();
      return isBlocked;
   }

   // this method should be call if there are some other conditions different from
   // the next operation status that can block the cell
   public void forceBlocked() {
      latestOperation.setBlocked(true);
      currentOperation = latestOperation;
   }

   private boolean wasBlocked() {
      return latestOperation != null && latestOperation.getNextOperation() != null
            && !latestOperation.getNextOperation().getRequiredWorkstation().getStatus()
                  .equals(Workstation.Status.IDLE);
   }

   public boolean assignOperation(Operation op) {
      if (this.currentOperation == null) {
         this.currentOperation = op;
         op.start();
         notifySimObjectObservers();
         return true;
      }
      return false;
   }

   public Set<Operator> assignOperators(Collection<? extends Operator> operators) {
      Set<Operator> returnAssigned = new HashSet<>();
      if (this.getStatus().equals(Workstation.Status.WAITING_FOR_OPERATOR)
            && operators.size() >= calculateOperationRequiredOperators()) {
         Iterator<? extends Operator> operatorsIt = operators.iterator();
         while (operatorsIt.hasNext() && calculateOperationRequiredOperators() > calculateAssignedOperators()) {
            Operator op = operatorsIt.next();
            // check first operators groups requirements
            if (op.getGroup() != null && currentOperation.getRequiredOperatorsGroups().containsKey(op.getGroup())) {
               if (assignedOperatorsGroups.containsKey(op.getGroup())) {
                  if (assignedOperatorsGroups.get(op.getGroup()).size() < currentOperation.getRequiredOperatorsGroups()
                        .get(op.getGroup())) {
                     if (assignedOperatorsGroups.get(op.getGroup()).add(op))
                        returnAssigned.add(op);
                  }
               } else {
                  assignedOperatorsGroups.put(op.getGroup(), new WorkstationOperatorsSet(this, Arrays.asList(op)));
                  returnAssigned.add(op);
               }
            }
            // if operator wasn't assigned to a group (it's not present in returnAssigned)
            // and current operation require generic operators assign to this group
            if (!returnAssigned.contains(op) && assignedOperators.size() < currentOperation.getRequiredOperators()
                  && assignedOperators.add(op))
               returnAssigned.add(op);
         }
      }
      // if assigned operators are not enough for this operation clear all so they
      // will be available for other workstations
      if (calculateOperationRequiredOperators() > calculateAssignedOperators()) {
         returnAssigned.clear();
         for (Set<Operator> group : assignedOperatorsGroups.values())
            group.clear();
         assignedOperatorsGroups.clear();
         assignedOperators.clear();
      } else {
         notifySimObjectObservers();
      }
      return returnAssigned;
   }

   public Set<Operator> unassignOperators() {
      if (this.getStatus().equals(Workstation.Status.PROCESSING))
         return Collections.emptySet();
      HashSet<Operator> returnSet = new HashSet<>(assignedOperators);
      assignedOperators.clear();
      for (Set<Operator> operatorsGroup : assignedOperatorsGroups.values()) {
         returnSet.addAll(operatorsGroup);
         operatorsGroup.clear();
      }
      assignedOperatorsGroups.clear();
      return returnSet;
   }

   public Operation unassignOperation() {
      Operation unassigned = currentOperation;
      unassigned.setBlocked(false);
      currentOperation = null;
      latestOperation = null;
      return unassigned;
   }

}
