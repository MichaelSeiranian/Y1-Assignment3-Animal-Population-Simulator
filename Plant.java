import java.util.Random;

/**
 * Write a description of class Plant here.
 *
 * @author Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27
 */
public abstract class Plant
{
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    
    private int growthLevel;
    
    
    // A shared random number generator to control growth.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Plant
     */
    public Plant(Field field, Location location)
    {
        this.field = field;
        
        setLocation(location);
    }

    public abstract void act(Weather weather, int step);
    
    public abstract int getMaxGrowthLevel();
    
    
    public Random getRandom()
    {
        return rand;
    }
    
    public int getGrowthLevel()
    {
        return growthLevel;
    }
    
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        location = newLocation;
        field.place(this, newLocation, 1);
    }
    
    protected void incrementGrowth(Weather weather)
    {
        if((growthLevel < getMaxGrowthLevel()) && (Simulator.isDay()) && (!weather.isRainy())) {
            if(weather.isSunny()){
                growthLevel++;
            }
            growthLevel++;
        }
    }
    
    protected void resetGrowth()
    {
        growthLevel = 0;
    }
    
    protected void setGrowthLevel(int newGrowthLevel)
    {
        growthLevel = newGrowthLevel;
    }
}
