import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Client{

    private String address = "localhost";
    private String settingsLocation = "\\Documents\\NielsenQBCheck\\Settings";
    private String name = "Test";
    private int port = 9001;
    private Socket s;

    public Client(){
        settingsLocation = System.getProperty("user.home")+settingsLocation;
    }

    public void openSocket(){
        try {
            s = new Socket(address, port);
        }
        catch(Exception e){


        }
    }

    public void changeName(String name){
        this.name = name;
    }

    //checks the current user logged in from the server
    public String checkUsers(){
        BufferedReader in = null;
        PrintWriter out = null;
        String msg = "User logged in: ";
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);

            String temp = in.readLine();
            if(temp.equals("")){
                msg = "User logged in: None";
                out.println(temp);
            }
            else{
                msg += temp;
                out.println(temp);
            }
        }
        catch(Exception e){

        }

        return msg;

    }

    public void notifyServer(){
        PrintWriter out = null;
        BufferedReader in = null;


        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            in.readLine();
            out.println(name);
        }
        catch(Exception e){

        }

    }

    public void closeSocket(){
        try {
            s.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public boolean loadSettings(){

        System.out.println("Loading settings...");
        List<String> settingsList = null;
        try {
            File settings = new File(settingsLocation);
            if (settings.exists()) {
                System.out.println("File exists");
                //load correct user settings here
                settingsList = Files.readAllLines(Paths.get(settingsLocation), StandardCharsets.UTF_8);
                if(settingsList.size()!=3){
                    return false;
                }
                name = settingsList.get(0);
                address = settingsList.get(1);
                port = Integer.parseInt(settingsList.get(2));
                System.out.println("Settings loaded...");
                System.out.println(name);
                System.out.println(address);
                System.out.println(port);
                return true;
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("No settings file exists");
        return false;
    }

    public boolean saveSettings(){
        ArrayList<String> settingsStr = new ArrayList<>();

        System.out.println("Writing new settings file...");
        try {
            if(!Files.exists(Paths.get(settingsLocation).getParent())){
                Files.createDirectories(Paths.get(settingsLocation).getParent());
            }
            settingsStr.add(name+"\n");
            settingsStr.add(address+"\n");
            settingsStr.add(port+"\n");
            Files.write(Paths.get(settingsLocation), settingsStr, StandardCharsets.UTF_8);

            System.out.println("Settings file written.");
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}