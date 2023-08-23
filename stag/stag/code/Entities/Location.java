package Entities;

import java.util.ArrayList;
import java.util.LinkedList;

public class Location extends Entity {

    public String entityType = "Entities.Location";
    public String name;
    public ArrayList<Artefact> artefacts = new ArrayList<Artefact>();
    public ArrayList<Furniture> furniture = new ArrayList<Furniture>();
    public ArrayList<Character> characters = new ArrayList<Character>();
    //ArrayList to hold the destination which can be reach from this location
    public ArrayList<Location> pathTo = new ArrayList<Location>();

    //method to return the content of the location
    public ArrayList<String> show(){
        ArrayList<String> linesToShow = new ArrayList<String>();
        String line1 = "You are in " + description + ". You can see: \n";
        linesToShow.add(line1);
        for(int i = 0; i < artefacts.size(); i++){
            String line =  artefacts.get(i).description + "\n";
            linesToShow.add(line);
        }
        for(int i = 0; i < furniture.size(); i++){
            String line = furniture.get(i).description + "\n";
            linesToShow.add(line);
        }
        for(int i = 0; i < characters.size(); i++){
            String line = characters.get(i).description + "\n";
            linesToShow.add(line);
        }
        return linesToShow;


    }

}
