package com.flowshop;

import java.util.Set;

public interface Workstation {

   boolean assignOperation(Operation op);

   Object getStatus();

   long process(long i);

   boolean evalBlockedStatus();

   int getAssignedOperators();

   int getRequiredOperators();

   void assignOperators(Operator... op);

   Set<Operator> unassignOperators();

}
