import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Client{
    private Preferences prefs;
    private String address = "localhost";
    private String name = "Default Name";
    private String rdpLocation = "";
    private int port = 9001;
    private Socket s;
    private String lastKnownUser = "";

    private final int timeout = 3000;

    public Client(){
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    public boolean openSocket(){
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(address, port), timeout);
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
                out = new PrintWriter(s.getOutputStream(), true);

                String temp = in.readLine();
                if (temp.equals("")) {
                    lastKnownUser = "None";
                    msg = "User logged in: None";
                    out.println(temp);
                } else {
                    lastKnownUser = temp;
                    msg += temp;
                    out.println(temp);
                }
            } catch (Exception e) {

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
        boolean userSettingsLoaded = false;

        name = prefs.get("Username", "Default Name");
        rdpLocation = prefs.get("RDPLocation", "");
        address = prefs.get("Address", "localhost");
        port = prefs.getInt("Port", 9001);
        if(!name.equals("Default Name") && !rdpLocation.equals("") &&
                !address.equals("localhost") && port != 9001){
            userSettingsLoaded = true;
        }
        if(userSettingsLoaded){
            System.out.println("User settings successfully loaded");
        } else {
            System.out.println("User settings either have default values or failed to load");
        }

        return userSettingsLoaded;
    }

    public boolean saveSettings(){
        boolean userSettingsSaved = false;

        prefs.put("Username", name);
        prefs.put("RDPLocation", rdpLocation);
        prefs.put("Address", address);
        prefs.putInt("Port", port);

        if(prefs.get("Username", "Default Name").equals(name) &&
                prefs.get("RDPLocation", "").equals(rdpLocation) &&
                prefs.get("Address", "localhost").equals(address) &&
                prefs.getInt("Port", 9001) == port){
                userSettingsSaved = true;
        }

        return userSettingsSaved;
    }
}