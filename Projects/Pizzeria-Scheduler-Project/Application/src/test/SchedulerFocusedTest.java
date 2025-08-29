package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerFocusedTest {
    private Scheduler scheduler;
    private List<Order> orders;
    private List<Chef> chefs;
    private List<Oven> ovens;
    private List<Driver> drivers;
    
    @Before 
    public void setUp() {
        orders = new ArrayList<>();
        orders.add(new Order("John", 1, 30, 1));
        orders.add(new Order("Maria", 3, 20, 0));
        scheduler = new SchedulerFocused(orders, 
            2,
            2,
            2,
            "FOCUSED",
            10, 
            5, 
            0
        );

        // Variable Names
        ovens = scheduler.getOvens();
        chefs = scheduler.getChefs();
        orders = scheduler.getOrders();
        drivers = scheduler.getDrivers();
    }

    @Test
    public void testFocusedScheduling() {
        // First Scheduling 
        scheduler.cycle();
        for (Chef chef : chefs) {
            assertEquals(4, chef.getCurrentPizza().getTimeLeft());
            assertTrue(chef.getCurrentOrder().getPerson().equals("Maria"));
        }

        // Complete First Pizza Preparation
        for (int i = 0; i < 4; i++) scheduler.cycle();
        for (Chef chef : chefs) {
            assertNull(chef.getCurrentOrder());
            assertNull(chef.getCurrentPizza());
        }

        // Second Scheduling 
        scheduler.cycle();

        // First Chef, should be working on Maria's Order Still
        assertEquals(4, chefs.get(0).getCurrentPizza().getTimeLeft());
        assertTrue(chefs.get(0).getCurrentOrder().getPerson().equals("Maria"));

        // Second Chef, should be working on John's Order
        assertEquals(4, chefs.get(1).getCurrentPizza().getTimeLeft());
        assertTrue(chefs.get(1).getCurrentOrder().getPerson().equals("John"));

        // Complete Second Preparation Phase 
        for (int i = 0; i < 4; i++) scheduler.cycle();
        for (Chef chef : chefs) {
            assertNull(chef.getCurrentOrder());
            assertNull(chef.getCurrentPizza());
        }

        // Pizza Preparation Stage is Complete - Begin Baking 
        for (Order order : orders) assertTrue(order.getState().equals(State.OVEN_WAITING));
        
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
        assertTrue(orders.get(0).ovenWaiting.size() == 1 && orders.get(0).ready.size() == 2);

        // Fourth Scheduling 
        scheduler.cycle();
        for (Order order : orders) assertTrue(order.getState().equals(State.OVEN_PREPARING));
        assertTrue(ovens.get(0).getCurrentOrder().getPerson().equals("Maria"));
        assertTrue(ovens.get(1).getCurrentOrder().getPerson().equals("John"));

        // Complete Second Batch Baking Phase 
        for (int i = 0; i < 9; i++) scheduler.cycle();
        for (Oven oven : ovens) assertNull(oven.getCurrentPizza());
        for (Order order : orders) assertTrue(order.getState().equals(State.DRIVER_WAITING));
        for (Driver driver : drivers) assertNull(driver.getCurrentOrder());

        // Fifth Scheduling - Begin Delivery Phase 
        scheduler.cycle();
        assertTrue(drivers.get(0).getCurrentOrder().getPerson().equals("Maria"));
        assertTrue(drivers.get(1).getCurrentOrder().getPerson().equals("John"));
        assertEquals(drivers.get(1).getCurrentOrder().getDeliveryTime(), 29);
        assertEquals(drivers.get(0).getCurrentOrder().getDeliveryTime(), 19);

        // Deliver Maria's Order 
        for (int i = 0; i < 19; i++) scheduler.cycle();
        assertNull(drivers.get(0).getCurrentOrder());
        assertTrue(orders.get(0).getState().equals(State.DELIVERED));
        assertTrue(drivers.get(1).getCurrentOrder().getPerson().equals("John"));
        assertEquals(drivers.get(1).getCurrentOrder().getDeliveryTime(), 10);

        // Deliver John's Order 
        for (int i = 0; i < 10; i++) scheduler.cycle();
        assertNull(drivers.get(0).getCurrentOrder());
        assertTrue(orders.get(0).getState().equals(State.DELIVERED));
        assertNull(drivers.get(1).getCurrentOrder());
        assertTrue(orders.get(1).getState().equals(State.DELIVERED));

        assertEquals(scheduler.getMinute(), 60);
    }
}
