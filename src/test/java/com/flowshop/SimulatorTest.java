package com.flowshop;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SimulatorTest {

   @Test
   void twoStationsSimulation() {
      Workstation wst1 = new Workstation();
      Workstation wst2 = new Workstation();
      
      Operation op11 = new Operation("ord1 op1", 50);
      Operation op12 = new Operation("ord1 op2", 100);
      Order ord1 = new Order(Arrays.asList(op11, op12));

      Operation op21 = new Operation("ord2 op1", 70);
      Operation op22 = new Operation("ord2 op2", 65);
      Order ord2 = new Order(Arrays.asList(op21, op22));

      Operation op31 = new Operation("ord3 op1", 45);
      Operation op32 = new Operation("ord3 op2", 100);
      Order ord3 = new Order(Arrays.asList(op31, op32));

   }

}
