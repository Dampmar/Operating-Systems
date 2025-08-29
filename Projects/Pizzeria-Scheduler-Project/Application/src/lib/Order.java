package lib;
import java.util.*;

public class Order {
    private String person;      // The person who ordered the pizza
    private int pizzas;         // The number of pizzas ordered
    private int deliveryTime;   // The time it takes to deliver the pizzas
    private int priority;       // The priority of the order  
    private boolean inProgress; // Whether the order is in progress or not
    private boolean delivering; // Whether the order is being delivered or not

    // Queues of pizzas in different states 
    public List<Pizza> pending = new ArrayList<Pizza>();
    public List<Pizza> preparing = new ArrayList<Pizza>();
    public List<Pizza> ovenWaiting = new ArrayList<Pizza>();
    public List<Pizza> ovenPreparing = new ArrayList<Pizza>();
    public List<Pizza> ready = new ArrayList<Pizza>();

    // Constructor 
    public Order(String person, int pizzas, int deliveryTime, int priority) {
        this.person = person;
        this.pizzas = pizzas;
        this.deliveryTime = deliveryTime;
        this.priority = priority;
        this.inProgress = false;

        // Create the pizzas 
        for (int i = 0; i < pizzas; i++) {
            this.pending.add(new Pizza());
        }
    }

    // Getters and Setters 
    public String getPerson() { return person; }
    public int getSize() { return pizzas; }
    public int getPriority() { return priority; }

    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public boolean getInProgress() { return inProgress; }
    public void toggleInProgress() { this.inProgress = !this.inProgress; }

    public boolean getDelivering() { return delivering; }
    public void toggleDelivering() { this.delivering = !this.delivering; }

    // Get State of the Order Function 
    public State getState() {
        if (!this.inProgress) return State.PENDING;
        if (preparing.size() > 0) return State.PREPARING;
        if (this.inProgress && pending.size() > 0) return State.CHEF_WAITING;
        if (ovenPreparing.size() > 0) return State.OVEN_PREPARING;
        if (ovenWaiting.size() > 0) return State.OVEN_WAITING;
        if (ready.size() == pizzas && this.deliveryTime > 0) return State.DRIVER_WAITING;
        if (deliveryTime == 0) return State.DELIVERED;
        return State.PENDING;
    }

    // Decrement Delivery Time Function
    public void decrementDeliveryTime() { this.deliveryTime--; }

    // Stringify the Order - Function 
    public String toString(int chefTime, int ovenTime) {
        int stageCompleted = 0;
        int stageInProgress = 0;
        int stageTimeLeft = 0;
        String stage = "";

        switch(getState()) {
            case PENDING:
                stage = "PENDING";
                stageInProgress = pending.size() + preparing.size();
                stageCompleted = ovenWaiting.size();
                stageTimeLeft = pending.size() * chefTime;                  
                break;
            case PREPARING:
                stage = "PREPARING";
                stageInProgress = preparing.size() + pending.size();
                stageCompleted = ovenWaiting.size();
                for (Pizza pizza : pending) {
                    if (pizza.getTimeLeft() == 0) stageTimeLeft += chefTime;    // Hasn't been started already
                    else stageTimeLeft += pizza.getTimeLeft();                  // Has been started already
                }
                for (Pizza pizza : preparing) { stageTimeLeft += pizza.getTimeLeft(); }
                break;
            case CHEF_WAITING:
                stage = "CHEF_WAITING";
                stageInProgress = pending.size() + preparing.size();
                stageCompleted = ovenWaiting.size();
                for (Pizza pizza : pending) {
                    if (pizza.getTimeLeft() == 0) stageTimeLeft += chefTime;    // Hasn't been started already
                    else stageTimeLeft += pizza.getTimeLeft();                  // Has been started already
                }
                break;
            case OVEN_PREPARING:
                stage = "OVEN_PREPARING";
                stageInProgress = ovenPreparing.size() + ovenWaiting.size();
                stageCompleted = ready.size();
                for (Pizza pizza : ovenPreparing) { stageTimeLeft += pizza.getTimeLeft(); }
                stageTimeLeft += ovenWaiting.size() * ovenTime;
                break;
            case OVEN_WAITING:
                stage = "OVEN_WAITING";
                stageInProgress = ovenPreparing.size() + ovenWaiting.size();
                stageCompleted = ready.size();
                stageTimeLeft += ovenWaiting.size() * ovenTime;
                break;
            case DRIVER_WAITING:
                stage = "DRIVER_WAITING";
                stageInProgress = 0;
                stageCompleted = pizzas;
                stageTimeLeft = deliveryTime;
                break;
            case DELIVERED:
                stage = "DELIVERED";
                stageInProgress = 0;
                stageCompleted = pizzas;
                stageTimeLeft = 0;
                break;
        }
        return person + "," + stage + "," + stageCompleted + "," + stageInProgress + "," + stageTimeLeft;
    }
}
