import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Seal.
 * Seals age, move, eat lobsters, and die.
 * 
 * @author David J. Barnes, Michael Kölling, 
 * Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27 (1)
 */
public class Seal extends Animal
{
    // Characteristics shared by all Seal (class variables).

    // The age at which a seal can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a seal can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a seal breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single lobster. In effect, this is the
    // number of steps a seal can go before it has to eat again.
    private static final int LOBSTER_FOOD_VALUE = 900;

    private static final int HUNGRY_FOOD_LEVEL = 60;

    /**
     * Create a Seal. A seal can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the seal will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seal(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(LOBSTER_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(LOBSTER_FOOD_VALUE);
        }
    }

    /**
     * This is what the seal does most of the time: it hunts for
     * lobsters. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSeals A list to return newly born seals.
     */
    public void act(List<Animal> newSeals, int step)
    {
        if(step % 730 == 0) {
            incrementAge();
        }
        
        if(isInfected()) {
            incrementInfectedCount();
        }

        incrementHunger();
        if(isAlive() && !Simulator.isDay()) {
            giveBirth(newSeals);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Look for lobsters adjacent to the current location.
     * Only the first live lobster is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        if(getFoodLevel() < HUNGRY_FOOD_LEVEL){

            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getAnimalAt(where);
                if(animal instanceof Lobster) {
                    Lobster lobster = (Lobster) animal;
                    if(lobster.isAlive()) { 
                        lobster.setDead();
                        setFoodLevel(LOBSTER_FOOD_VALUE);
                        return where;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getAnimalAt(where);
            if(animal instanceof Seal) {
                infect((Seal) animal);
                
                Seal seal = (Seal) animal;
                if(seal.isAlive()) { 
                    if(this.isMale() != seal.isMale()) {
                        return super.canBreed();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check whether or not this seal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeals A list to return newly born seals.
     */
    private void giveBirth(List<Animal> newSeals)
    {
        // New seals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seal young = new Seal(false, field, loc);
            newSeals.add(young);
        }
    }

    public int getMaxAge()
    {
        return MAX_AGE;
    }

    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
}
