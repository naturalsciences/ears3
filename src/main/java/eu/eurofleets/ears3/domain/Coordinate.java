/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICoordinate;

/**
 *
 * @author thomas
 */
public class Coordinate implements ICoordinate {

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

}
