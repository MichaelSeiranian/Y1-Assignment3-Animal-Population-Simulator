import java.util.Iterator;
import java.util.List;

/**
 * Write a description of class Tuna here.
 *
 * @author Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27 (1)
 */
public class Tuna extends Animal
{
    // Characteristics shared by all surgeonfish (class variables).

    // The age at which a surgeonfish can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a surgeonfish can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a surgeonfish breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single surgeonfish. In effect, this is the
    // number of steps a surgeonfish can go before it has to eat again.
    private static final int SURGEONFISH_FOOD_VALUE = 600;

    private static final int HUNGRY_FOOD_LEVEL = 60;

    /**
     * Create a tuna. A tuna can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the tuna will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tuna(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(SURGEONFISH_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(SURGEONFISH_FOOD_VALUE);
        }
    }

    /**
     * This is what the tuna does most of the time: it hunts for
     * surgeonfishs. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newtunas A list to return newly born tunas.
     */
    public void act(List<Animal> newTunas, int step)
    {
        if(step % 730 == 0) {
            incrementAge();
        }
        
        if(isInfected()) {
            incrementInfectedCount();
        }

        incrementHunger();
        if(isAlive() && Simulator.isDay()) {
            giveBirth(newTunas);            
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
     * Look for surgeonfishs adjacent to the current location.
     * Only the first live surgeonfish is eaten.
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
                if(animal instanceof Surgeonfish) {
                    Surgeonfish surgeonfish = (Surgeonfish) animal;
                    if(surgeonfish.isAlive()) { 
                        surgeonfish.setDead();
                        setFoodLevel(SURGEONFISH_FOOD_VALUE);
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
            if(animal instanceof Tuna) {
                infect((Tuna) animal);
                
                Tuna tuna = (Tuna) animal;
                if(tuna.isAlive()) { 
                    if(this.isMale() != tuna.isMale()) {
                        return super.canBreed();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check whether or not this tuna is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newtunas A list to return newly born tunas.
     */
    private void giveBirth(List<Animal> newTunas)
    {
        // New tunas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Tuna young = new Tuna(false, field, loc);
            newTunas.add(young);
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
