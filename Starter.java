import javax.swing.*;
import java.awt.*;

public class Starter {
    public static void main(String[] args){
        JFrame frame = new JFrame("COVID-19");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        DrawingPanel mainPanel = new DrawingPanel();
        mainPanel.setPreferredSize(new Dimension(1200,700));
        mainPanel.setBackground(Color.white);
        frame.getContentPane().add(mainPanel);
        frame.pack();

        Timer t = new Timer(100,mainPanel);
        t.start();

        mainPanel.addMouseListener(mainPanel);

    }
}
