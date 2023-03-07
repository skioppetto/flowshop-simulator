package com.flowshop;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

import com.flowshop.reader.ConfigurationJsonReader;
import com.flowshop.reader.SimulationBuilder;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.Workstation;
import com.flowshop.writer.CsvFileWriter;
import com.flowshop.writer.SimulationLogger;
import com.flowshop.writer.TimeseriesWriter;
import com.flowshop.writer.TimeseriesWriter.Unit;

/**
 * Hello world!
 */
public final class App {
    // seconds in a day
    // TODO: move to argument
    private static final long MAX_SIMULATION_TIME = 24L * 3600L;
    private static final int MAX_SIMULATION_HANGS = 10;

    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String folder = args[0];
        SimulationBuilder builder = new SimulationBuilder(new ConfigurationJsonReader(folder));
        Simulation sim = builder.build();
        String pathToResults = folder + File.separator + "results";
        sim.addSimulationObserver(new CsvFileWriter(pathToResults));
        sim.addSimulationObserver(new TimeseriesWriter(pathToResults, new Date(), Unit.SECONDS));
        sim.addSimulationObserver(new SimulationLogger(pathToResults));
        Order lastOrder = sim.getOrders().get(sim.getOrders().size() - 1);
        sim.start();
        long lastSimulation = 0;
        int countHangs = 0;
        while (!lastOrder.getStatus().equals(Order.Status.DONE) && sim.getSimulationTime() < MAX_SIMULATION_TIME) {
            sim.process(calculateProgress(sim.getWorkstations()));
            if (sim.getSimulationTime() == lastSimulation) {
                countHangs++;
            } else {
                countHangs = 0;
            }
            if (countHangs >= MAX_SIMULATION_HANGS) {
                System.out.println("SIMULATION HANGS:");
                break;
            }
            lastSimulation = sim.getSimulationTime();
        }
        sim.stop();
    }

    private static int calculateProgress(Set<Workstation> workstations) {
        long progress = 0;
        for (Workstation wrk : workstations) {
            if (wrk.evalProcess() > 0 && (wrk.evalProcess() < progress || progress == 0))
                progress = wrk.evalProcess();
        }
        return (int) progress;

    }

}
