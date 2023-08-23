import Entities.*;
import Action.*;
import Entities.Character;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Controller {
    public Model model;
    public Controller(Model m){
        this.model = m;
    }

    //parse the dot file and update the location and path data into memory
    public void parseEntity(String entityFilename){
        try{
            Parser dotParser = new Parser();
            FileReader reader = new FileReader(entityFilename);
            dotParser.parse(reader);
            ArrayList<Graph> graphs = dotParser.getGraphs();
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            for(Graph g : subGraphs){
                ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                for (Graph g1 : subGraphs1){
                    ArrayList<Node> nodesLoc = g1.getNodes(false);
                    Node nLoc = nodesLoc.get(0);
                    Location location = new Location();
                    location.id = g1.getId().getId();
                    location.name = nLoc.getId().getId();
                    location.description = nLoc.getAttribute("description");
                    ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                    for (Graph g2 : subGraphs2) {
                        String entityType = g2.getId().getId();
                        String entityId = "";
                        String entityDescription = "";
                        ArrayList<Node> nodesEnt = g2.getNodes(false);
                        if(entityType.equalsIgnoreCase("artefacts")){
                            for (Node nEnt : nodesEnt) {
                                entityId = nEnt.getId().getId();
                                entityDescription = nEnt.getAttribute("description");
                                Artefact artefact = new Artefact();
                                artefact.id = entityId;
                                artefact.description = entityDescription;
                                location.artefacts.add(artefact);
                            }

                        }else if(entityType.equalsIgnoreCase("furniture")){
                            for (Node nEnt : nodesEnt) {
                                entityId = nEnt.getId().getId();
                                entityDescription = nEnt.getAttribute("description");
                                Furniture furniture = new Furniture();
                                furniture.id = entityId;
                                furniture.description = entityDescription;
                                location.furniture.add(furniture);
                            }

                        }else if(entityType.equalsIgnoreCase("characters")){
                            for (Node nEnt : nodesEnt) {
                                entityId = nEnt.getId().getId();
                                entityDescription = nEnt.getAttribute("description");
                                Character character = new Character();
                                character.id = entityId;
                                character.description = entityDescription;
                                location.characters.add(character);
                            }

                        }
                    }
                    model.addLocation(location);
                }

                //parse the path part connect the Target in the file into the pathTo in the source location
                ArrayList<Edge> edges = g.getEdges();
                for (Edge e : edges){
                    for (Location locationStart : model.locations){
                        if(e.getSource().getNode().getId().getId().equals(locationStart.name)){
                            for(Location locationEnt : model.locations){
                                if(e.getTarget().getNode().getId().getId().equals(locationEnt.name)){
                                    locationStart.pathTo.add(locationEnt);
                                }
                            }
                        }
                    }
                }
            }


        }catch(IOException ioe) {
            System.err.println(ioe);
        }catch (com.alexmerz.graphviz.ParseException pe) {
            System.out.println(pe);
        }

    }

    //parse the action.json file into the memory
    public void parseActions(String actionFilename){
        JSONParser parser = new JSONParser();
        try{
            Reader reader = new FileReader(actionFilename);
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray JSONactions = (JSONArray) jsonObject.get("actions");
            for(int i = 0; i < JSONactions.size(); i++){
                Action action = new Action();
                JSONObject JSONaction = (JSONObject) JSONactions.get(i);
                JSONArray JSONtriggers = (JSONArray) JSONaction.get("triggers");
                for(int j = 0; j < JSONtriggers.size(); j++){
                    action.triggers.add(JSONtriggers.get(j).toString());
                }
                JSONArray JSONsubjects = (JSONArray) JSONaction.get("subjects");
                for(int j = 0; j < JSONsubjects.size(); j++){
                    action.subjects.add(JSONsubjects.get(j).toString());
                }
                JSONArray JSONconsumed = (JSONArray) JSONaction.get("consumed");
                for(int j = 0; j < JSONconsumed.size(); j++){
                    action.consumed.add(JSONconsumed.get(j).toString());
                }
                JSONArray JSONproduced = (JSONArray) JSONaction.get("produced");
                for(int j = 0; j < JSONproduced.size(); j++){
                    action.produced.add(JSONproduced.get(j).toString());
                }
                String narration = (String) JSONaction.get("narration");
                action.narration = narration;
                model.addAction(action);
            }



        }catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    //find the key words in the input command from players and perform the functions
    public void handleCommand(String playerName){

        for(int i = 0; i < model.inputCommands.size(); i++){
            if(model.inputCommands.get(i).equalsIgnoreCase("look")){
                look(playerName);

            }else if(model.inputCommands.get(i).equalsIgnoreCase("get")){
                get(playerName);
            }else if(       model.inputCommands.get(i).equalsIgnoreCase("inventory")
                        ||  model.inputCommands.get(i).equalsIgnoreCase("inv")){
                inventory(playerName);
            }else if(model.inputCommands.get(i).equalsIgnoreCase("goto")){
                goTo(playerName);
            }else if(model.inputCommands.get(i).equalsIgnoreCase("drop")){
                drop(playerName);
            }else if(model.inputCommands.get(i).equalsIgnoreCase("health")){
                health(playerName);
            }else{
                for(int j = 0; j < model.actions.size(); j++){
                    for(int k = 0; k < model.actions.get(j).triggers.size(); k++){
                        if(model.inputCommands.get(i).equalsIgnoreCase(model.actions.get(j).triggers.get(k))){
                            Boolean containSubject = false;
                            for(String sub : model.actions.get(j).subjects){
                                if(model.inputCommands.contains(sub)){
                                    containSubject = true;
                                };
                            }
                            if(containSubject){
                                trigger(playerName,model.actions.get(j));
                            }

                        }
                    }
                }
            }
        }

    }

    //trigger the action in the actions.json
    private void trigger(String playerName, Action action){
        Player player = model.getPlayer(playerName);
        Boolean isPresent = false;

        for(String subject : action.subjects){
            isPresent = false;

            for(Artefact playerArtefact : player.artefacts){
                if(subject.equals(playerArtefact.id)){
                    isPresent = true;
                }
            }
            for(Artefact locationArtefact : player.location.artefacts){
                if(subject.equals(locationArtefact.id)){
                    isPresent = true;
                }
            }
            for(Furniture locationFurniture : player.location.furniture){
                if(subject.equals(locationFurniture.id)){
                    isPresent = true;
                }
            }
            for(Character locationCharacter : player.location.characters){
                if(subject.equals(locationCharacter.id)){
                    isPresent = true;
                }
            }
            if(isPresent == false){
                break;
            }
        }
        if(isPresent == true){
            for(String consumedName : action.consumed){
                consume(consumedName,player);

            }
            //produce entities
            for(String producedName : action.produced){
                for(Location location : model.locations){
                    if(location.name.equals("unplaced")){
                        for(Artefact artefact : location.artefacts){
                            if(artefact.id.equals(producedName)){
                                player.location.artefacts.add(artefact);
                            }
                        }
                        for(Furniture furniture : location.furniture){
                            if(furniture.id.equals(producedName)){
                                player.location.furniture.add(furniture);
                            }
                        }
                        for(Character character : location.characters){
                            if(character.id.equals(producedName)){
                                player.location.characters.add(character);
                            }
                        }


                    }

                    if(location.name.equals(producedName)){
                        player.location.pathTo.add(location);
                    }


                }
                //if the produce thing is health than add the health of the player
                if(producedName.equalsIgnoreCase("health")){
                    player.health = player.health + 1;
                }
            }
            model.stringToShow.add(action.narration + "\n");
        }
        if(player.health <= 0){

            dead(playerName);
        }
    }

    //perform the object consuming part of the action
    private void consume(String consumedName, Player player){

        for(int i = 0; i < player.artefacts.size(); i++){
            if(consumedName.equals(player.artefacts.get(i).id)){
                player.artefacts.remove(i);
                return;
            }
        }

        for(int i = 0; i < player.location.artefacts.size(); i++){
            if(consumedName.equals(player.location.artefacts.get(i).id)){
                player.location.artefacts.remove(i);
                return;
            }
        }

        for(int i = 0; i < player.location.furniture.size(); i++){
            if(consumedName.equals(player.location.furniture.get(i).id)){
                player.location.furniture.remove(i);
                return;
            }
        }

        for(int i = 0; i < player.location.characters.size(); i++){
            if(consumedName.equals(player.location.characters.get(i).id)){
                player.location.characters.remove(i);
                return;
            }
        }

        for(int i = 0; i < player.location.pathTo.size(); i++){
            if(consumedName.equals(player.location.pathTo.get(i).id)){
                player.location.pathTo.remove(i);
                return;
            }
        }

        if(consumedName.equals("health")){
            player.health = player.health - 1;

        }


    }

    //perform the dead command, reset the situation of the player
    private void dead(String playerName){
        Player player = model.getPlayer(playerName);
        model.stringToShow.add("Your health runs out. You are dead. \n");
        for(Artefact art : player.artefacts){
            player.location.artefacts.add(art);
        }
        player.artefacts.clear();
        player.health = 3;
        player.location = model.locations.get(0);

    }

    //perform look command to show whats in the location
    private void look(String playerName){
        Player player = model.getPlayer(playerName);
        model.setStringToShow(new ArrayList<String>(model.getPlayerLocation(playerName).show()));
        model.stringToShow.add("You can access from here: \n");
        for(Location locationEnd : player.location.pathTo){
            model.stringToShow.add(locationEnd.name + "\n");
        }
    }

    //perform get command to transfer the artefacts from the location into the player
    private void get(String playerName){

        Player player = model.getPlayer(playerName);
        for(int j = 0; j < model.inputCommands.size(); j++){
            for(int i = 0; i < player.location.artefacts.size(); i++){
                if(model.inputCommands.get(j).equals(player.location.artefacts.get(i).id)){
                    player.artefacts.add(player.location.artefacts.get(i));
                    player.location.artefacts.remove(i);
                    model.stringToShow.add("You picked up a " + player.artefacts.get(player.artefacts.size()-1).id);
                }
            }
        }

    }

    //perform inv or inventory command to show what is in the inventory of the player
    private void inventory(String playerName){
        Player player = model.getPlayer(playerName);
        model.stringToShow.add("You have \n");
        for(int i = 0; i < player.artefacts.size(); i++){
            model.stringToShow.add(player.artefacts.get(i).description + "\n");
        }
    }

    //perform the goto action change the location of the player
    private void goTo(String playerName){
        Player player = model.getPlayer(playerName);
        for(int i = 0; i < model.inputCommands.size(); i++){
            for(Location locationEnd : player.location.pathTo){
                if(model.inputCommands.get(i).equals(locationEnd.name)){
                    player.location = locationEnd;
                }
            }
        }
        look(playerName);
    }

    //perform the drop action, drop the artefacts from the player to the location
    private void drop(String playerName){
        Player player = model.getPlayer(playerName);
        for(int i = 0; i < model.inputCommands.size(); i ++){
            for(int j = 0; j < player.artefacts.size(); j++){
                if(model.inputCommands.get(i).equals(player.artefacts.get(j).id)){
                    player.location.artefacts.add(player.artefacts.get(j));
                    player.artefacts.remove(j);
                    model.stringToShow.add("You dropped a " + player.location.artefacts.get(player.location.artefacts.size()-1).id);
                }
            }
        }

    }

    //perform the health command, show the health of the player
    private void health(String playerName){
        Player player = model.getPlayer(playerName);
        model.stringToShow.add("Your health: \n" + player.health + "\n");
    }

}
