import java.io.*;
import java.net.*;

public class Client{

    private final String address = "localhost";
    private String name = "Test";
    private final int port = 9001;
    private Socket s;

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


}