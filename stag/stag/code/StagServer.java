import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class StagServer
{
    public Model model = new Model();
    public Controller controller = new Controller(model);

    public static void main(String args[])
    {
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);


    }

    public StagServer(String entityFilename, String actionFilename, int portNumber)
    {
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            controller.parseEntity(entityFilename);
            controller.parseActions(actionFilename);
            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss)
    {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException
    {
        //update inputCommands
        String line = in.readLine();
        //add paddings around the specific characters, for easier parsing
        line = paddingSpecialStr(line, ":");
        //use scanner to split the input and store them into model.inputCommands
        try {
            Scanner commandScanner = new Scanner(line);
            while (commandScanner.hasNext()){

                String command = commandScanner.next();
                model.inputCommands.add(command);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //add the player into the game, a simple check for existence is in the model
        model.addPlayer(model.inputCommands.get(0));
        //Before access the memory for showing the string, clean it
        model.clearStringToSHow();
        //controller handleCommand to perform all of the actions assign by the input.
        // model.inputCommands.get(0) is the playerName
        controller.handleCommand(model.inputCommands.get(0));
        //write the string in the model.stringToShow into client
        for(int i = 0; i < model.stringToShow.size(); i++){
            out.write(model.stringToShow.get(i));
        }
        //after all of the action, clear the memory for the commands.
        //get ready for the next command
        model.inputCommands.clear();
    }

    //add paddings around the specific characters, for easier parsing
    private String paddingSpecialStr(String incomingCommand, String SpecialStr){
        if(incomingCommand.contains(SpecialStr)){
            incomingCommand = incomingCommand.replace(SpecialStr, " " + SpecialStr + " ");
        }

        return  incomingCommand;
    }

}

