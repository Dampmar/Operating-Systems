package components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import lib.*;

// AI Utilization: the CyclicBarrier and the completedSimulations variabels of this section and it's implications where developed with the help of AI.

public class SimulationThreads {
    private ExecutorService executorService;        // Threadpool containing executable threads
    private List<Simulation> simulations;           // List of simulation instances for threads
    private CyclicBarrier barrier;                  // Logical barrier for thread synchronization
    private volatile boolean allComplete = false;   // Flag to end execution
    private AtomicInteger completedSimulations = new AtomicInteger(0);

    public SimulationThreads(String[] inputFiles, int chefs, int ovens, int drivers, String strategy, int bakeTime, int chefTime, int chefQuantum) {
        this.executorService = Executors.newFixedThreadPool(inputFiles.length);
        this.simulations = new ArrayList<>();

        // Initialize simulations 
        for (int i = 0; i < inputFiles.length; i++) {
            simulations.add(new Simulation(inputFiles[i], chefs, ovens, drivers, strategy, bakeTime, chefTime, chefQuantum));
        }

        // Create barrier for synchronization: when all simulation threads reach the barrier, print the state of the restaurants
        this.barrier = new CyclicBarrier(simulations.size(), () -> {
            System.out.println("==== MINUTE " + simulations.get(0).getMinute() + " ====");
            for (int i = 0; i < simulations.size(); i++) {
                System.out.println("==== RESTAURANT " + i + " ====");
                simulations.get(i).printEndCycleState();
            }
            // Update global completion status 
            allComplete = completedSimulations.get() == simulations.size();
        });
    }

    private void runSimulation(int simulationIndex) {
        Simulation simulation = simulations.get(simulationIndex);   // Get Correct Simulation for Thread
        boolean isCompleted = false;

        while (!allComplete) {
            // Run a simulation cycle 
            simulation.cycle();

            // Check for completion status after running cycle 
            boolean hasUndeliveredOrders = simulation.getOrders().stream()
                .anyMatch(order -> order.getState() != State.DELIVERED);

            // If thread execution is complete, indicate it 
            if (!hasUndeliveredOrders && !isCompleted) {
                completedSimulations.incrementAndGet();
                isCompleted = true;
            }

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void run() {
        List<Future<?>> futures = new ArrayList<>();

        // Submit each simulation to a thread in the threadpool
        for (int i = 0; i < simulations.size(); i++) {
            final int simulationIndex = i;
            futures.add(executorService.submit(() -> runSimulation(simulationIndex)));
        }

        // Wait for all threads to complete, should all complete at the same time
        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
