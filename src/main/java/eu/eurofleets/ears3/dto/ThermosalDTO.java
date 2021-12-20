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
public class ThermosalDTO {

    public Instant timeStamp;
    public Instant instrumentTime;
    public Double temperature;
    public Double salinity;
    public Double sigmat;
    public Double conductivity;
    public Double rawFluorometry;
}
