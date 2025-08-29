package lib;

public class Driver {
    private int id;
    private Order currentOrder;

    public Driver(int id) {
        this.id = id;
        this.currentOrder = null;
    }

    public int getId() { return id; }
    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }

    // Function in charge of delivering the order
    public void deliver() {
        if (currentOrder == null) return;
        int newTime = currentOrder.getDeliveryTime() - 1;
        currentOrder.setDeliveryTime(newTime);
        if (newTime == 0) {
            currentOrder = null;
        }
    }
}
