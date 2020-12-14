package root;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PlotPanel extends JPanel {

    private ArrayList<Double[]> graphicsData;
    private ArrayList<Double[]> originalData;
    private int selectedMarker = -1;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double[][] viewport = new double[2][2];
    private ArrayList<double[][]> undoHistory = new ArrayList(5);
    private double scaleX;
    private double scaleY;
    private BasicStroke axisStroke;
    private BasicStroke gridStroke;
    private BasicStroke markerStroke;
    private BasicStroke selectionStroke;
    private Font axisFont;
    private Font labelsFont;
    private static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private boolean scaleMode = false;
    private boolean changeMode = false;
    private double[] originalPoint = new double[2];
    private Rectangle2D.Double selectionRect = new Rectangle2D.Double();
    private boolean showAxis = true;
    private boolean showMarkers = false;

    public PlotPanel() {
        this.setBackground(Color.WHITE);
        this.axisStroke = new BasicStroke(2.0f, 0, 0, 10.0f, null, 0.0f);
        this.gridStroke = new BasicStroke(1.0f, 0, 0, 10.0f, new float[]{2.0f, 5.0f}, 0.0f);
        this.markerStroke = new BasicStroke(1.5f, 0, 0, 10.0f, null, 0.0f);
        this.selectionStroke = new BasicStroke(3.0f, 0, 0, 10.0f, new float[]{10.0f, 5.0f}, 0.0f);
        this.axisFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        this.labelsFont = new Font(Font.SERIF, Font.ITALIC, 11);
        formatter.setMaximumFractionDigits(3);
        //this.addMouseListener(new MouseHandler());
        //this.addMouseMotionListener(new MouseMotionHandler());
    }


}
