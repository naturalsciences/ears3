/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.domain.Coordinate;

/**
 *
 * @author Thomas Vandenberghe
 */
public class SpatialUtil {
    
    public static Double bearingByCoord(Coordinate coord1, Coordinate coord2) {
        if (coord1 == null || coord2 == null) {
            return 0D;
        }
        double λ1 = coord1.x;
        double λ2 = coord2.x;

        double φ1 = coord1.y;
        double φ2 = coord2.y;
        double y = Math.sin(λ2 - λ1) * Math.cos(φ2);
        double x = Math.cos(φ1) * Math.sin(φ2)
                - Math.sin(φ1) * Math.cos(φ2) * Math.cos(λ2 - λ1);
        double θ = Math.atan2(y, x);
        double bearing = (θ * 180 / Math.PI + 360) % 360;
        return bearing;
    }
}
