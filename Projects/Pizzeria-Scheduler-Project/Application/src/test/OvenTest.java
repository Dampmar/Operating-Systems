package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;

public class OvenTest {
    private Oven oven;
    private Order order;
    private Pizza pizza;

    @Before
    public void setUp() {
        oven = new Oven(0);
        order = new Order("Test Customer", 1, 30, 1);
        pizza = order.pending.get(0);
    }

    @Test 
    public void testConstructor() {
        assertEquals(0, oven.getId());
        assertNull(oven.getCurrentOrder());
        assertNull(oven.getCurrentPizza());
    }

    @Test 
    public void testAssignments() {
        oven.setCurrentOrder(order);
        oven.setCurrentPizza(pizza);

        assertEquals(order, oven.getCurrentOrder());
        assertEquals(pizza, oven.getCurrentPizza());
    }

    @Test 
    public void testCooking() {
        oven.setCurrentOrder(order);
        oven.setCurrentPizza(pizza);

        pizza.setTimeLeft(5);

        order.pending.remove(pizza);
        order.ovenPreparing.add(pizza);
        order.toggleInProgress();

        assertEquals(5, pizza.getTimeLeft());
        assertEquals(1, order.ovenPreparing.size());

        for (int i = 0; i < 4; i++) {
            oven.cook();
            assertTrue(oven.getCurrentPizza() != null);
            assertEquals(5 - i - 1, pizza.getTimeLeft());
        }

        oven.cook();

        assertNull(oven.getCurrentOrder());
        assertNull(oven.getCurrentPizza());
        assertTrue(order.ready.contains(pizza));
        assertFalse(order.ovenPreparing.contains(pizza));
    }
}
