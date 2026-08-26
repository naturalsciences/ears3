package eu.eurofleets.ears3.excel;

import eu.eurofleets.ears3.excel.converters.ExcelTimeSerialConverter;
import eu.eurofleets.ears3.excel.converters.StringConverter;
import io.github.rushuat.ocell.annotation.FieldConverter;
import io.github.rushuat.ocell.annotation.FieldFormula;
import io.github.rushuat.ocell.annotation.FieldName;
/* import lombok.*;
import lombok.experimental.FieldDefaults; */

//import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

//@Entity
/* @Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString */
public class SpreadsheetEvent {

    public enum FIELDS {
        Date, Time, Actor, Program, Tool, Process, Action, Label, Station, Description,
        Remarks, Dist, Elapsed_Time, Status, Region, Weather, Navigation
    };

    //
    @FieldName("Date")
    @FieldConverter(StringConverter.class)
    //@NotNull
    @NotBlank
    String date;

    @FieldName("Time")
    @FieldConverter(ExcelTimeSerialConverter.class)
    @NotBlank
    String time;

    @FieldName("Actor")
    @FieldConverter(StringConverter.class)
    String actor;

    @FieldName("Program")
    @FieldConverter(StringConverter.class)
    @NotBlank
    String program;

    @FieldName("Tool")
    @FieldConverter(StringConverter.class)
    @NotBlank
    String tool;

    @FieldName("Process")
    @FieldFormula
    @FieldConverter(StringConverter.class)
    @NotBlank
    String process;

    @FieldName("Action")
    @FieldConverter(StringConverter.class)
    @NotBlank
    String action;

    @FieldName("Label")
    @FieldConverter(StringConverter.class)
    String label;

    @FieldName("Station")
    @FieldConverter(StringConverter.class)
    String station;

    @FieldName("Description")
    @FieldConverter(StringConverter.class)
    String description;

    @FieldName("Remarks")
    @FieldConverter(StringConverter.class)
    String remarks;

    @FieldName("Dist")
    @FieldConverter(StringConverter.class)
    String distance;

    @FieldName("Elapsed Time")
    @FieldConverter(StringConverter.class)
    String elapsedTime;

    @FieldName("Status")
    @FieldConverter(StringConverter.class)
    String status;

    @FieldName("Region")
    @FieldConverter(StringConverter.class)
    String region;

    @FieldName("Weather")
    @FieldConverter(StringConverter.class)
    String weather;

    @FieldName("Navigation")
    @FieldConverter(StringConverter.class)
    String navigation;

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the hour
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the hour to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the actor
     */
    public String getActor() {
        return actor;
    }

    /**
     * @param actor the actor to set
     */
    public void setActor(String actor) {
        this.actor = actor;
    }

    /**
     * @return the program
     */
    public String getProgram() {
        return program;
    }

    /**
     * @param program the program to set
     */
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * @return the tool
     */
    public String getTool() {
        return tool;
    }

    /**
     * @param tool the tool to set
     */
    public void setTool(String tool) {
        this.tool = tool;
    }

    /**
     * @return the process
     */
    public String getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(String process) {
        this.process = process;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the station
     */
    public String getStation() {
        return station;
    }

    /**
     * @param station the station to set
     */
    public void setStation(String station) {
        this.station = station;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * @return the time
     */
    public String getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @param elapsedTime the time to set
     */
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the weather
     */
    public String getWeather() {
        return weather;
    }

    /**
     * @param weather the weather to set
     */
    public void setWeather(String weather) {
        this.weather = weather;
    }

    /**
     * @return the navigation
     */
    public String getNavigation() {
        return navigation;
    }

    /**
     * @param navigation the navigation to set
     */
    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((actor == null) ? 0 : actor.hashCode());
        result = prime * result + ((program == null) ? 0 : program.hashCode());
        result = prime * result + ((tool == null) ? 0 : tool.hashCode());
        result = prime * result + ((process == null) ? 0 : process.hashCode());
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((station == null) ? 0 : station.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
        result = prime * result + ((distance == null) ? 0 : distance.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((weather == null) ? 0 : weather.hashCode());
        result = prime * result + ((navigation == null) ? 0 : navigation.hashCode());
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
        SpreadsheetEvent other = (SpreadsheetEvent) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (actor == null) {
            if (other.actor != null)
                return false;
        } else if (!actor.equals(other.actor))
            return false;
        if (program == null) {
            if (other.program != null)
                return false;
        } else if (!program.equals(other.program))
            return false;
        if (tool == null) {
            if (other.tool != null)
                return false;
        } else if (!tool.equals(other.tool))
            return false;
        if (process == null) {
            if (other.process != null)
                return false;
        } else if (!process.equals(other.process))
            return false;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (station == null) {
            if (other.station != null)
                return false;
        } else if (!station.equals(other.station))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (remarks == null) {
            if (other.remarks != null)
                return false;
        } else if (!remarks.equals(other.remarks))
            return false;
        if (distance == null) {
            if (other.distance != null)
                return false;
        } else if (!distance.equals(other.distance))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (weather == null) {
            if (other.weather != null)
                return false;
        } else if (!weather.equals(other.weather))
            return false;
        if (navigation == null) {
            if (other.navigation != null)
                return false;
        } else if (!navigation.equals(other.navigation))
            return false;
        return true;
    }

}
