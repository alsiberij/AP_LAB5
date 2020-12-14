package root;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
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

    protected Point2D.Double translateXYtoPoint(double x, double y) {
        double deltaX = x - this.viewport[0][0];
        double deltaY = this.viewport[0][1] - y;
        return new Point2D.Double(deltaX * this.scaleX, deltaY * this.scaleY);
    }

    protected double[] translatePointToXY(int x, int y) {
        return new double[]{this.viewport[0][0] + (double)x / this.scaleX, this.viewport[0][1] - (double)y / this.scaleY};
    }

    protected int findSelectedPoint(int x, int y) {
        if (this.graphicsData == null) {
            return -1;
        }
        int pos = 0;
        for (Double[] point : this.graphicsData) {
            Point2D.Double screenPoint = this.translateXYtoPoint(point[0], point[1]);
            double distance = (screenPoint.getX() - (double)x) * (screenPoint.getX() - (double)x) + (screenPoint.getY() - (double)y) * (screenPoint.getY() - (double)y);
            if (distance < 100.0) {
                return pos;
            }
            ++pos;
        }
        return -1;
    }

    protected Point2D.Double shiftPoint(Point2D.Double source, double deltaX, double deltaY) {
        Point2D.Double result = new Point2D.Double();
        result.setLocation(source.getX() + deltaX, source.getY() + deltaY);
        return result;
    }

    public ArrayList<Double[]> getGraphicsData() {
        return graphicsData;
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void zoomToRegion(double x1, double y1, double x2, double y2) {
        this.viewport[0][0] = x1;
        this.viewport[0][1] = y1;
        this.viewport[1][0] = x2;
        this.viewport[1][1] = y2;
        this.repaint();
    }


}
