package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import lib.*;
import components.*;
import java.util.*;

public class FileParserTest {
    private String testFileName;
    private List<Order> orders;

    @Before 
    public void setUp() {
        testFileName = "TestingFile.txt";
        orders = FileParser.read_file(testFileName);
    }

    @Test
    public void testReadFileNotEmpty() {
        assertNotNull("Orders list should not be null", orders);
        assertTrue("Orders list should not be empty", orders.size() > 0);
    }

    @Test
    public void testOrderProperties() {
        Order firstOrder = orders.get(0);
        
        assertEquals("Person name should match", "John Doe", firstOrder.getPerson());
        assertEquals("Number of pizzas should match", 2, firstOrder.getSize());
        assertEquals("Delivery time should match", 30, firstOrder.getDeliveryTime());
        assertEquals("Priority should match", 1, firstOrder.getPriority());
    }
}