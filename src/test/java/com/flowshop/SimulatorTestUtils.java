package com.flowshop;

import java.util.Arrays;

import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.Workstation;

public class SimulatorTestUtils {

   public static Order buildOrder(String orderId, WorkCell[] workstations, long[] cycletimes) {
      Operation[] operations = new Operation[workstations.length];
      for (int i = workstations.length - 1; i >= 0; i--) {
         Operation nextOperation = (i == workstations.length - 1) ? null : operations[i + 1];
         operations[i] = new Operation(orderId + " op" + (i + 1), cycletimes[i], workstations[i], nextOperation);
      }
      return new Order(orderId, Arrays.asList(operations));

   }

   public static void simulateProcess(long processTime, Workstation... stations) {
      for (Workstation w : stations) {
         w.process(processTime);
      }
      for (Workstation w : stations) {
         w.evalBlockedStatus();
      }
   }

}
