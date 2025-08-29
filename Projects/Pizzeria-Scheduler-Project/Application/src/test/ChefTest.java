package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;

public class ChefTest {
    private Chef chef; 
    private Order order; 
   
    @Before 
    public void setUp() {
        chef = new Chef(0);
        order = new Order("John", 1, 20, 0);
    }

    @Test 
    public void testConstructor() {
        assertEquals(0, chef.getId());
        assertEquals(null, chef.getCurrentOrder());
        assertEquals(null, chef.getCurrentPizza());
        assertEquals(0, chef.getTimePreparing());
    }

    @Test 
    public void testOrderAndPizzaSetters() {
        chef.setCurrentOrder(order);
        assertEquals(order, chef.getCurrentOrder());

        Pizza pizza = order.pending.get(0);
        chef.setCurrentPizza(pizza);
        assertEquals(pizza, chef.getCurrentPizza());
    }

    @Test 
    public void testPrepare() {
        Pizza pizza = order.pending.get(0);
        pizza.setTimeLeft(5);               // Set the Chef Preparation Time to 5

        chef.setCurrentOrder(order);
        chef.setCurrentPizza(pizza);
        
        assertEquals(order, chef.getCurrentOrder());
        assertEquals(pizza, chef.getCurrentPizza());
        assertEquals(pizza.getTimeLeft(), 5);
        assertEquals(0, chef.getTimePreparing());

        order.pending.remove(pizza);
        order.preparing.add(pizza);

        // Prepare the pizza 4 times
        for (int i = 0; i < 4; i++) {
            chef.prepare();
            assertTrue("Chef should still have pizza", chef.getCurrentPizza() != null);
            assertTrue("Pizza should still be preparing", order.preparing.contains(pizza));
        }

        // Prepare the pizza 1 more time, finishing it 
        chef.prepare();

        // Chef should have finished preparing the pizza
        assertEquals(null, chef.getCurrentPizza());
        assertEquals(null, chef.getCurrentOrder());
        assertEquals(0, chef.getTimePreparing());
        assertEquals(0, pizza.getTimeLeft());

        // Check the order state
        assertEquals(0, order.pending.size());
        assertEquals(0, order.preparing.size());
        assertEquals(1, order.ovenWaiting.size());
    }

    @Test 
    public void testPreempt() {
        Pizza pizza = order.pending.get(0);
        pizza.setTimeLeft(5);               // Set the Chef Preparation Time to 5

        chef.setCurrentOrder(order);
        chef.setCurrentPizza(pizza);
        
        assertEquals(order, chef.getCurrentOrder());
        assertEquals(pizza, chef.getCurrentPizza());
        assertEquals(pizza.getTimeLeft(), 5);
        assertEquals(0, chef.getTimePreparing());

        order.pending.remove(pizza);
        order.preparing.add(pizza);

        // Prepare the pizza 4 times
        for (int i = 0; i < 4; i++) {
            chef.prepare();
            assertTrue("Chef should still have pizza", chef.getCurrentPizza() != null);
            assertTrue("Pizza should still be preparing", order.preparing.contains(pizza));
        }

        // Preempt the pizza
        chef.preempt();

        // Chef should have preempted the pizza
        assertEquals(null, chef.getCurrentPizza());
        assertEquals(null, chef.getCurrentOrder());
        assertEquals(0, chef.getTimePreparing());
        assertEquals(1, pizza.getTimeLeft());

        // Check the order state
        assertEquals(1, order.pending.size());
        assertEquals(0, order.preparing.size());
        assertEquals(0, order.ovenWaiting.size());
    }
}
