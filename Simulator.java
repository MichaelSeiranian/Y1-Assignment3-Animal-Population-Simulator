import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Lobsters, Seals, Eels, Tuna, Surgeonfish, Seaweed and Algae.
 * 
 * It also simulates the time of day, weather and disease.
 * 
 * @author David J. Barnes, Michael Kölling,
 * Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2021.02.27 (1)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a Seal will be created in any given grid position.
    private static final double SEAL_CREATION_PROBABILITY = 0.04;
    // The probability that an Eel will be created in any given grid position.
    private static final double EEL_CREATION_PROBABILITY = 0.05;
    // The probability that a Tuna will be created in any given grid position.
    private static final double TUNA_CREATION_PROBABILITY = 0.05;
    // The probability that a lobster will be created in any given grid position.
    private static final double LOBSTER_CREATION_PROBABILITY = 0.10;
    // The probability that a Surgeonfish will be created in any given grid position.
    private static final double SURGEONFISH_CREATION_PROBABILITY = 0.08;
    
    private static final double SEAWEED_CREATION_PROBABILITY = 0.25;
    
    private static final double ALGA_CREATION_PROBABILITY = 0.25;


    // List of animals in the field.
    private List<Animal> animals;
    
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;

    private static boolean day;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    private Weather weather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        weather = new Weather();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Lobster.class, Color.RED);
        view.setColor(Seal.class, Color.GRAY);
        view.setColor(Surgeonfish.class, Color.BLUE);
        view.setColor(Eel.class, Color.GREEN);
        view.setColor(Tuna.class, Color.ORANGE);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * seal and lobster.
     */
    public void simulateOneStep()
    {
        step++;

        changeTimeOfDay();
        
        if(step % 2 == 0)
        {
            weather.changeWeather();
        }

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, step);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(weather, step);
        }        

        // Add the newly born seales and lobsters to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }

    private void changeTimeOfDay()
    {
        if(day) {
            day = false;
        }
        else {
            day = true;
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with seals and lobsters.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SEAL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seal seal = new Seal(true, field, location);
                    animals.add(seal);
                }
                else if(rand.nextDouble() <= LOBSTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lobster lobster = new Lobster(true, field, location);
                    animals.add(lobster);
                }
                else if(rand.nextDouble() <= EEL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Eel eel = new Eel(true, field, location);
                    animals.add(eel);
                }
                else if(rand.nextDouble() <= TUNA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tuna tuna = new Tuna(true, field, location);
                    animals.add(tuna);
                }
                else if(rand.nextDouble() <= SURGEONFISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Surgeonfish surgeonfish = new Surgeonfish(true, field, location);
                    animals.add(surgeonfish);
                }
                else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    plants.add(seaweed);
                }
                else if(rand.nextDouble() <= ALGA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Alga alga = new Alga(true, field, location);
                    plants.add(alga);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    public static boolean isDay()
    {
        return day;
    }
    
}
