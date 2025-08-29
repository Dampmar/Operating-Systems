package lib;

public class Chef {
    private int id;
    private Order currentOrder;
    private Pizza currentPizza;
    private int timePreparing;

    public Chef(int id) {
        this.id = id;
        this.currentOrder = null;
        this.currentPizza = null;
        this.timePreparing = 0;
    }

    // Getters and Setters
    public int getId() { return id; }

    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }

    public Pizza getCurrentPizza() { return currentPizza; }
    public void setCurrentPizza(Pizza pizza) { this.currentPizza = pizza; }

    public int getTimePreparing() { return timePreparing; }
    public void setTimePreparing(int timePreparing) { this.timePreparing = timePreparing; }

    // Prepare Pizza Function 
    public void prepare() {
        if (currentPizza == null) return;

        currentPizza.setTimeLeft(currentPizza.getTimeLeft() - 1);
        timePreparing++;
        if (currentPizza.getTimeLeft() == 0) {
            // Move the pizza to waiting for oven
            currentOrder.preparing.remove(currentPizza);
            currentOrder.ovenWaiting.add(currentPizza);

            // Set the current pizza to null
            currentOrder = null;
            currentPizza = null;
            timePreparing = 0;
        }
    }

    // Preempt Pizza Function 
    public void preempt() {
        if (currentPizza == null) return;
        // Move the pizza back to pending
        currentOrder.pending.add(currentPizza);
        currentOrder.preparing.remove(currentPizza);

        // Set the current pizza to null
        currentOrder = null;
        currentPizza = null;
        timePreparing = 0;
    }
}
