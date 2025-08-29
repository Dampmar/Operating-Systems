package lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Scheduler {
    protected List<Order> orders = new ArrayList<Order>(); 
    protected List<Chef> chefs = new ArrayList<Chef>(); 
    protected List<Oven> ovens = new ArrayList<Oven>();
    protected List<Driver> drivers = new ArrayList<Driver>();
    protected String strategy;
    protected int chefQuantum;
    protected int chefTime;
    protected int bakeTime;
    protected int minute;

    // Constructor 
    public Scheduler(List<Order> orders, int chefs, int ovens, int drivers, String strategy, int bakeTime, int chefTime, int chefQuantum) {
        this.bakeTime = bakeTime;
        this.chefTime = chefTime;
        this.chefQuantum = chefQuantum;
        this.strategy = strategy;
        this.minute = 0;

        prioritizeList(orders);
        initializeWorkers(chefs, ovens, drivers);
    }

    // Helper Functions - Constructor
    private void prioritizeList(List<Order> orders) {
        PriorityQueue<Order> queue = new PriorityQueue<>(Comparator.comparingInt(Order::getPriority));
        queue.addAll(orders);
        this.orders.addAll(queue);
    }

    private void initializeWorkers(int chefs, int ovens, int drivers) {
        // Create Chefs
        for (int i = 0; i < chefs; i++) {
            this.chefs.add(new Chef(i));
        }
        
        // Create Ovens
        for (int i = 0; i < ovens; i++) {
            this.ovens.add(new Oven(i));
        }

        // Create Drivers 
        for (int i = 0; i < drivers; i++) {
            this.drivers.add(new Driver(i));
        }
    }

    // Getters and Setters 
    public List<Chef> getChefs() { return chefs; }
    public List<Oven> getOvens() { return ovens; }
    public List<Driver> getDrivers() { return drivers; }
    public List<Order> getOrders() { return orders; }

    public int getMinute() { return minute; }
    public void setMinute(int min) { this.minute = min; }

    public String getStrategy() { return strategy; }

    // Scheduling Functions
    protected void update() {
        for (Chef chef : chefs) chef.prepare();
        for (Oven oven : ovens) oven.cook();
        for (Driver driver : drivers) driver.deliver();
    }

    protected void incrementMinute() {
        this.minute++;
    }

    protected abstract void schedule();

    public void cycle() {
        schedule();
        update();
        incrementMinute();
    }
}