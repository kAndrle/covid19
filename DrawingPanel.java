import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DrawingPanel extends JPanel implements ActionListener, MouseListener {
    private Map<Integer,County> counties; // <FIPS ID, county object>
    private JSlider dateSlider; //dates: Jan 21-April 6
    // Jan has 31 days; Feb 29; Mar 31; Apr 30; 77 days total
    private JLabel dateLabel; //displays the date in "March 23 2020" format
    //private JLabel countyLabel;
    //private ButtonGroup statistic;
    private JRadioButton deaths;
    private JRadioButton infections;
    //private ButtonGroup mathType;
    private JRadioButton byPercent;
    private JRadioButton absolute;
    private String statistic;
    private String mathType;

    public DrawingPanel(){
        this.setLayout(null);
        this.setBackground(Color.white);

        dateLabel = new JLabel("January 21 2020");
        dateLabel.setBounds(640,540,200,50);
        this.add(dateLabel);

        dateSlider = new JSlider(0,76,0);
        dateSlider.setBounds(20,540,240,60);
        this.add(dateSlider);

        statistic = "Infections";
        deaths = new JRadioButton("Deaths");
        infections = new JRadioButton("Infections");
        infections.setSelected(true);
        deaths.setSelected(false);
        deaths.addActionListener(event -> {
            infections.setSelected(false);
            statistic = "Deaths";
        });
        infections.addActionListener(event->{
            deaths.setSelected(false);
            statistic = "Infections";
        });
        infections.setBounds(300,540,150,30);
        deaths.setBounds(300,570,150,30);
        this.add(infections);
        this.add(deaths);

        mathType = "Percent";
        byPercent = new JRadioButton("By Percent");
        absolute = new JRadioButton("Absolute");
        byPercent.setSelected(true);
        absolute.setSelected(false);
        byPercent.addActionListener(event->{
            mathType = "Percent";
            absolute.setSelected(false);
        });
        absolute.addActionListener(event->{
            mathType = "Absolute";
            byPercent.setSelected(false);
        });
        byPercent.setBounds(470,540,150,30);
        absolute.setBounds(470,570,150,30);
        this.add(byPercent);
        this.add(absolute);



        //Read files:
        counties = new HashMap<>();
        Path coordPath = Paths.get("countyCoords.txt");
        File coordinateFile = coordPath.toFile();
        Path covidPath = Paths.get("us-counties.csv");
        File covidData = covidPath.toFile();
        //File coordinateFile = new File("countyCoords.txt");
        //File covidData = new File("us-counties.csv");
        FileReader fr = null;
        BufferedReader br = null;
        try{ //This can throw file not found exception
            fr = new FileReader(coordinateFile);
            br = new BufferedReader(fr);
            //Null when we're done
            String lineOData = br.readLine();
            while(lineOData!=null){
                int FIPS = Integer.parseInt(lineOData);
                counties.put(FIPS,new County(FIPS));
                String population = br.readLine();
                counties.get(FIPS).setPopulation(Integer.parseInt(population));
                String coordinates = br.readLine();
                String coordSets[] = coordinates.split(" ");
                for(String coordSet: coordSets){
                    String[] coords = coordSet.split(",");
                    counties.get(FIPS).addCoordinates(Double.parseDouble(coords[0]),Double.parseDouble(coords[1]));
                }
                lineOData = br.readLine();
            }
        } catch (IOException ex){
            //Print to the error stream
            // IOException ex will contain attempted file name
            System.err.println("ERROR accessing :"+ex.getMessage());
        }
        try{ //This can throw file not found exception
            fr = new FileReader(covidData);
            br = new BufferedReader(fr);
            //Null when we're done
            String firstLine = br.readLine(); // read and ignore
            String lineOData = br.readLine();
            while(lineOData!=null){
                String data[] = lineOData.split(",");
                if(!data[3].equalsIgnoreCase("") && counties.containsKey(Integer.parseInt(data[3]))) {
                    Integer FIPS = Integer.parseInt(data[3]);
                    if (!counties.get(FIPS).hasName()) {
                        counties.get(FIPS).setName(data[1] + ", " + data[2]);
                    }
                    counties.get(FIPS).newDay(Integer.parseInt(data[4]), Integer.parseInt(data[5]));
                }
                lineOData = br.readLine();
            }
        } catch (IOException ex){
            //Print to the error stream
            // IOException ex will contain attempted file name
            System.err.println("ERROR accessing :"+ex.getMessage());
        }

    }


    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        //g.drawRect(20,20, 1000,500); //US lat is (25,50) and long is (-125,-75)
        //Rectangle goes from (20,1020) and (520,20). 1 degree of lat/long is equal to 20 pixels. So multiply values by 20.
        for(County c: counties.values()){
            int[][] coords = c.getCoordinates();
            g.setColor(c.getColor());
            g.fillPolygon(coords[0],coords[1],coords[0].length);
            g.setColor(Color.black);
            g.drawPolygon(coords[0],coords[1],coords[0].length);


        }
        for(County c: counties.values()){
            //now make circles:
            if(c.getInfected().size()>dateSlider.getValue() && c.getDeaths().size()>dateSlider.getValue()) {
                int radius;
                if (statistic.equalsIgnoreCase("Infections")) {
                    g.setColor(Color.green);
                    if (mathType.equalsIgnoreCase("Percent")) {
                        radius = (int) (10000.0 * c.getInfected().get(dateSlider.getValue()) / c.getPopulation());
                    } else {
                        radius = c.getInfected().get(dateSlider.getValue());
                    }
                } else {
                    g.setColor(Color.red);
                    if (mathType.equalsIgnoreCase("Percent")) {
                        radius = (int) (10000.0 * c.getDeaths().get(dateSlider.getValue()) / c.getPopulation());
                    } else {
                        radius = c.getDeaths().get(dateSlider.getValue());
                    }
                }
                int[] center = c.getCenter();
                g.drawOval(center[0] - radius, center[1] - radius, 2*radius, 2*radius);
            }
        }
        int date = dateSlider.getValue();
        if(date<11){
            dateLabel.setText("January "+(date+21)+" 2020");
        }else if(date < 40){
            dateLabel.setText("February "+(date-10)+" 2020");
        }else if(date < 71){
            dateLabel.setText("March "+(date-39)+" 2020");
        }else{
            dateLabel.setText("April "+(date-70)+" 2020");
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
