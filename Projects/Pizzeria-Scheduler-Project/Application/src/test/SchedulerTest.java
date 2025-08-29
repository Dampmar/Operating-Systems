package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerTest {
    private Scheduler scheduler;
    private List<Order> orders;

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
    }

    @Test
    public void testConstructor() {
        assertEquals(2, scheduler.getChefs().size());
        assertEquals(2, scheduler.getOvens().size());
        assertEquals(2, scheduler.getDrivers().size());
        assertEquals(2, scheduler.getOrders().size());
        assertEquals(0, scheduler.getMinute());
        assertEquals("FOCUSED", scheduler.getStrategy());
    }

    @Test
    public void testPriorityOrder() {
        // First order should be John's since it has higher priority
        Order firstOrder = scheduler.getOrders().get(0);
        assertEquals("Maria", firstOrder.getPerson());
        assertEquals(0, firstOrder.getPriority());

        // Second order should be Maria's with lower priority
        Order secondOrder = scheduler.getOrders().get(1);
        assertEquals("John", secondOrder.getPerson());
        assertEquals(1, secondOrder.getPriority());
    }
}
