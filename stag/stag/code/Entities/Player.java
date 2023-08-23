package Entities;

import java.util.ArrayList;
import java.util.LinkedList;

public class Player extends Entity {
    public String entityType = "Entities.Player";
    public String name = "";
    //the location where the player is present
    public Location location;
    public ArrayList<Artefact> artefacts = new ArrayList<Artefact>();
    public int health = 3;
    public Player (String name, Location location){
        this.name = name;
        this.location = location;
    }
}
