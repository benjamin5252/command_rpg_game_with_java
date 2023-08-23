import java.util.ArrayList;
import Action.Action;
import Entities.*;

//Model is a class hold most of the data
public class Model {
    //ArrayList to hold all the locations parsed from the entities file
    public ArrayList<Location> locations = new ArrayList<Location>();
    //ArrayList to hold all the players, according to the input command
    public ArrayList<Player> players = new ArrayList<Player>();
    //ArrayList to hold all the actions parsed from the action.json file
    public ArrayList<Action> actions = new ArrayList<Action>();
    //ArrayList to hold the string prepared to write to the client
    public ArrayList<String> stringToShow = new ArrayList<String>();
    //ArrayList to hold the split input command
    public ArrayList<String> inputCommands = new ArrayList<String>();

    //add new action into memory
    public void addAction(Action action){
        actions.add(action);
    }

    //set the input into StringToShow which is a holder for the string to write to the client
    public void setStringToShow(ArrayList<String> str){
        stringToShow = str;
    }

    public void clearStringToSHow(){
        stringToShow.clear();
    }

    //add a location to the memory
    public void addLocation(Location location){
        locations.add(location);
    }

    //return the location of the player by player name
    public Location getPlayerLocation(String playerName){


        for(int i = 0; i < players.size(); i++){
            if(players.get(i).name.equals(playerName)){
                return players.get(i).location;
            }
        }
        return null;
    }

    //check if there is the player already in the memory
    public Boolean hasPlayer(String playerName){
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).name.equals(playerName)){
                return true;
            }
        }


        return false;
    }

    //add new player if there is no same player in the memory
    public void addPlayer(String playerName){
        if(!hasPlayer(playerName)){
            players.add(new Player(playerName, locations.get(0)));
        }
    }

    //get Player object by playerName
    public Player getPlayer(String playerName){
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).name.equals(playerName)){
                return players.get(i);
            }
        }
        return null;
    }

}
