package root;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MainFrame extends JFrame {

    public static void main(String[] args) {

    }

    private class PlotMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            showAxisMI.setEnabled(fileLoaded);
            showMarkersMI.setEnabled(fileLoaded);
        }

        @Override
        public void menuDeselected(MenuEvent e) {

        }

        @Override
        public void menuCanceled(MenuEvent e) {

        }
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
