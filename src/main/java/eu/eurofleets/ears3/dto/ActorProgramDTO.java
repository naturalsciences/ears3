package eu.eurofleets.ears3.dto;

public class ActorProgramDTO {
    private PersonDTO actor;
    private String program;

    public ActorProgramDTO() {
    }

    public ActorProgramDTO(PersonDTO actor, String program) {
        this.actor = actor;
        this.program = program;
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
}
