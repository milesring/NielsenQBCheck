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
    private String rdpLocation = "";
    private int port = 9001;
    private Socket s;
    private String lastKnownUser = "";

    public Client(){
        settingsLocation = System.getProperty("user.home")+settingsLocation;
    }

    public boolean openSocket(){
        try {
            s = new Socket(address, port);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort(){
        return port;
    }

    public void setRdpLocation(String loc){
        rdpLocation = loc;
    }

    public String getRdpLocation(){
        return rdpLocation;
    }

    //checks the current user logged in from the server
    public String checkUsers(){
        BufferedReader in = null;
        PrintWriter out = null;
        String msg = "User logged in: ";
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), false);

            String temp = in.readLine();
            if(temp.equals("")){
                lastKnownUser = "None";
                msg = "User logged in: None";
                out.println(temp);
            }
            else{
                lastKnownUser = temp;
                msg += temp;
                out.println(temp);
            }
        }
        catch(Exception e){

        }

        if(msg.equals("User logged in: ")){
            msg+=lastKnownUser;
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
        }catch(Exception e){
            //s was never opened
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
                if(settingsList.size()!=4){
                    return false;
                }
                name = settingsList.get(0);
                address = settingsList.get(1);
                port = Integer.parseInt(settingsList.get(2));
                rdpLocation = settingsList.get(3);
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
            settingsStr.add(name);
            settingsStr.add(address);
            settingsStr.add(Integer.toString(port));
            settingsStr.add(rdpLocation);
            Files.write(Paths.get(settingsLocation), settingsStr, StandardCharsets.UTF_8);

            System.out.println("Settings file written.");
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}