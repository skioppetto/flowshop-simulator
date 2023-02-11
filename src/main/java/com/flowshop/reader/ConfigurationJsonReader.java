package com.flowshop.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;

public class ConfigurationJsonReader implements IConfigurationReader {
   private final String folder;

   private static final String WORKSTATION_JSON_FILENAME = "workstations.json";
   private static final String OPERATOR_JSON_FILENAME = "operators.json";
   private static final String ORDERS_JSON_FILENAME = "orders.json";

   private final Gson gson = new Gson();
   private Reader workstationReader;
   private Reader operatorReader;
   private Reader orderReader;

   public ConfigurationJsonReader(String folder) throws FileNotFoundException {
      this.folder = folder;
      this.orderReader = buildReader(ORDERS_JSON_FILENAME);
      try {
         this.operatorReader = buildReader(OPERATOR_JSON_FILENAME);
      } catch (FileNotFoundException e) {
         System.out.println("no operators configuration provided (operator.json)");
         // I can mange this case whenever all stations are automated stations.
      }
      try {
         this.workstationReader = buildReader(WORKSTATION_JSON_FILENAME);
      } catch (FileNotFoundException e) {
         System.out.println("no workstation configuration provided (workstation.json)");
         // I can mange this case whenever all stations are simple one cell stations.
      }
   }

   public WorkstationRequirements[] getWorkstationsRequirements() {
      return (workstationReader != null) ? gson.fromJson(workstationReader, WorkstationRequirements[].class) : null;
   }

   public OperatorRequirements[] getOperatorRequirements() {
      return (operatorReader != null) ? gson.fromJson(operatorReader, OperatorRequirements[].class) : null;
   }

   public OrderRequirements[] getOrdersRequirements() {
      return gson.fromJson(orderReader, OrderRequirements[].class);
   }

   private Reader buildReader(String filename) throws FileNotFoundException {
      String file = folder + File.separator + filename;
      return new FileReader(file);

   }

}
