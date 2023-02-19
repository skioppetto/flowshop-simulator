package com.flowshop;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.flowshop.reader.ConfigurationJsonReader;
import com.flowshop.reader.SimulationBuilder;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;
import com.flowshop.writer.CsvFileWriter;

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
        sim.addEventsWriter(new CsvFileWriter(sim, folder + File.separator + "results"));
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
                printSimStatus(sim);
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

    private static void printSimStatus(Simulation sim) {
        System.out.println("### SIMULATION TIME: " + sim.getSimulationTime());
        System.out.println("### WORKSTATIONS:");
        for (Workstation wrk : sim.getWorkstations()) {
            System.out.println("# " + wrk.getId() + " is currently " + wrk.getStatus().toString());
            if (wrk instanceof WorkGroup) {
                for (WorkCell wcell : ((WorkGroup) wrk).getWorkCells()) {
                    System.out.println("# " + wcell.getId() + " is currently " + wrk.getStatus().toString());
                    if (wcell.getCurrentOperation() != null)
                        System.out.println("\t" + cellStatus(wcell));
                }
            } else {
                WorkCell wcell = (WorkCell) wrk;
                if (wcell.getCurrentOperation() != null)
                    System.out.println("\t" + cellStatus(wcell));
            }
        }
        System.out.println("### OPERATORS:");
        System.out.println("available operators: " + sim.getAvailableOperators().size());
        for (Operator op : sim.getOperators()) {
            System.out.println("operator " + op.getId() + ((op.getAssignedWorkstation() == null) ? " is not assigned"
                    : " is assigned to " + op.getAssignedWorkstation().getId()));
        }
    }

    private static String cellStatus(WorkCell wcell) {
        return "operation " + wcell.getCurrentOperation().getId() + "\tprogress "
                + wcell.getCurrentOperation().getProcessedTime()
                + "/" + wcell.getCurrentOperation().getCycleTime()
                + "\t required operators: " + wcell.getCurrentOperation().getRequiredOperators();
    }
}
