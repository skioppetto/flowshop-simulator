package com.flowshop.simulator;

import java.util.Collection;
import java.util.HashSet;

public class WorkstationOperatorsSet extends HashSet<Operator> {

   private WorkCell workstation;

   public WorkstationOperatorsSet(WorkCell workstation) {
      super();
      this.workstation = workstation;
   }

   public WorkstationOperatorsSet(WorkCell workstation, Collection<? extends Operator> collection) {
      super(collection);
      this.workstation = workstation;
      for (Operator op : collection)
         op.setAssignedWorkstation(workstation);
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

   @Override
   public void clear() {
      this.stream().forEach(operator -> ((Operator) operator).setAssignedWorkstation(null));
      super.clear();
   }

}
