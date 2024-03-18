/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICoordinate;

/**
 *
 * @author Thomas Vandenberghe
 */
public class Coordinate implements ICoordinate {

    public static double THRESHOLD = 0.5D;

    public Double x;
    public Double y;

    public Coordinate(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public boolean isValid() {
        return x != null && y != null && x != 0 && y != 0;
    }

    /**
     * Test if there is a sudden jump of more than THRESHOLD between 2 other
     * chronologically ordered coordinates. The order is beforeCoordinate, this.
     *
     * @param beforeCoordinate
     * @return true if there is a sudden jump, false if there is none.
     */
    public boolean testSpike(Coordinate beforeCoordinate) {
        return (Math.abs(beforeCoordinate.x - x) > THRESHOLD || Math.abs(beforeCoordinate.y - y) > THRESHOLD);
        //  && (Math.abs(afterCoordinate.x - x) > THRESHOLD || Math.abs(afterCoordinate.y - y) > THRESHOLD);
    }

    @Override
    public String toString() {
        return "Coordinate{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (x == null) {
            if (other.x != null)
                return false;
        } else if (!x.equals(other.x))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        return true;
    }

    
}
