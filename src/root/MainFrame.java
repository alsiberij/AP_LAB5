package root;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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

    public MainFrame() {
        super("Построение графика");
        this.setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        this.setLocation((kit.getScreenSize().width - 700) / 2, (kit.getScreenSize().height - 500) / 2);
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        AbstractAction openGraphicsAction = new AbstractAction("Открыть файл") {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (MainFrame.this.fileChooser == null) {
                    MainFrame.this.fileChooser = new JFileChooser();
                    MainFrame.this.fileChooser.setCurrentDirectory(new File("."));
                }
                MainFrame.this.fileChooser.showOpenDialog(MainFrame.this);
                MainFrame.this.openFile(MainFrame.this.fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(openGraphicsAction);
        AbstractAction resetGraphicsAction = new AbstractAction("Отменить все изменения"){

            @Override
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.display.reset();
            }
        };
        resetGraphicsMenuItem = fileMenu.add(resetGraphicsAction);
        this.resetGraphicsMenuItem.setEnabled(false);
        AbstractAction saveAction = new AbstractAction("Сохранить изменения") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    saveToBin(new File(fileChooser.getSelectedFile().getName().concat(".bin")));
                }
            }
        };
        saveMenuItem = fileMenu.add(saveAction);
        saveMenuItem.setEnabled(false);

        JMenu plotMenu = new JMenu("График");
        menuBar.add(plotMenu);

        Action showAxisAction = new AbstractAction("Показать оси координат") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowAxis(showAxisMI.isSelected());
            }
        };
        showAxisMI = new JCheckBoxMenuItem(showAxisAction);
        plotMenu.add(showAxisMI);
        showAxisMI.setSelected(true);

        Action showMarkersAction = new AbstractAction("Показать маркеры точек") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowMarkers(showMarkersMI.isSelected());
            }
        };
        showMarkersMI = new JCheckBoxMenuItem(showMarkersAction);
        plotMenu.add(showMarkersMI);
        showMarkersMI.setSelected(false);
        plotMenu.addMenuListener(new PlotMenuListener());
        add(display, "Center");
    }

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
