import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a Surgeonfish.
 * Surgeonfish age, move, breed, and die.
 *
 * @author Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27 (1)
 */
public class Surgeonfish extends Animal
{
    // Characteristics shared by all surgeonfishs (class variables).

    // The age at which a surgeonfish can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a surgeonfish can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a surgeonfish breeding.
    private static final double BREEDING_PROBABILITY = 0.50;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single alga. In effect, this is the
    // number of steps a surgeonfish can go before it has to eat again.
    private static final int ALGA_FOOD_VALUE = 45;

    private static final int HUNGRY_FOOD_LEVEL = 35;
    
    /**
     * Create a new surgeonfish. A surgeonfish may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the surgeonfish will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Surgeonfish(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(ALGA_FOOD_VALUE));
        }
        else {
            setFoodLevel(ALGA_FOOD_VALUE);
        }
    }

    /**
     * This is what the lobster does most of the time - it swims 
     * around. Sometimes it will breed or die of old age.
     * @param newLobsters A list to return newly born lobsters.
     */
    public void act(List<Animal> newSurgeonfish, int step)
    {
        if(step % 730 == 0) {
            incrementAge();
        }
        
        if(isInfected()) {
            incrementInfectedCount();
        }
        
        incrementHunger();
        if(isAlive() && Simulator.isDay()) {
            giveBirth(newSurgeonfish);            
            
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
     * Look for algae adjacent to the current location.
     * Only the first algae is eaten.
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
                if(plant instanceof Alga) {
                    Alga alga = (Alga) plant;
                    
                    setFoodLevel(getFoodLevel() + (alga.getGrowthLevel() * ALGA_FOOD_VALUE));
                    alga.resetGrowth();
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
    private void giveBirth(List<Animal> newSurgeonfish)
    {
        // New lobsters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Surgeonfish young = new Surgeonfish(false, field, loc);
            newSurgeonfish.add(young);
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
            if(animal instanceof Surgeonfish) {
                infect((Surgeonfish) animal);
                
                Surgeonfish surgeonfish = (Surgeonfish) animal;
                if(surgeonfish.isAlive()) { 
                    if(this.isMale() != surgeonfish.isMale()) {
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
