/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import java.time.Instant;

/**
 *
 * @author Thomas Vandenberghe
 */
public class WeatherDTO {

    public Instant timeStamp;
    public Instant instrumentTime;
    public Double windSpeedAverage;
    public Double windSpeedInstantaneous;
    public Double windDirection;
    public Double atmosphericTemperature;
    public Double humidity;
    public Double solarRadiation;
    public Double atmosphericPressure;
    public Double waterTemperature;
}
