package Entities;

public class Artefact extends Entity {
    public String entityType = "Entities.Artefact";
    public Artefact(Artefact artefact){
        this.id = artefact.id;
        this.description = artefact.description;
        this.entityType = artefact.entityType;
    }
    public Artefact(){}
}
