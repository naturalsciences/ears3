package eu.eurofleets.ears3.dto;

public class PlatformDTO {

    public String identifier;
    public String name;
    public String platformClass;
    public String vesselOperator;

    public PlatformDTO() {
    }

    public PlatformDTO(String identifier, String name, String platformClass, String vesselOperator) {
        this.identifier = identifier;
        this.name = name;
        this.platformClass = platformClass;
        this.vesselOperator = vesselOperator;
    }
}
