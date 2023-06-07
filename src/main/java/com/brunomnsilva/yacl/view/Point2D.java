package com.brunomnsilva.yacl.view;

/**
 * A simple 2D point with x and y coordinates.
 *
 * @author brunomnsilva
 */
public class Point2D {

    /**
     * The x-coordinate of the point.
     */
    public double x;

    /**
     * The y-coordinate of the point.
     */
    public double y;

    /**
     * Constructs a new point with the given x and y coordinates.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the point.
     *
     * @return the x-coordinate of the point.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the point.
     *
     * @param x the new x-coordinate of the point.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the point.
     *
     * @return the y-coordinate of the point.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the point.
     *
     * @param y the new y-coordinate of the point.
     */
    public void setY(double y) {
        this.y = y;
    }
}

