package com.flowshop;

import java.util.Collection;
import java.util.HashSet;

public class WorkstationOperatorsSet extends HashSet<Operator> {

   private Workstation workstation;

   public WorkstationOperatorsSet(Workstation workstation) {
      super();
      this.workstation = workstation;
   }

   @Override
   public boolean add(Operator operator) {
      operator.setAssignedWorkstation(workstation);
      return super.add(operator);
   }

   @Override
   public boolean remove(Object o) {
      ((Operator) o).setAssignedWorkstation(null);
      return super.remove(o);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      c.stream().forEach(operator -> ((Operator) operator).setAssignedWorkstation(null));
      return super.removeAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Operator> c) {
      c.stream().forEach(operator -> ((Operator) operator).setAssignedWorkstation(workstation));
      return super.addAll(c);
   }

}
