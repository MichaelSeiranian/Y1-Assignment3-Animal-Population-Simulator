
/**
 * Write a description of class Alga here.
 *
 * @author Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27
 */
public class Alga extends Plant
{
    private static final int MAX_GROWTH_LEVEL = 10;

    /**
     * Constructor for objects of class Alga
     */
    public Alga(boolean randomGrowth, Field field, Location location)
    {
        super(field, location);
        if(randomGrowth) {
            setGrowthLevel(getRandom().nextInt(MAX_GROWTH_LEVEL));
        }
        else {
            setGrowthLevel(0);
        }
    }

    public void act(Weather weather, int step)
    {
        if(step % 3 == 0) {
            incrementGrowth(weather);
        }
    }
    
    public int getMaxGrowthLevel()
    {
        return MAX_GROWTH_LEVEL;
    }
}
