import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Lobster.
 * Lobsters age, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling, 
 * Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27 (1)
 */
public class Lobster extends Animal
{
    // Characteristics shared by all Lobsters (class variables).

    // The age at which a Lobster can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a Lobster can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a Lobster breeding.
    private static final double BREEDING_PROBABILITY = 0.50;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single seaweed. In effect, this is the
    // number of steps a lobster can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 45;
    
    private static final int HUNGRY_FOOD_LEVEL = 35;
    
    // Individual characteristics (instance fields).
    

    /**
     * Create a new Lobster. A Lobster may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Lobster will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lobster(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(SEAWEED_FOOD_VALUE));
        }
        else{
            setFoodLevel(SEAWEED_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the lobster does most of the time - it swims 
     * around. Sometimes it will breed or die of old age.
     * @param newLobsters A list to return newly born lobsters.
     */
    public void act(List<Animal> newLobsters, int step)
    {
        if(step % 730 == 0) {
            incrementAge();
        }
        
        if(isInfected()) {
            incrementInfectedCount();
        }
        
        incrementHunger();
        if(isAlive() && !Simulator.isDay()) {
            giveBirth(newLobsters);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
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
     * Look for seaweed adjacent to the current location.
     * Only the first seaweed is eaten.
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
                Object plant = field.getPlantAt(where);
                if(plant instanceof Seaweed) {
                    Seaweed seaweed = (Seaweed) plant;
                    
                    setFoodLevel(getFoodLevel() + (seaweed.getGrowthLevel() * SEAWEED_FOOD_VALUE));
                    seaweed.resetGrowth();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this lobster is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLobsters A list to return newly born lobsters.
     */
    private void giveBirth(List<Animal> newLobsters)
    {
        // New lobsters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lobster young = new Lobster(false, field, loc);
            newLobsters.add(young);
        }
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
            if(animal instanceof Lobster) {
                infect((Lobster) animal);
                
                Lobster lobster = (Lobster) animal;
                if(lobster.isAlive()) { 
                    if(this.isMale() != lobster.isMale()) {
                        return super.canBreed();
                    }
                }
            }
        }
        return false;
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
