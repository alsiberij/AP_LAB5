package root;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PlotPanel extends JPanel {

    public class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent ev) {
            if (ev.getButton() == 3) {
                if (PlotPanel.this.undoHistory.size() > 0) {
                    PlotPanel.this.viewport = PlotPanel.this.undoHistory.get(PlotPanel.this.undoHistory.size() - 1);
                    PlotPanel.this.undoHistory.remove(PlotPanel.this.undoHistory.size() - 1);
                } else {
                    PlotPanel.this.zoomToRegion(PlotPanel.this.minX, PlotPanel.this.maxY, PlotPanel.this.maxX, PlotPanel.this.minY);
                }
                PlotPanel.this.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent ev) {
            if (ev.getButton() != 1) {
                return;
            }
            PlotPanel.this.selectedMarker = PlotPanel.this.findSelectedPoint(ev.getX(), ev.getY());
            PlotPanel.this.originalPoint = PlotPanel.this.translatePointToXY(ev.getX(), ev.getY());
            if (PlotPanel.this.selectedMarker >= 0) {
                PlotPanel.this.changeMode = true;
                PlotPanel.this.setCursor(Cursor.getPredefinedCursor(8));
            } else {
                PlotPanel.this.scaleMode = true;
                PlotPanel.this.setCursor(Cursor.getPredefinedCursor(5));
                PlotPanel.this.selectionRect.setFrame(ev.getX(), ev.getY(), 1.0, 1.0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent ev) {
            if (ev.getButton() != 1) {
                return;
            }
            PlotPanel.this.setCursor(Cursor.getPredefinedCursor(0));
            if (PlotPanel.this.changeMode) {
                PlotPanel.this.changeMode = false;
            } else {
                PlotPanel.this.scaleMode = false;
                double[] finalPoint = PlotPanel.this.translatePointToXY(ev.getX(), ev.getY());
                PlotPanel.this.undoHistory.add(PlotPanel.this.viewport);
                PlotPanel.this.viewport = new double[2][2];
                PlotPanel.this.zoomToRegion(PlotPanel.this.originalPoint[0], PlotPanel.this.originalPoint[1], finalPoint[0], finalPoint[1]);
                PlotPanel.this.repaint();
            }
        }
    }

    public class MouseMotionHandler implements MouseMotionListener {
        @Override
        public void mouseMoved(MouseEvent ev) {
            PlotPanel.this.selectedMarker = PlotPanel.this.findSelectedPoint(ev.getX(), ev.getY());
            if (PlotPanel.this.selectedMarker >= 0) {
                PlotPanel.this.setCursor(Cursor.getPredefinedCursor(8));
            } else {
                PlotPanel.this.setCursor(Cursor.getPredefinedCursor(0));
            }
            PlotPanel.this.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent ev) {
            if (PlotPanel.this.changeMode) {
                double[] currentPoint = PlotPanel.this.translatePointToXY(ev.getX(), ev.getY());
                double newY = ((Double[])PlotPanel.this.graphicsData.get(PlotPanel.this.selectedMarker))[1] + (currentPoint[1] - ((Double[])PlotPanel.this.graphicsData.get(PlotPanel.this.selectedMarker))[1]);
                if (newY > PlotPanel.this.viewport[0][1]) {
                    newY = PlotPanel.this.viewport[0][1];
                }
                if (newY < PlotPanel.this.viewport[1][1]) {
                    newY = PlotPanel.this.viewport[1][1];
                }
                ((PlotPanel.this).graphicsData.get((int)((PlotPanel)PlotPanel.this).selectedMarker))[1] = newY;
                PlotPanel.this.repaint();
            } else {
                double height;
                double width = (double)ev.getX() - PlotPanel.this.selectionRect.getX();
                if (width < 5.0) {
                    width = 5.0;
                }
                if ((height = (double)ev.getY() - PlotPanel.this.selectionRect.getY()) < 5.0) {
                    height = 5.0;
                }
                PlotPanel.this.selectionRect.setFrame(PlotPanel.this.selectionRect.getX(), PlotPanel.this.selectionRect.getY(), width, height);
                PlotPanel.this.repaint();
            }
        }
    }

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

    public void displayGraphics(ArrayList<Double[]> graphicsData) {
        this.graphicsData = graphicsData;
        this.originalData = new ArrayList(graphicsData.size());
        for (Double[] point : graphicsData) {
            Double[] newPoint = new Double[]{point[0], point[1]};
            this.originalData.add(newPoint);
        }
        this.minX = graphicsData.get(0)[0];
        this.maxX = graphicsData.get(graphicsData.size() - 1)[0];
        this.maxY = graphicsData.get(0)[1];
        this.minY = this.maxY;
        for (int i = 1; i < graphicsData.size(); ++i) {
            if (graphicsData.get(i)[1] < this.minY) {
                this.minY = graphicsData.get(i)[1];
            }
            if (!(graphicsData.get(i)[1] > this.maxY)) continue;
            this.maxY = graphicsData.get(i)[1];
        }
        this.zoomToRegion(this.minX, this.maxY, this.maxX, this.minY);
    }

    public void reset() {
        this.displayGraphics(this.originalData);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.scaleX = this.getSize().getWidth() / (this.viewport[1][0] - this.viewport[0][0]);
        this.scaleY = this.getSize().getHeight() / (this.viewport[0][1] - this.viewport[1][1]);
        if (this.graphicsData == null || this.graphicsData.size() == 0) {
            return;
        }
        Graphics2D canvas = (Graphics2D)g;
        //this.paintGrid(canvas);
        if (showAxis) {
            paintAxis(canvas);
        }

        paintGraphics(canvas);

        if (showMarkers) {
            paintMarkers(canvas);
        }
        this.paintLabels(canvas);
        this.paintSelection(canvas);
    }

    private void paintSelection(Graphics2D canvas) {
        if (!this.scaleMode) {
            return;
        }
        canvas.setStroke(this.selectionStroke);
        canvas.setColor(Color.BLACK);
        canvas.draw(this.selectionRect);
    }

    private void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10,3,6,3,6,3,10,3,3}, 0));
        canvas.setColor(Color.RED);
        Double currentX = null;
        Double currentY = null;
        for (Double[] point : this.graphicsData) {
            if (point[0] < this.viewport[0][0] || point[1] > this.viewport[0][1] || point[0] > this.viewport[1][0] || point[1] < this.viewport[1][1]) continue;
            if (currentX != null && currentY != null) {
                canvas.draw(new Line2D.Double(this.translateXYtoPoint(currentX, currentY), this.translateXYtoPoint(point[0], point[1])));
            }
            currentX = point[0];
            currentY = point[1];
        }
    }

    private void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(this.markerStroke);

        double avg = 0;
        for (int i = 0; i < graphicsData.size(); i++) {
            avg += graphicsData.get(i)[1];
        }
        avg /= graphicsData.size();

        int i = -1;
        for (Double[] point : this.graphicsData) {
            ++i;
            if (point[0] < this.viewport[0][0] || point[1] > this.viewport[0][1] || point[0] > this.viewport[1][0] || point[1] < this.viewport[1][1]) {
                continue;
            }

            double mul = i == selectedMarker ? 1.75 : 1;

            if (point[1] >= avg * 2) {
                canvas.setColor(Color.MAGENTA);
                canvas.setPaint(Color.MAGENTA);
            } else {
                canvas.setColor(Color.BLUE);
                canvas.setPaint(Color.BLUE);
            }

            canvas.setStroke(markerStroke);
            Point2D.Double center = translateXYtoPoint(point[0], point[1]);
            canvas.draw(new Line2D.Double(shiftPoint(center, -6*mul, 0), shiftPoint(center, 6*mul, 0)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 6*mul), shiftPoint(center, 0, -6*mul)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 6*mul, 6*mul), shiftPoint(center, -6*mul, -6*mul)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -6*mul, 6*mul), shiftPoint(center, 6*mul, -6*mul)));

        }
    }

    private void paintLabels(Graphics2D canvas) {
        Rectangle2D bounds;
        String label;
        Point2D.Double point;
        double pos;
        canvas.setColor(Color.BLACK);
        canvas.setFont(this.labelsFont);
        FontRenderContext context = canvas.getFontRenderContext();
        double labelYPos = this.viewport[1][1] < 0.0 && this.viewport[0][1] > 0.0 ? 0.0 : this.viewport[1][1];
        double labelXPos = this.viewport[0][0] < 0.0 && this.viewport[1][0] > 0.0 ? 0.0 : this.viewport[0][0];
        double step = (this.viewport[1][0] - this.viewport[0][0]) / 10.0;
        /*for (pos = this.viewport[0][0]; pos < this.viewport[1][0]; pos += step) {
            point = this.translateXYtoPoint(pos, labelYPos);
            label = formatter.format(pos);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 5), (float)(point.getY() - bounds.getHeight()) + 10);
        }
        step = (this.viewport[0][1] - this.viewport[1][1]) / 10.0;
        for (pos = this.viewport[1][1]; pos < this.viewport[0][1]; pos += step) {
            point = this.translateXYtoPoint(labelXPos, pos);
            label = formatter.format(pos);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 10), (float)(point.getY() - bounds.getHeight()) + 10);
        }*/
        if (this.selectedMarker >= 0) {
            point = this.translateXYtoPoint(this.graphicsData.get(this.selectedMarker)[0], this.graphicsData.get(this.selectedMarker)[1]);
            label = "X = " + formatter.format(this.graphicsData.get(this.selectedMarker)[0]) + "; Y = " + formatter.format(this.graphicsData.get(this.selectedMarker)[1]);
            bounds = this.labelsFont.getStringBounds(label, context);
            canvas.setColor(Color.BLUE);
            canvas.drawString(label, (float)(point.getX() + 5.0), (float)(point.getY() - bounds.getHeight()));
        }
    }

    private void paintAxis(Graphics2D canvas) {
        Point2D.Double labelPos;
        Rectangle2D bounds;
        canvas.setStroke(this.axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setFont(this.axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (this.viewport[0][0] <= 0.0 && this.viewport[1][0] >= 0.0) {
            canvas.draw(new Line2D.Double(this.translateXYtoPoint(0.0, this.viewport[0][1]), this.translateXYtoPoint(0.0, this.viewport[1][1])));
            canvas.draw(new Line2D.Double(this.translateXYtoPoint(-(this.viewport[1][0] - this.viewport[0][0]) * 0.0025, this.viewport[0][1] - (this.viewport[0][1] - this.viewport[1][1]) * 0.015), this.translateXYtoPoint(0.0, this.viewport[0][1])));
            canvas.draw(new Line2D.Double(this.translateXYtoPoint((this.viewport[1][0] - this.viewport[0][0]) * 0.0025, this.viewport[0][1] - (this.viewport[0][1] - this.viewport[1][1]) * 0.015), this.translateXYtoPoint(0.0, this.viewport[0][1])));
            bounds = this.axisFont.getStringBounds("Y", context);
            labelPos = this.translateXYtoPoint(0.0, this.viewport[0][1]);
            canvas.drawString("Y", (float)labelPos.x + 10.0f, (float)(labelPos.y + bounds.getHeight() / 2.0));
        }
        if (this.viewport[1][1] <= 0.0 && this.viewport[0][1] >= 0.0) {
            canvas.draw(new Line2D.Double(this.translateXYtoPoint(this.viewport[0][0], 0.0), this.translateXYtoPoint(this.viewport[1][0], 0.0)));
            canvas.draw(new Line2D.Double(this.translateXYtoPoint(this.viewport[1][0] - (this.viewport[1][0] - this.viewport[0][0]) * 0.01, (this.viewport[0][1] - this.viewport[1][1]) * 0.005), this.translateXYtoPoint(this.viewport[1][0], 0.0)));
            canvas.draw(new Line2D.Double(this.translateXYtoPoint(this.viewport[1][0] - (this.viewport[1][0] - this.viewport[0][0]) * 0.01, -(this.viewport[0][1] - this.viewport[1][1]) * 0.005), this.translateXYtoPoint(this.viewport[1][0], 0.0)));
            bounds = this.axisFont.getStringBounds("X", context);
            labelPos = this.translateXYtoPoint(this.viewport[1][0], 0.0);
            canvas.drawString("X", (float)(labelPos.x - bounds.getWidth() - 10.0), (float)(labelPos.y - bounds.getHeight() / 2.0));
        }
    }

}
