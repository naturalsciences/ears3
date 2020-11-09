/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import java.time.Instant;

/**
 *
 * @author thomas
 */
public class NavigationDTO {

    public Instant timeStamp;
    public Instant instrumentTime;
    public Double lon;
    public Double lat;
    public Double heading;
    public Double sow;
    public Double depth;
    public Double cog;
    public Double sog;
}
