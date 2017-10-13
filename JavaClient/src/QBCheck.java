import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//TO DO, add user and server options

public class QBCheck extends Application {

    private String fileLocation;
    private Client client;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        client = new Client();

        if(!client.loadSettings()){
            client.saveSettings();
        }

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Nielsen QB Check");
        primaryStage.setScene(scene);
        Text titleText = new Text("Nielsen QuickBooks Login Check");

        client.openSocket();
        Text users = new Text(client.checkUsers());
        client.closeSocket();

        grid.add(titleText, 0, 0, 2, 1);
        grid.add(users, 0, 1, 2, 1);

        Button connectBtn = new Button("Connect");
        Button locateBtn = new Button("Locate");
        Button nameBtn = new Button("Name");
        Button exitBtn = new Button("Exit");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(connectBtn);
        hbBtn.getChildren().add(locateBtn);
        hbBtn.getChildren().add(nameBtn);
        hbBtn.getChildren().add(exitBtn);

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        locateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //open filebrowser to get location of remote desktop
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Remote Desktop Shortcut");
                fileLocation = fileChooser.showOpenDialog(primaryStage).toString();

            }
        });

       nameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                client.changeName("Test2");
            }
        });

        connectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               //launch qb remote server here
                Runtime runtime = Runtime.getRuntime();
                try{

                    //launch rdp
                    Process process = runtime.exec("C:\\Windows\\System32\\mstsc "+fileLocation);
                    client.openSocket();
                    client.notifyServer();
                    users.setText(client.checkUsers());
                    client.closeSocket();

                    //wait for rdp to close
                    process.waitFor();

                    //change logged in user to none
                    client.changeName("");
                    client.openSocket();
                    client.notifyServer();
                    users.setText(client.checkUsers());
                    client.closeSocket();
                    client.changeName("Test");

                }
                catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });

        grid.add(hbBtn, 1, 4);

        primaryStage.show();
    }
}
