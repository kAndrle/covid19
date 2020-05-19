import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class County {
    private String name;
    private int FIPS;
    private ArrayList<Integer> latitudes; //y axis
    private ArrayList<Integer> longitudes; //x axis
    private int population;
    private ArrayList<Integer> infected;
    private ArrayList<Integer> deaths;
    private Color color;
    private int[] center; // average [long,lat]

    public County(int FIPS){
        this.name = "noName";
        this.FIPS = FIPS;
        latitudes = new ArrayList<Integer>();
        longitudes = new ArrayList<Integer>();
        population=0;
        infected = new ArrayList<>();
        deaths = new ArrayList<>();
        int r = (int)(Math.random()*106+150);
        int g = (int)(Math.random()*106+150);
        int b = (int)(Math.random()*106+150);
        color = new Color(r,g,b);
    }

    public void setPopulation(int population){
        this.population = population;
    }

    public void newDay(int infections, int deaths){
        infected.add(infections);
        this.deaths.add(deaths);
    }

    public boolean hasName(){
        if(name.equalsIgnoreCase("noName")){
            return false;
        }
        return true;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addCoordinates(double longitude, double latitude){
        longitude = (longitude+126)*20;
        latitude = (latitude-50)*(-20); // y axis is flipped!
        latitudes.add((int)(latitude));
        longitudes.add((int)(longitude));
        double latSum = 0;
        double longSum = 0;
        for(int i=0;i<latitudes.size();i++){
            latSum+=latitudes.get(i);
            longSum+=longitudes.get(i);
        }
        center = new int[]{(int)(longSum / longitudes.size()), (int)(latSum / latitudes.size())};
    }

    public int[][] getCoordinates(){
        int[][] coordinates = new int[2][latitudes.size()];
        for(int i=0;i<latitudes.size();i++){
            coordinates[0][i] = longitudes.get(i);
            coordinates[1][i] = latitudes.get(i);
        }
        return coordinates;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Integer> getInfected(){
        return infected;
    }
    public ArrayList<Integer> getDeaths(){
        return deaths;
    }
    public int getPopulation(){
        return population;
    }
    public int[] getCenter(){
        return center;
    }
}
