package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;

public class DriverTest {
    private Driver driver;
    private Order order; 
    
    @Before 
    public void setUp() {
        driver = new Driver(0);
        order = new Order("Test Customer", 0, 30, 1);
        order.toggleInProgress();
    }

    @Test 
    public void testConstructor() {
        assertEquals(0, driver.getId());
        assertEquals(null, driver.getCurrentOrder());
    }

    @Test 
    public void testSetCurrentOrder() {
        driver.setCurrentOrder(order);
        assertEquals(order, driver.getCurrentOrder());
    }

    @Test 
    public void testDeliver() {
        driver.setCurrentOrder(order);
        assertEquals(30, driver.getCurrentOrder().getDeliveryTime());

        for (int i = 0; i < 29; i++) {
            driver.deliver();
            assertEquals(30 - i - 1, driver.getCurrentOrder().getDeliveryTime());
        }

        driver.deliver();
        assertEquals(0, order.getDeliveryTime());
        assertEquals(null, driver.getCurrentOrder());
        assertEquals(order.getState(), State.DELIVERED);
    }
}
