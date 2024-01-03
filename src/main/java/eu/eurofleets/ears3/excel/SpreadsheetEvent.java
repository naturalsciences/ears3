package eu.eurofleets.ears3.excel;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.converters.StringConverter;

//import io.github.rushuat.ocell.annotation.FieldConverter;
//import io.github.rushuat.ocell.annotation.FieldName;

public class SpreadsheetEvent {

    public static enum FIELDS {
        Date, Tool,
        Dist, Process
    };

    // @FieldName("Date")
    @NotNull
    public Date date;

    // @FieldName("Dist")

    public String distance;

    // @FieldName("Tool")
    @NotBlank
    public String tool;

    // @FieldName("Process")
    @NotBlank
    public String process;
}
