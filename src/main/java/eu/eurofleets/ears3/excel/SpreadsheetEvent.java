package eu.eurofleets.ears3.excel;

import eu.eurofleets.ears3.excel.converters.StringConverter;
import io.github.rushuat.ocell.annotation.FieldConverter;
import io.github.rushuat.ocell.annotation.FieldFormula;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class SpreadsheetEvent {

    public enum FIELDS {
        Date, Hour, Actor, eventDefinitionId, Program, Tool, Process, Action, Label, Station, Description,
        Remarks, Dist, Time, Status, Region, Weather, Navigation
    };

    @FieldName("Date")
    @FieldConverter(StringConverter.class)
    @NotNull
    String date;

    @FieldName("Hour")
    @FieldConverter(StringConverter.class)
    String hour;

    @FieldName("Actor")
    @FieldConverter(StringConverter.class)
    String actor;

    @FieldName("eventDefinitionId")
    @FieldConverter(StringConverter.class)
    String eventDefinitionId;

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

    @FieldName("Time")
    @FieldConverter(StringConverter.class)
    String time;

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

}
