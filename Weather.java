import java.util.Random;

/**
 * Controls weather state for the Simulator Class.
 * 
 * Weather can be Sunny, Cloudy or Rainy.
 * Weather affects how much plants grow.
 *
 * @author Dvin Amasi Hartoonian (K21049232), William Atta (K21097986)
 * and Michael Seiranian (K20127931)
 * @version 2022.02.27
 */
public class Weather
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private boolean sunny;
    private boolean cloudy;
    private boolean rainy;

    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        sunny = true;
        cloudy = false;
        rainy = false;
    }

    
    public boolean isSunny()
    {
        return sunny;
    }
    
    public boolean isCloudy()
    {
        return cloudy;
    }
    
    public boolean isRainy()
    {
        return rainy;
    }
    
    
    private void setSunny()
    {
        sunny = true;
        cloudy = false;
        rainy = false;
    }
    
    private void setCloudy()
    {
        sunny = false;
        cloudy = true;
        rainy = false;
    }
    
    private void setRainy()
    {
        sunny = false;
        cloudy = false;
        rainy = true;
    }
    
    public void changeWeather()
    {
        int newWeather = rand.nextInt(3);
        
        if (newWeather == 0){
            setSunny();
        }
        else if (newWeather == 1){
            setCloudy();
        }
        else {
            setRainy();
        }
    }
}

