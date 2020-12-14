package root;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.io.*;
import java.util.ArrayList;

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

    protected void openFile(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            ArrayList<Double[]> graphicsData = new ArrayList<Double[]>(50);
            while (in.available() > 0) {
                Double x = in.readDouble();
                Double y = in.readDouble();
                graphicsData.add(new Double[]{x, y});
            }
            if (graphicsData.size() > 0) {
                this.fileLoaded = true;
                this.resetGraphicsMenuItem.setEnabled(true);
                saveMenuItem.setEnabled(true);
                this.display.displayGraphics(graphicsData);
            }
        }
        catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Файл не найден", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка чтения", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    void saveToBin(File file) {
        try {
            ArrayList<Double[]> dataTable = display.getGraphicsData();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            for (int i = 0; i < dataTable.size(); i++) {
                out.writeDouble(dataTable.get(i)[0]);
                out.writeDouble(dataTable.get(i)[1]);
            }
            out.close();
        } catch (Exception ignore) {

        }
    }


}
