package com.brunomnsilva.yacl.view;

import com.brunomnsilva.yacl.core.Clusterable;
import com.brunomnsilva.yacl.hierarchical.Dendogram;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * A visualization class for {@link Dendogram} instances.
 *
 * @param <T> the type of objects in the dendrogram, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class DendogramVisualization<T extends Clusterable<T>> extends JPanel {

    /**
     * Defines the text that is displayed for each dendogram leaf
     */
    public enum LabelType {
        NONE, // No info is displayed
        CLUSTER_ID, // The assigned cluster id during the clustering process
        LABEL // The specified label of the respective Clusterable item
    }

    public static Font FONT_TEXT_SMALL = new Font("SansSerif", Font.PLAIN, 10);
    public static Font FONT_TEXT_REGULAR = new Font("SansSerif", Font.PLAIN, 12);
    public static Color COLOR_BACKGROUND = Color.WHITE;
    public static Color COLOR_FOREGROUND = Color.BLACK;

    private static final int MARGIN_LEFT = 60;
    private static final int MARGIN_TOP = 20;
    private static final int MARGIN_RIGHT = 0;
    private static int MARGIN_BOTTOM = 20; // may be dynamically changed due to cluster labeling
    private static final int MAX_MARGIN_BOTTOM = 150;
    private static final int AXIS_NUMBER_LEVELS = 6;
    private static final NumberFormat numberFormat = new DecimalFormat("###,###.###");

    private final Dendogram<T> dendogram;
    private final List<Dendogram.DendogramNode<T>> leafList; // Pre-computed leaf list
    private final LabelType labelType;

    /**
     * Creates a new instance of the visualization.
     * @param dendogram the dendogram to visualize
     */
    public DendogramVisualization(Dendogram<T> dendogram) {
        this(dendogram, LabelType.NONE);
    }

    /**
     * Creates a new instance of the visualization.
     * <br/>
     * <code>labelType</code> can be the following:
     * <ul>
     *     <li>NONE - No info is displayed</li>
     *     <li>CLUSTER_ID - The assigned cluster id during the clustering process</li>
     *     <li>LABEL - The specified label of the respective Clusterable item</li>
     * </ul>
     * @param dendogram the dendogram to visualize
     * @param labelType the type of label to display at each dendogram leaf
     */
    public DendogramVisualization(Dendogram<T> dendogram, LabelType labelType) {
        super(true);

        this.labelType = labelType;
        this.dendogram = dendogram;
        this.leafList = dendogram.getLeafNodesOf(dendogram.getRoot());

        // Increase margin, since we'll not be displaying cluster ids
        if(labelType == LabelType.LABEL) {
            MARGIN_BOTTOM = largestLeafLabelScreenLength(this.leafList) + 10; // plus some margin
        }
    }

    private int largestLeafLabelScreenLength(List<Dendogram.DendogramNode<T>> leafList) {
        FontMetrics fontMetrics = getFontMetrics(FONT_TEXT_SMALL);

        int maxLen = 0;
        for (Dendogram.DendogramNode<T> leaf : leafList) {
            int len = fontMetrics.stringWidth( leaf.getClusterable().clusterableLabel() );
            if(len > maxLen) {
                maxLen = len;
            }
        }

        return Math.min(maxLen, MAX_MARGIN_BOTTOM);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D)graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(COLOR_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(COLOR_FOREGROUND);
        g2.setFont(FONT_TEXT_SMALL);

        // TODO: I'm still not happy with this, since with non monospace fonts
        // it cannot be effectively calculated.
        FontMetrics fontMetrics = g2.getFontMetrics();
        int charWidth = fontMetrics.stringWidth("_");
        int labelMaxLength = (int)(MARGIN_BOTTOM/(float)charWidth);

        // Cache nodes locations
        Map<Integer, Point2D> nodesLocations = new HashMap<>();

        int areaWidth = getWidth();
        int areaHeight = getHeight() - MARGIN_BOTTOM - MARGIN_TOP;

        double maxClusterLevel = dendogram.getRoot().getClusterLevel();

        int numNodes = leafList.size();
        double xStep = (double)(areaWidth - MARGIN_LEFT - MARGIN_RIGHT) / (numNodes + 1);
        double x = xStep + MARGIN_LEFT;

        // Cache the leaf nodes positions and draw the labels
        for (Dendogram.DendogramNode<T> node : leafList) {
            nodesLocations.put(node.getClusterId(), new Point2D(x, getHeight() - MARGIN_BOTTOM));

            // Draw any label for the leaf?
            if(this.labelType == LabelType.LABEL) {
                String label = String.format("%s", node.getClusterable().clusterableLabel());
                // Draw 90 deg rotated labels
                // Also, abbreviate if necessary
                label = abbreviate(label, labelMaxLength);
                drawRotatedText(g2, label, (int)x, areaHeight + MARGIN_BOTTOM / 2 + MARGIN_TOP, -90);
            } else if(this.labelType == LabelType.CLUSTER_ID){
                String label = String.format("%d", node.getClusterId());
                float labelWidth = fontMetrics.stringWidth(label);
                g2.drawString(label, (int)(x - labelWidth/2), areaHeight + MARGIN_BOTTOM + MARGIN_TOP - 5);
            }

            x += xStep;
        }

        // Draw the dendogram upwards to the root
        List<Dendogram.DendogramNode<T>> unvisited = new ArrayList<>(leafList);

        while(unvisited.size() > 1) {
            // There will always be two consecutive nodes with the same parent in this list
            Dendogram.DendogramNode<T> current = null, next = null;
            int foundIndex = -1;
            for(int i=0; i < unvisited.size() - 1; ++i) {
                current = unvisited.get(i);
                next = unvisited.get(i + 1);
                if(current.getParent() == next.getParent()) {
                    foundIndex = i;
                    break;
                }
            }

            Dendogram.DendogramNode<T> parent = current.getParent();

            Point2D cluster1_pos = nodesLocations.get(current.getClusterId());
            Point2D cluster2_pos = nodesLocations.get(next.getClusterId());

            double clusterLevel = parent.getClusterLevel();

            double midPoint = (cluster1_pos.x + cluster2_pos.x) / 2;
            double relativeHeight = (clusterLevel * areaHeight / maxClusterLevel);
            double y = (areaHeight - relativeHeight) + MARGIN_TOP;

            nodesLocations.put( parent.getClusterId(), new Point2D(midPoint, y) );

            /*String label = String.format("%d (%.3f)", parent.getClusterId(), clusterLevel);
            float labelWidth = fontMetrics.stringWidth(label);
            g2.drawString(label, (float)(midPoint - labelWidth/2), (float)y+10);*/

            unvisited.remove(current); //removed from foundIndex
            unvisited.remove(next); //removed from foundIndex + 1
            unvisited.add(foundIndex, parent); //insert at foundIndex (mandatory, for this algorithm to work)

            g2.drawLine((int)cluster1_pos.x, (int)cluster1_pos.y,
                    (int)cluster1_pos.x, (int)y);

            g2.drawLine((int)cluster2_pos.x, (int)cluster2_pos.y,
                    (int)cluster2_pos.x, (int)y);

            g2.drawLine((int)cluster1_pos.x, (int)y,
                    (int)cluster2_pos.x, (int)y);
        }

        // Draw the cluster level axis
        drawAxis(g2, maxClusterLevel);
    }

    private void drawAxis(Graphics2D g2, double maxClusterLevel) {
        g2.setFont(FONT_TEXT_REGULAR);
        g2.setColor(COLOR_FOREGROUND);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int xTickOffset = -3;
        FontMetrics fontMetrics = g2.getFontMetrics();
        int yTickOffset = fontMetrics.getHeight() / 2;

        int areaHeight = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;
        int dy = (areaHeight) / (AXIS_NUMBER_LEVELS - 1);
        double dValue = (maxClusterLevel) / (AXIS_NUMBER_LEVELS - 1);

        int y = MARGIN_TOP;
        double val = maxClusterLevel;

        int x = MARGIN_LEFT;

        String strVal;
        int strValLength;

        // Top of scale
        strVal = numberFormat.format(val);
        strValLength = fontMetrics.stringWidth(strVal) + 2 * Math.abs(xTickOffset);
        g2.drawLine(x, y, x + xTickOffset, y);
        g2.drawString(strVal, x - strValLength, y + yTickOffset);

        // "middle" of scale
        y += dy;
        val -= dValue;

        for(int i = 1; i < AXIS_NUMBER_LEVELS - 1; i++) {
            strVal = numberFormat.format(val);
            strValLength = fontMetrics.stringWidth(strVal) + 2 * Math.abs(xTickOffset);
            g2.drawLine(x, y, x + xTickOffset, y);
            g2.drawString(strVal, x - strValLength, y + yTickOffset);

            y += dy;
            val -= dValue;
        }

        // Bottom of scale
        strVal = numberFormat.format(0);
        strValLength = fontMetrics.stringWidth(strVal) + 2 * Math.abs(xTickOffset);
        g2.drawLine(x, getHeight() - MARGIN_BOTTOM, x + xTickOffset, getHeight() - MARGIN_BOTTOM);
        g2.drawString(strVal, x - strValLength, y + yTickOffset);

        // Draw vertical line
        g2.drawLine(x, MARGIN_TOP, x, getHeight() - MARGIN_BOTTOM);

        // Draw Axis Label
        //g2.setFont(FONT_TEXT_REGULAR);
        //drawRotatedText(g2, "Cluster Level", MARGIN_LEFT / 4, areaHeight / 2 + MARGIN_TOP, -90);
    }

    private void drawRotatedText(Graphics2D g2d, String text, int xPos, int yPos, int rotationDegrees) {
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(text);
        int stringHeight = fontMetrics.getHeight();

        double rotationAngle = Math.toRadians(rotationDegrees);
        g2d.rotate(rotationAngle, xPos, yPos);

        int adjustedXPos = xPos - stringWidth / 2;
        int adjustedYPos = yPos + stringHeight / 2;

        g2d.drawString(text, adjustedXPos, adjustedYPos);

        // 'reset' rotation on the graphics context
        g2d.rotate(-rotationAngle, xPos, yPos);
    }

    private static String abbreviate(String input, int length) {
        return ( input.length () > length ) ? input.substring ( 0 , length - 1 ).concat ( "…" ) : input;
    }

}
