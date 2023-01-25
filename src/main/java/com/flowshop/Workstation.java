package com.flowshop;

import java.util.Collection;
import java.util.Set;

public interface Workstation {

   boolean assignOperation(Operation op);

   Object getStatus();

   long process(long i);

   boolean evalBlockedStatus();

   int getAssignedOperators();

   int getRequiredOperators();

   Set<Operator> assignOperators(Collection<? extends Operator> operators);

   Set<Operator> unassignOperators();

}
