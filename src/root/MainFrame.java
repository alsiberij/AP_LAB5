package root;

import javax.swing.*;

public class MainFrame extends JFrame {

    public static void main(String[] args) {

    }

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private JFileChooser fileChooser = null;
    private JMenuItem resetGraphicsMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem showAxisMI;
    private JMenuItem showMarkersMI;
    private PlotPanel display = new PlotPanel();
    private boolean fileLoaded = false;


}
