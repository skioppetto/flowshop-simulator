package com.flowshop;

import java.util.Arrays;

public class SimulatorTestUtils {
   
   public static Order buildOrder(String orderId, WorkCell[] workstations, long[] cycletimes) {
      Operation[] operations = new Operation[workstations.length];
      for (int i = workstations.length - 1; i >= 0; i--) {
         Operation nextOperation = (i == workstations.length - 1) ? null : operations[i + 1];
         operations[i] = new Operation(orderId + " op" + (i + 1), cycletimes[i], workstations[i], nextOperation);
      }
      return new Order(Arrays.asList(operations));

   }

}
