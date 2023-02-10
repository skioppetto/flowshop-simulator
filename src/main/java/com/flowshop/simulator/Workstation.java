package com.flowshop.simulator;

import java.util.Collection;
import java.util.Set;

public abstract class Workstation extends ObservableSimObject{

   public enum Status {
      IDLE, WAITING_FOR_OPERATOR, PROCESSING, BLOCKED
   }

   public abstract String getId();

   public abstract boolean assignOperation(Operation op);

   public abstract Workstation.Status getStatus();

   public abstract long process(long i);

   public abstract boolean evalBlockedStatus();

   public abstract Set<Operator> assignOperators(Collection<? extends Operator> operators);

   public abstract Set<Operator> unassignOperators();
}
