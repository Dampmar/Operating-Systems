package lib;

import java.util.List; 

public class SchedulerFocused extends Scheduler{
    
    public SchedulerFocused(List<Order> orders, int chefs, int ovens, int drivers, String strategy, int bakeTime, int chefTime, int chefQuantum) {
        super(orders, chefs, ovens, drivers, "FOCUSED", bakeTime, chefTime, chefQuantum);
    }

    @Override 
    protected void schedule() {
        // Schedule Pizza's to Chefs 
        for (Chef chef : chefs) 
            if (chef.getCurrentOrder() == null)
                for (Order order : orders) 
                    if (!order.pending.isEmpty()) {
                        // Begin Preparation
                        Pizza pizza = order.pending.remove(0);
                        order.preparing.add(pizza);
                        if (!order.getInProgress()) order.toggleInProgress();

                        // Assignments to chef
                        chef.setCurrentOrder(order);
                        chef.setCurrentPizza(pizza);
                        chef.setTimePreparing(0);

                        // Set Preparation Time 
                        pizza.setTimeLeft(chefTime);
                        break;
                    }
        
        for (Oven oven : ovens) 
            if (oven.getCurrentOrder() == null)
                for (Order order : orders)
                    if ((!order.ovenWaiting.isEmpty() && order.getState() == State.OVEN_PREPARING) || order.getState() == State.OVEN_WAITING) {
                        // Begin Baking 
                        Pizza pizza = order.ovenWaiting.remove(0);
                        order.ovenPreparing.add(pizza);

                        // Assignments to oven
                        oven.setCurrentOrder(order);
                        oven.setCurrentPizza(pizza);

                        // Set Preparation Time 
                        pizza.setTimeLeft(bakeTime);
                        break;
                    }
        
        for (Driver driver : drivers) 
            if (driver.getCurrentOrder() == null) 
                for (Order order : orders) 
                    if (order.ready.size() == order.getSize() && order.getState() == State.DRIVER_WAITING && !order.getDelivering()) {
                        order.toggleDelivering();
                        driver.setCurrentOrder(order);
                        break;
                    }
    }
}
