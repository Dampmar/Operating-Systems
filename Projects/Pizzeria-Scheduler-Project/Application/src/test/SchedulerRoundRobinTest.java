package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerRoundRobinTest {
    private Scheduler scheduler;
    private List<Order> orders;
    private List<Chef> chefs;
    private List<Oven> ovens;
    private List<Driver> drivers;

    @Before 
    public void setUp() {
        orders = new ArrayList<>();
        orders.add(new Order("Maria", 3, 20, 0));
        scheduler = new SchedulerRoundRobin(orders, 
            2,
            2,
            2,
            "RR",
            10, 
            6, 
            3
        );

        // Variable Names
        ovens = scheduler.getOvens();
        chefs = scheduler.getChefs();
        orders = scheduler.getOrders();
        drivers = scheduler.getDrivers();
    }

    @Test 
    public void testRoundRobinScheduling() {
        // Keep track of Pizzas 
        int cycles = 0;
        Pizza pizza0 = orders.get(0).pending.get(0);
        Pizza pizza1 = orders.get(0).pending.get(1);
        Pizza pizza2 = orders.get(0).pending.get(2);

        // First Scheduling 
        scheduler.cycle();
        cycles++;
        for (Chef chef : chefs) {
            assertEquals(5, chef.getCurrentPizza().getTimeLeft());
            assertEquals(chef.getTimePreparing(), 1);
            assertTrue(chef.getCurrentOrder().getPerson().equals("Maria"));
        }
        assertTrue(chefs.get(0).getCurrentPizza() == pizza0 && chefs.get(1).getCurrentPizza() == pizza1);

        // Reach first quantum
        for (int i = 0; i < 2; i++) { scheduler.cycle(); cycles++; } 
        for (Chef chef : chefs) {
            assertEquals(3, chef.getCurrentPizza().getTimeLeft());
            assertEquals(chef.getTimePreparing(), 3);
            assertTrue(chef.getCurrentOrder().getPerson().equals("Maria"));
        }

        // Preemption should take place
        scheduler.cycle();
        cycles++;
        assertTrue(chefs.get(0).getCurrentPizza() == pizza2 && chefs.get(1).getCurrentPizza() == pizza0);
        for (Chef chef : chefs) assertEquals(chef.getTimePreparing(), 1);
        assertEquals(chefs.get(0).getCurrentPizza().getTimeLeft(), 5);          // Pizza 2 = 5 cycles missing
        assertEquals(chefs.get(1).getCurrentPizza().getTimeLeft(), 2);          // Pizza 0 = 2 cycles missing

        // Complete Second Time Quantum
        for (int i = 0; i < 2; i++) { scheduler.cycle(); cycles++; } 
        assertEquals(chefs.get(0).getTimePreparing(), 3);
        assertEquals(chefs.get(1).getTimePreparing(), 0);
        assertEquals(chefs.get(0).getCurrentPizza().getTimeLeft(), 3);          // Pizza 2 = 3 cycles missing
        assertEquals(chefs.get(1).getCurrentPizza(), null);                     // Pizza 0 = complete (null)

        // Preemption should take place
        scheduler.cycle();
        cycles++; 
        assertEquals(chefs.get(1).getTimePreparing(), 1);
        assertEquals(chefs.get(0).getTimePreparing(), 1);
        assertEquals(chefs.get(1).getCurrentPizza().getTimeLeft(), 2);          // Pizza 1 = 2 cycles missing
        assertEquals(chefs.get(0).getCurrentPizza().getTimeLeft(), 2);          // Pizza 2 = 2 cycles missing

        for (int i = 0; i < 2; i++) { scheduler.cycle(); cycles++; } 
        assertEquals(chefs.get(0).getCurrentPizza(), null);                     // Pizza 0 = complete (null)
        assertEquals(chefs.get(1).getCurrentPizza(), null);                     // Pizza 0 = complete (null)
        assertTrue(orders.get(0).getState().equals(State.OVEN_WAITING));
        assertEquals(cycles, 9);

        /*  Instead of Taking the expected 12 cycles to complete the preparation of the pizzas as would happen in FOCUSED, it takes 1/4 of the time
         *  thus proving more efficient
        */

        // Rest of Processing should remain the same 
        // Third Scheduling 
        scheduler.cycle();
        assertTrue(orders.get(0).getState().equals(State.OVEN_PREPARING));

        for (Oven oven : ovens) {
            assertEquals(oven.getCurrentPizza().getTimeLeft(), 9);
            assertEquals(oven.getCurrentOrder().getPerson(), "Maria");
        }

        // Complete First Baking Phase
        for (int i = 0; i < 9; i++) scheduler.cycle();

        for (Oven oven : ovens) assertNull(oven.getCurrentPizza());
        assertTrue(orders.get(0).ovenWaiting.size() == 1);

        // Fourth Scheduling 
        scheduler.cycle();
        for (Order order : orders) assertTrue(order.getState().equals(State.OVEN_PREPARING));
        assertTrue(ovens.get(0).getCurrentOrder().getPerson().equals("Maria"));

        // Complete Second Batch Baking Phase 
        for (int i = 0; i < 9; i++) scheduler.cycle();
        for (Oven oven : ovens) assertNull(oven.getCurrentPizza());
        for (Order order : orders) assertTrue(order.getState().equals(State.DRIVER_WAITING));
        for (Driver driver : drivers) assertNull(driver.getCurrentOrder());

        // Fifth Scheduling - Begin Delivery Phase 
        scheduler.cycle();
        assertTrue(drivers.get(0).getCurrentOrder().getPerson().equals("Maria"));
        assertEquals(drivers.get(0).getCurrentOrder().getDeliveryTime(), 19);

        // Deliver Maria's Order 
        for (int i = 0; i < 19; i++) scheduler.cycle();
        assertNull(drivers.get(0).getCurrentOrder());
        assertTrue(orders.get(0).getState().equals(State.DELIVERED));

        assertEquals(scheduler.getMinute(), 49);
    }
}
