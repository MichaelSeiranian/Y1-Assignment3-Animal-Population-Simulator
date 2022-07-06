import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes, Michael Kölling, 
 * Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27 (1)
 */
public abstract class Animal
{
    private static final double INFECTION_PROBABILITY = 0.005;
    
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    
    // The animal's age.
    private int age;
    
    // Individual characteristics (instance fields).
    // The animal's food level, which is increased by eating food.
    private int foodLevel;
    
    private boolean male;
    
    private boolean infected;
    
    private int infectedCount;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        infectedCount = 0;
        
        int chance = rand.nextInt(2);
        if(chance == 0) {
            male = true;
        }
        else {
            male = false;
        }
        
        if(rand.nextDouble() <= INFECTION_PROBABILITY) {
            infected = true;
        } else {
            infected = false;
        }
        
        setLocation(location);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals, int step);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    protected void setInfected()
    {
        infected = true;
    }
    
    protected void setHealthy()
    {
        double chance = rand.nextDouble();
        if(chance <= 0.05){
            infected = false;
            infectedCount = 0;
        }
    }
    
    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    protected void incrementInfectedCount(){
        infectedCount++;
        if(infectedCount >= 20) {
            setDead();
        } else {
            setHealthy();
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation, 0);
    }
    
    public void setAge(int newAge)
    {
        age = newAge;
    }
    
    public void setFoodLevel(int newFoodLevel)
    {
        foodLevel = newFoodLevel;
    }
    
    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    protected void infect(Animal animal)
    {
        if(rand.nextInt(2) == 0) {
            if(isInfected()) {
                animal.setInfected();
            }
            else if (animal.isInfected()){
                setInfected();
            }
        }

    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    public int getAge()
    {
        return age;
    }
    
    public int getFoodLevel()
    {
        return foodLevel;
    }
    
    public boolean isMale()
    {
        return male;
    }
    
    public boolean isInfected()
    {
        return infected;
    }
    
    abstract public int getMaxAge();
    
    abstract public double getBreedingProbability();
    
    abstract public int getMaxLitterSize();
    
    abstract public int getBreedingAge();
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
    
    /**
     * An animal can breed if it has reached the breeding age.
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {   
        return getAge() >= getBreedingAge();
    }
    
    public Random getRandom()
    {
        return rand;
    }
}
