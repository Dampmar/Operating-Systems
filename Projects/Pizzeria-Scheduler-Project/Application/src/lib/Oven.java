package lib;

public class Oven {
    private int id;
    private Order currentOrder;
    private Pizza currentPizza;

    public Oven(int id) {
        this.id = id;
        this.currentOrder = null;
        this.currentPizza = null;
    }

    // Getters and Setters 
    public int getId() { return id; }
    
    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }

    public Pizza getCurrentPizza() { return currentPizza; }
    public void setCurrentPizza(Pizza pizza) { this.currentPizza = pizza; }

    // Function in charge of cooking 
    public void cook() {
        if (currentPizza == null) return;
        currentPizza.setTimeLeft(currentPizza.getTimeLeft() - 1);
        if (currentPizza.getTimeLeft() == 0) {
            // Move the pizza to waiting for oven
            currentOrder.ovenPreparing.remove(currentPizza);
            currentOrder.ready.add(currentPizza);

            // Set the current pizza to null
            currentOrder = null;
            currentPizza = null;
        }
    }
}
