package com.flowshop.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeseriesWriter extends AbstractEventsWrapper {

   private final FileWriter timeseriesFw;
   private long millisStart;
   private Set<String> series = new HashSet<>();
   private Map<Long, Set<TimeseriesEvent>> eventsMap = new HashMap<>();
   private final Unit unit;

   public enum Unit {

      SECONDS(1000l), MINUTES(1000l * 60l), HOURS(1000l * 60l * 24l);

      final long multiplier;

      Unit(long multiplier) {
         this.multiplier = multiplier;
      }
   }

   class TimeseriesEvent {
      String series;
      Object value;

      public TimeseriesEvent(String series, Object value) {
         this.series = series;
         this.value = value;
      }

   }

   public TimeseriesWriter(String pathToFolder, Date start, Unit unit) throws IOException {
      Files.createDirectories(Paths.get(pathToFolder));
      Files.deleteIfExists(Paths.get(pathToFolder, "timeseries.csv"));
      timeseriesFw = new FileWriter(Paths.get(pathToFolder, "timeseries.csv").toFile());
      this.unit = unit;
      millisStart = start.getTime();
   }

   void catchOperatorEvent(OperatorEvent event) {
   }

   void catchWorkstationBufferEvent(WorkstationBufferEvent event) {

   }

   void catchWorkstationEvent(WorkstationEvent event) {
      String wrkSeries = event.getWorkstationId() + ".status";
      String operationSeries = event.getWorkstationId() + ".operation";
      String orderSeries = event.getWorkstationId() + ".order";

      Set<TimeseriesEvent> eventsSet;
      if (!eventsMap.keySet().contains(event.getStartTime())) {
         eventsSet = new HashSet<>();
         eventsMap.put(event.getStartTime(), eventsSet);
      } else {
         eventsSet = eventsMap.get(event.getStartTime());
      }

      eventsSet.add(new TimeseriesEvent(wrkSeries, event.getStatus().toString()));
      eventsSet.add(new TimeseriesEvent(operationSeries,
            (event.getOperationId() != null) ? event.getOperationId() : "<unassigned>"));
      eventsSet.add(new TimeseriesEvent(orderSeries,
            (event.getOrderId() != null) ? event.getOrderId() : "<unassigned>"));
      series.add(wrkSeries);
      series.add(operationSeries);
      series.add(orderSeries);

   }

   private String header(List<String> series) {
      StringBuilder headerBuilder = new StringBuilder();
      headerBuilder.append("time");
      for (String col : series) {
         headerBuilder.append(", " + col);
      }
      headerBuilder.append("\n");
      return headerBuilder.toString();
   }

   void close() {

      List<Long> timestampList = new LinkedList<>(eventsMap.keySet());
      List<String> seriesList = new LinkedList<>(series);
      Collections.sort(timestampList);
      Collections.sort(seriesList);
      StringBuilder sBuilder = new StringBuilder();
      sBuilder.append(header(seriesList));
      Map<String, Object> latest = new HashMap<>();
      for (Long timestamp : timestampList) {
         Instant instant = Instant.ofEpochMilli(millisStart + (timestamp * unit.multiplier));
         String iso8601 = DateTimeFormatter.ISO_INSTANT.format(instant);
         sBuilder.append(iso8601);
         Set<TimeseriesEvent> events = eventsMap.get(timestamp);
         Set<String> changedSeries = new HashSet<>();
         String[] values = new String[seriesList.size()];
         for (TimeseriesEvent event : events) {
            values[seriesList.indexOf(event.series)] = (event.value != null) ? event.value.toString() : "<unknown>";
            changedSeries.add(event.series);
            latest.put(event.series, event.value);
         }
         for (String s : seriesList)
            if (!changedSeries.contains(s))
               values[seriesList.indexOf(s)] = (latest.containsKey(s)) ? latest.get(s).toString() : "<unknown>";

         for (String value : values)
            sBuilder.append("," + value);

         sBuilder.append("\n");

      }

      try {
         timeseriesFw.append(sBuilder.toString());
         timeseriesFw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
