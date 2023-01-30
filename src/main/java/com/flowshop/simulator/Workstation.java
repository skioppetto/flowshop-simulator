package com.flowshop.simulator;

import java.util.Collection;
import java.util.Set;

public interface Workstation {

   boolean assignOperation(Operation op);

   WorkCell.Status getStatus();

   long process(long i);

   boolean evalBlockedStatus();

   int getAssignedOperators();

   Set<Operator> assignOperators(Collection<? extends Operator> operators);

   Set<Operator> unassignOperators();

}
