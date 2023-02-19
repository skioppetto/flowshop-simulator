package com.flowshop.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.flowshop.reader.OperationRequrements;
import com.flowshop.reader.OrderRequirements;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrdersGenerator {

   private static final int ORDERS_COUNT = 50;
   private static final int ORDER_ID_LENGTH = 10;
   private static final int OPERATIONS_COUNT = 8;
   private static final long CYCLE_TIME_MIN = 10;
   private static final long CYCLE_TIME_MAX = 100;
   private static final int OPERATORS_MIN = 0;
   private static final int OPERATORS_MAX = 4;

   public static void main(String[] args) throws IOException {
      OrderRequirements[] orders = new OrderRequirements[ORDERS_COUNT];
      for (int i = 0; i < ORDERS_COUNT; i++) {
         OrderRequirements orderReq = new OrderRequirements();
         String orderId = generateRandomOrderId();
         OperationRequrements[] opsReq = new OperationRequrements[OPERATIONS_COUNT];
         orderReq.setOrderId(orderId);
         orderReq.setOperations(opsReq);
         for (int j = 0; j < OPERATIONS_COUNT; j++) {
            OperationRequrements opReq = new OperationRequrements();
            opReq.setOperationId(orderId + "." + String.format("%02d", j + 1));
            opReq.setWorkstation("wrk" + (j + 1));
            opReq.setCycleTime(generateCycleTime());
            opReq.setOperatorsAny(generateOperators());
            opsReq[j] = opReq;
         }
         orders[i] = orderReq;
      }
      Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
      String json = gson.toJson(orders);
      Files.createDirectories(Paths.get("generator"));
      FileWriter fWriter = new FileWriter("./generator/generator_orders.json");
      // System.out.println(json);
      fWriter.append(json);
      fWriter.close();
   }

   private static int generateOperators() {
      return OPERATORS_MIN + (int) (Math.random() * ((double) (OPERATORS_MAX - OPERATORS_MIN)));
   }

   private static long generateCycleTime() {
      return CYCLE_TIME_MIN + (long) (Math.random() * ((double) (CYCLE_TIME_MAX - CYCLE_TIME_MIN)));
   }

   private static String generateRandomOrderId() {
      String orderId = "";
      for (int i = 0; i < ORDER_ID_LENGTH; i++) {
         orderId = orderId + (int)( Math.random() * 9d);
      }
      return orderId;
   }
}
