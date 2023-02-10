package com.flowshop.writer;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.Workstation;

public class CsvFileWriterTest {

   private Order buildOrder(String orderName, Workstation wst1, Workstation wst2) {
      Operation op2 = new Operation(orderName + ".slow.op2", 30, wst2, null);
      Operation op1 = new Operation(orderName + ".fast.op1", 10, wst1, op2);
      return new Order("order", Arrays.asList(op1, op2));

   }

   @Test
   void runSimulation() throws IOException {
      BufferedWorkstation bCell1 = new BufferedWorkstation(new WorkCell("cell1"), 2, 0);
      WorkCell cell2 = new WorkCell("cell2");
      Simulation sim = new Simulation(Arrays.asList(
            buildOrder("ord1", bCell1, cell2),
            buildOrder("ord2", bCell1, cell2),
            buildOrder("ord3", bCell1, cell2)));
      CsvFileWriter writer = new CsvFileWriter(sim, "./test");
      Thread writerThread = new Thread(writer);
      writerThread.start();
      sim.start();
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);
      sim.process(10);

      writer.setStopped(true);
      try {
         writerThread.join(10);
         writerThread.interrupt();
      } catch (InterruptedException e) {
         fail();
      }
   }
}
