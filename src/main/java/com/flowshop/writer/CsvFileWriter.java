package com.flowshop.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.flowshop.simulator.Simulation;

public class CsvFileWriter extends AbstractEventsWriter {

   private final FileWriter workstationFw;
   private final FileWriter workstationBufferFw;
   private final FileWriter operatorFw;

   public CsvFileWriter(Simulation simulation, String pathToFolder) throws IOException {
      super(simulation);
      Files.createDirectories(Paths.get(pathToFolder));
      Files.deleteIfExists(Paths.get(pathToFolder, "workstations.csv"));
      Files.deleteIfExists(Paths.get(pathToFolder, "operators.csv"));
      Files.deleteIfExists(Paths.get(pathToFolder, "buffers.csv"));
      workstationFw = new FileWriter(Paths.get(pathToFolder, "workstations.csv").toFile());
      workstationBufferFw = new FileWriter(Paths.get(pathToFolder, "buffers.csv").toFile());
      operatorFw = new FileWriter(Paths.get(pathToFolder, "operators.csv").toFile());
      headerWorkstations();
      headerOperators();
      headerBuffers();
   }

   private void headerBuffers() throws IOException {
      workstationBufferFw.append("workstationId, status, statusStart, statusDuration\n");
   }

   private void headerOperators() throws IOException {
      operatorFw.append("operatorId, groupId, status, statusStart, statusDuration, workCellId, operationId, orderId\n");
   }

   private void headerWorkstations() throws IOException {
      workstationFw.append("workcellId, workstationId, status, statusStart, statusDuration, operationId, orderId\n");
   }

   void catchOperatorEvent(OperatorEvent event) {
      String line = event.getOperatorId() + ", " + event.getGroupId() + ", " + event.getStatus().toString() + ", "
            + event.getStartTime() + ", " + event.getDuration() + ", " + event.getWorkstationId() + ", "
            + event.getOperationId() + ", " + event.getOrderId() + "\n";
      try {
         operatorFw.append(line);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   void catchWorkstationBufferEvent(WorkstationBufferEvent event) {
      String line = event.getWorkstationId() + ", " + event.getBufferType().toString() + ", "
            + event.getEventType().toString() + ", " + event.getTime() + ", " + event.getSize() + "\n";
      try {
         workstationBufferFw.append(line);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   void catchWorkstationEvent(WorkstationEvent event) {
      String line = event.getWorkstationId() + ", " + (event.getWorkGroupId()==null?event.getWorkstationId():event.getWorkGroupId()) + ", " + event.getStatus().toString()
            + ", " + event.getStartTime() + ", " + event.getDuration() + ", " + event.getOperationId() + ", "
            + event.getOrderId() + "\n";
      try {
         workstationFw.append(line);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   void close() {
      try {
         workstationFw.close();
         workstationBufferFw.close();
         operatorFw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
