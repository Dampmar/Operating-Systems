package components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.*;

public class Simulation {
    private String inputFile;
    private List<Order> orders = new ArrayList<Order>();
    private Scheduler scheduler;
    private int chefTime;
    private int bakeTime;
    private int quantum;

    // Constructor 
    public Simulation(String inputFile, int chefs, int ovens, int drivers, String strategy, int bakeTime, int chefTime, int chefQuantum) {
        this.inputFile = inputFile;
        this.orders = FileParser.read_file(inputFile);
        this.chefTime = chefTime;
        this.bakeTime = bakeTime;
        this.quantum = chefQuantum;

        // Create apropiate scheduler depending on strategy
        if (strategy.equals("RR") && chefQuantum > 0) {
            this.scheduler = new SchedulerRoundRobin(orders, chefs, ovens, drivers, strategy, bakeTime, chefTime, chefQuantum);
        } 
        else if (strategy.equals("FOCUSED")) {
            this.scheduler = new SchedulerFocused(orders, chefs, ovens, drivers, strategy, bakeTime, chefTime, chefQuantum);
        } 
        else {
            throw new IllegalArgumentException("Invalid scheduler configuration.");
        }
    }

    // Function in charge of executing: while !allOrdersDelivered execute a cycle
    public void run() {
        while (scheduler.getOrders().stream().anyMatch(order -> order.getState() != State.DELIVERED)) {
            scheduler.cycle();
            System.out.println("==== Minute " + getMinute());
            printEndCycleState();
        }
    }

    // Function in charge of printing 
    public void printEndCycleState() {
        // Print Order Status 
        for (Order order : scheduler.getOrders()) {
            System.out.println(order.toString(this.chefTime, this.bakeTime));
        }

        // Print Chef Status
        for (Chef chef : scheduler.getChefs()) {
            String status = "Chef" + chef.getId() + ",";
            if (chef.getCurrentPizza() != null) {
                status += chef.getCurrentOrder().getPerson();
                if (scheduler.getStrategy().equals("RR")) {
                    int timeQuantumRemaining = quantum - chef.getTimePreparing();
                    // int timeQuantumRemaining = chef.getCurrentPizza().getTimeLeft();
                    status += "," + timeQuantumRemaining;
                }
            } else {
                status += "None";
            }
            System.out.println(status);
        }

        // Print Oven Status 
        for (Oven oven : scheduler.getOvens()) {
            String status = "Oven" + oven.getId() + ",";
            if (oven.getCurrentPizza() != null) {
                status += oven.getCurrentOrder().getPerson();
            } else {
                status += "None";
            }
            System.out.println(status);
        }

        // Print Driver Status 
        for (Driver driver : scheduler.getDrivers()) {
            String status = "Driver" + driver.getId() + ",";
            if (driver.getCurrentOrder() != null) {
                status += driver.getCurrentOrder().getPerson();
            } else {
                status += "None";
            }
            System.out.println(status);
        }
    }

    // Helper Functions for Multi-threading 
    public List<Order> getOrders() { return scheduler.getOrders(); }
    public void cycle() {  scheduler.cycle(); }
    public int getMinute() { return scheduler.getMinute(); }

}
