package com.flowshop.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.SimObserver;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;

public class SimulationLogger implements SimObserver {

   private final FileWriter fLogger;

   public SimulationLogger(String pathToFolder) throws IOException {
      Files.createDirectories(Paths.get(pathToFolder));
      fLogger = new FileWriter(pathToFolder + File.separator + "sim.log");
   }

   @Override
   public void onProcessEnd(Simulation sim) {
      try {
         fLogger.append(printSimStatus(sim));
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   public void onProcessStart(Simulation sim) {

   }

   @Override
   public void onStartSimulation(Simulation sim) {

   }

   @Override
   public void onStopSimulation(Simulation sim) {
      try {
         fLogger.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static String printSimStatus(Simulation sim) {
      StringBuilder sBuilder = new StringBuilder();

      sBuilder.append("### SIMULATION TIME: " + sim.getSimulationTime() + "\n");
      sBuilder.append("### WORKSTATIONS:" + "\n");
      for (Workstation wrk : sim.getWorkstations()) {
         sBuilder.append("# " + wrk.getId() + " is currently " + wrk.getStatus().toString() + "\n");
         Workstation workstation = wrk;
         if (wrk instanceof BufferedWorkstation) {
            BufferedWorkstation buff = (BufferedWorkstation) wrk;
            String bufferState = "before buffer: " + buff.getBeforeBuffer().size() + "/"
                  + buff.getBeforeBufferMaxSize() + "\tafter buffer: " + buff.getAfterBuffer().size() + "/"
                  + buff.getAfterBufferMaxSize() + "\n";
            workstation = buff.getWorkstation();
            sBuilder.append(bufferState);
         }
         if (workstation instanceof WorkGroup) {
            for (WorkCell wcell : ((WorkGroup) workstation).getWorkCells()) {
               sBuilder.append(
                     "# " + wcell.getId() + " is currently " + workstation.getStatus().toString() + "\n");
               if (wcell.getCurrentOperation() != null)
                  sBuilder.append("\t" + cellStatus(wcell) + "\n");
            }
         } else {
            WorkCell wcell = (WorkCell) workstation;
            if (wcell.getCurrentOperation() != null)
               sBuilder.append("\t" + cellStatus(wcell) + "\n");
         }
      }
      sBuilder.append("### OPERATORS:" + "\n");
      sBuilder.append("available operators: " + sim.getAvailableOperators().size() + "\n");
      for (Operator op : sim.getOperators()) {
         sBuilder.append("operator " + op.getId() + ((op.getAssignedWorkstation() == null) ? " is not assigned"
               : " is assigned to " + op.getAssignedWorkstation().getId()) + "\n");
      }
      return sBuilder.toString();
   }

   private static String cellStatus(WorkCell wcell) {
      return "operation " + wcell.getCurrentOperation().getId() + "\tprogress "
            + wcell.getCurrentOperation().getProcessedTime()
            + "/" + wcell.getCurrentOperation().getCycleTime()
            + "\t required operators: " + wcell.getCurrentOperation().getRequiredOperators();
   }

}
