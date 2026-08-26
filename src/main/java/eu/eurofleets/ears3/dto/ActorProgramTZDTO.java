package eu.eurofleets.ears3.dto;

public class ActorProgramTZDTO {
    private PersonDTO actor;
    private String program;
    private String timezone;

    public ActorProgramTZDTO() {
    }

    public ActorProgramTZDTO(PersonDTO actor, String program, String timezone) {
        this.actor = actor;
        this.program = program;
        this.timezone = timezone;
    }

    public PersonDTO getActor() {
        return actor;
    }

    public void setActor(PersonDTO actor) {
        this.actor = actor;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
