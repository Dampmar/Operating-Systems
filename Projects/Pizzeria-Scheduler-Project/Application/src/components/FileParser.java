package components;

import java.io.*;
import java.util.*;

import lib.Order;

public class FileParser {
    private static final String BASE_PATH_STRING = "tests/";
    public static List<Order> read_file(String fileName) {
        List<Order> orders = new ArrayList<>();
        File file = new File(BASE_PATH_STRING + fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; 
            while ((line = br.readLine()) != null) 
            {   
                // Read Contents of File
                String[] orderDetails = line.split(",");
                String person = orderDetails[0];
                int numberOfPizzas = Integer.parseInt(orderDetails[1]);
                int deliveryTime = Integer.parseInt(orderDetails[2]);
                int priority = Integer.parseInt(orderDetails[3]);

                // Create a List Order 
                Order order = new Order(person, numberOfPizzas, deliveryTime, priority);
                orders.add(order);
            }

        } catch (IOException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }
        
        return orders; 
    }
}
