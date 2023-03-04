package com.flowshop.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.flowshop.reader.OperationRequrements;
import com.flowshop.reader.OrderRequirements;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrdersGenerator {

   private static final int ORDERS_COUNT = 10;
   private static final int ORDER_ID_LENGTH = 10;
   private static final int OPERATIONS_COUNT = 5;
   private static final long CYCLE_TIME_MIN = 10;
   private static final long CYCLE_TIME_MAX = 100;
   private static final int OPERATORS_MIN = 1;
   private static final int OPERATORS_MAX = 2;
   private static final String FILENAME = "./generator_orders.json";

   private static OrdersGeneratorConfig DEFAULT_CONFIG = new OrdersGeneratorConfig();

   static {
      DEFAULT_CONFIG = new OrdersGeneratorConfig();
      DEFAULT_CONFIG.setCycleTimeMax(CYCLE_TIME_MAX);
      DEFAULT_CONFIG.setCycleTimeMin(CYCLE_TIME_MIN);
      DEFAULT_CONFIG.setOperationsCount(OPERATIONS_COUNT);
      DEFAULT_CONFIG.setOperatorsMax(OPERATORS_MAX);
      DEFAULT_CONFIG.setOperatorsMin(OPERATORS_MIN);
      DEFAULT_CONFIG.setOrdersCount(ORDERS_COUNT);
      DEFAULT_CONFIG.setFilename(FILENAME);
   };

   public static void main(String[] args) throws IOException {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

      OrdersGeneratorConfig config = null;
      if (args.length > 0) {
         String configFile = args[0];
         FileReader configReader = new FileReader(configFile);
         config = gson.fromJson(configReader, OrdersGeneratorConfig.class);
      } else {
         config = DEFAULT_CONFIG;
      }
      OrderRequirements[] orders = new OrderRequirements[config.getOrdersCount()];
      for (int i = 0; i < config.getOrdersCount(); i++) {
         OrderRequirements orderReq = new OrderRequirements();
         String orderId = generateRandomOrderId();
         OperationRequrements[] opsReq = new OperationRequrements[config.getOperationsCount()];
         orderReq.setOrderId(orderId);
         orderReq.setOperations(opsReq);
         for (int j = 0; j < config.getOperationsCount(); j++) {
            OperationRequrements opReq = new OperationRequrements();
            opReq.setOperationId(orderId + "." + String.format("%02d", j + 1));
            opReq.setWorkstation("wrk" + (j + 1));
            opReq.setCycleTime(generateCycleTime(config));
            opReq.setOperatorsAny(generateOperators(config));
            opsReq[j] = opReq;
         }
         orders[i] = orderReq;
      }
      String json = gson.toJson(orders);
      FileWriter fWriter = new FileWriter(config.getFilename());
      // System.out.println(json);
      fWriter.append(json);
      fWriter.close();
   }

   private static int generateOperators(OrdersGeneratorConfig config) {
      return config.getOperatorsMin()
            + (int) (Math.random() * ((double) (config.getOperatorsMax() - config.getOperatorsMin())));
   }

   private static long generateCycleTime(OrdersGeneratorConfig config) {
      return CYCLE_TIME_MIN + (long) (Math.random() * ((double) (config.getCycleTimeMax() - config.getCycleTimeMin())));
   }

   private static String generateRandomOrderId() {
      String orderId = "";
      for (int i = 0; i < ORDER_ID_LENGTH; i++) {
         orderId = orderId + (int) (Math.random() * 9d);
      }
      return orderId;
   }
}
