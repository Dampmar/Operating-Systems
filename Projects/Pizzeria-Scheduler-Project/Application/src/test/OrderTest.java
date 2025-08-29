package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;

public class OrderTest {
    private Order order; 

    @Before 
    public void setUp() {
        order = new Order("John", 1, 30, 1);
    }

    @Test 
    public void testConstructor() {
        assertEquals("John", order.getPerson());
        assertEquals(1, order.getSize());
        assertEquals(30, order.getDeliveryTime());
        assertEquals(1, order.getPriority());
        assertEquals(false, order.getInProgress());
    }

    @Test 
    public void testQueuesInitState() {
        assertEquals(1, order.pending.size());
        assertEquals(0, order.preparing.size());
        assertEquals(0, order.ovenWaiting.size());
        assertEquals(0, order.ovenPreparing.size());
        assertEquals(0, order.ready.size());
    }

    @Test 
    public void testOrderStateTransitions() {
        Pizza pizza = order.pending.get(0);
        assertEquals(State.PENDING, order.getState());

        order.toggleInProgress();
        order.pending.remove(pizza);
        order.preparing.add(pizza);
        assertEquals(State.PREPARING, order.getState());

        order.preparing.remove(pizza);
        order.ovenWaiting.add(pizza);
        assertEquals(State.OVEN_WAITING, order.getState());

        order.ovenWaiting.remove(pizza);
        order.ovenPreparing.add(pizza);
        assertEquals(State.OVEN_PREPARING, order.getState());

        order.ovenPreparing.remove(pizza);
        order.ready.add(pizza);
        assertEquals(State.DRIVER_WAITING, order.getState());

        order.setDeliveryTime(0);
        assertEquals(State.DELIVERED, order.getState());

        // Reset the order state to Chef Waiting 
        Pizza pizza2 = new Pizza();
        order.pending.add(pizza2);
        order.ready.remove(pizza);
        order.ovenWaiting.add(pizza);
        order.setDeliveryTime(30);
        assertEquals(State.CHEF_WAITING, order.getState());
    }
}
