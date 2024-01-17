package eu.eurofleets.ears3.excel;

import java.time.LocalTime;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.converters.StringConverter;

//import io.github.rushuat.ocell.annotation.FieldConverter;
//import io.github.rushuat.ocell.annotation.FieldName;
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class SpreadsheetEvent {

    public enum FIELDS {
        Date, Tool,
        Dist, Process,

        Hour, Action, Label, Station, Description
    };

    // @FieldName("Date")
    @NotNull
    Date date;

    // @FieldName("Dist")
    String distance;

    // @FieldName("Tool")
    @NotBlank
    String tool;

    // @FieldName("Process")
    @NotBlank
    String process;


    LocalTime hour;
    String action;
    String label;
    String station;
    String description;
    String remarks;
}
