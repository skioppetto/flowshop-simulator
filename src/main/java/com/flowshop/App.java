package com.flowshop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

import com.flowshop.reader.ConfigurationJsonReader;
import com.flowshop.reader.SimulationBuilder;
import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.Order;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;
import com.flowshop.writer.CsvFileWriter;
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
        sim.addEventsWriter(new CsvFileWriter(sim, folder + File.separator + "results"));
        sim.addEventsWriter(new TimeseriesWriter(sim, folder + File.separator + "results", new Date(), Unit.SECONDS));
        FileWriter fLogger = new FileWriter(folder + File.separator + "results" + File.separator + "sim.log");
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
                fLogger.append(printSimStatus(sim));
            }
            if (countHangs >= MAX_SIMULATION_HANGS) {
                fLogger.append(printSimStatus(sim));
                System.out.println("SIMULATION HANGS:");
                System.out.println(printSimStatus(sim));
                break;
            }
            lastSimulation = sim.getSimulationTime();
        }
        sim.stop();
        fLogger.close();
    }

    private static int calculateProgress(Set<Workstation> workstations) {
        long progress = 0;
        for (Workstation wrk : workstations) {
            if (wrk.evalProcess() > 0 && (wrk.evalProcess() < progress || progress == 0))
                progress = wrk.evalProcess();
        }
        return (int) progress;

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
