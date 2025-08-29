import lib.*;
import java.util.*;
import components.SimulationThreads;

public class ThreadedApp {
    public static void main(String[] args) throws Exception {
        String[] inputFiles = {"FocusedBasic.txt", "ThreadedBasic.txt"};
        String strategy = "RR";
        int chefs = 2;
        int ovens = 2;
        int drivers = 2;
        int bakeTime = 10;
        int chefTime = 5;
        int chefQuantum = 3;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input-files":
                    if (i + 1 < args.length) {
                        inputFiles = args[++i].split(",");
                    }
                    break;
                case "--available-ovens":
                    if (i + 1 < args.length) ovens = Integer.parseInt(args[++i]);
                    break;
                case "--available-chefs":
                    if (i + 1 < args.length) chefs = Integer.parseInt(args[++i]);
                    break;
                case "--available-drivers":
                    if (i + 1 < args.length) drivers = Integer.parseInt(args[++i]);
                    break;
                case "--bake-time":
                    if (i + 1 < args.length) bakeTime = Integer.parseInt(args[++i]);
                    break;
                case "--chef-time":
                    if (i + 1 < args.length) chefTime = Integer.parseInt(args[++i]);
                    break;
                case "--chef-strategy":
                    if (i + 1 < args.length) strategy = args[++i];
                    break;
                case "--chef-quantum":
                    if (i + 1 < args.length) chefQuantum = Integer.parseInt(args[++i]);
                    break;
                case "--help":
                    printHelp();
                    return;
                default:
                    System.out.println("Unknown option: " + args[i]);
                    printHelp();
                    return;
            }
        }

        try {
            // Create and run threaded simulation
            SimulationThreads simulation = new SimulationThreads(
                inputFiles,
                chefs,
                ovens,
                drivers,
                strategy,
                bakeTime,
                chefTime,
                chefQuantum
            );
            simulation.run();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("Threaded Pizza Scheduler Usage:");
        System.out.println("java -jar scheduler.jar [options]");
        System.out.println("\nOptions:");
        System.out.println("  --input-files <files>             Comma-separated list of input files");
        System.out.println("  --available-chefs <number>        Number of chefs per restaurant (default: 2)");
        System.out.println("  --available-ovens <number>        Number of ovens per restaurant (default: 2)");
        System.out.println("  --available-drivers <number>      Number of drivers per restaurant (default: 2)");
        System.out.println("  --chef-strategy <type>            Scheduling strategy: FOCUSED or RR (default: RR)");
        System.out.println("  --bake-time <minutes>             Time to bake a pizza (default: 10)");
        System.out.println("  --chef-time <minutes>             Time for chef to prepare a pizza (default: 5)");
        System.out.println("  --chef-quantum <minutes>               Time quantum for RR strategy (default: 3)");
        System.out.println("  --help                            Show this help message");
    }
} 
