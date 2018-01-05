import java.io.*;
import java.net.*;
import java.util.prefs.Preferences;

class Client{
    private final Preferences prefs;
    private String address = "localhost";
    private String name = "Default Name";
    private String rdpLocation = "";
    private int port = 9001;
    private Socket s;

    public Client(){
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    public boolean openSocket(){
        try {
            s = new Socket();
            int timeout = 3000;
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
        BufferedReader in;
        PrintWriter out;
        String msg = "User logged in: ";

            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);

                String temp = in.readLine();
                String lastKnownUser = "";
                if (temp.equals("")) {
                    lastKnownUser = "None";
                    msg = "User logged in: None";
                    out.println(temp);
                } else {
                    lastKnownUser = temp;
                    msg += temp;
                    out.println(temp);
                }
            } catch (Exception ignored) {

            }
        return msg;

    }

    public void notifyServer(){
        PrintWriter out;
        BufferedReader in;


        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            in.readLine();
            out.println(name);
        }
        catch(Exception ignored){

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

    public void saveSettings(){
        prefs.put("Username", name);
        prefs.put("RDPLocation", rdpLocation);
        prefs.put("Address", address);
        prefs.putInt("Port", port);
    }
}