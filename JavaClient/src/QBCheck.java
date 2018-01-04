import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Optional;

//TO DO, LAST KNOWN USER ISSUES IF 1 LOGIN, THEN 2 LOGIN, EXIT 1, 1 IS CLEARED FROM THE SERVER, BUT 2 IS ACTUALLY LOGGED IN

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
        fileLocation = client.getRdpLocation();


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        //grid.setGridLinesVisible(true);
        //grid.setHgap(10);
        grid.setVgap(10);

        Scene scene = new Scene(grid, 300, 275);

        primaryStage.setTitle("Nielsen QB Check");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:Nielsen2.png"));

        //Settings Buttons
        Button nameBtn = new Button(client.getName());
        nameBtn.setMinWidth(50);
        nameBtn.setStyle(("-fx-font-size: 10"));
        Button locateBtn = new Button("RDP");
        locateBtn.setMinWidth(50);
        locateBtn.setStyle(("-fx-font-size: 10"));
        Button addressBtn = new Button("Server");
        addressBtn.setMinWidth(50);
        addressBtn.setStyle(("-fx-font-size: 10"));
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setMinWidth(50);
        refreshBtn.setStyle(("-fx-font-size: 10"));
        grid.add(nameBtn, 0, 0, 1, 1);
        grid.add(locateBtn, 1,0,1,1);
        grid.add(addressBtn, 2, 0, 1, 1);
        grid.add(new Text(""), 3, 0, 1, 1);
        grid.add(refreshBtn, 8, 0, 1, 1);

        //Main Text
        Text titleText = new Text("Nielsen QuickBooks Check");
        titleText.setFont(Font.font(16));
        titleText.setUnderline(true);
        grid.add(titleText, 1, 3, 6, 2);

        //Users Text
        Text users;
        if(!client.openSocket()){
            users = new Text("Error: No connection");
        }else {
            users = new Text(client.checkUsers());
        }
        client.closeSocket();
        grid.add(users, 1, 7, 3, 1);

        //Main Buttons
        Button connectBtn = new Button("Connect");
        connectBtn.setStyle("-fx-font-size: 15");
        connectBtn.setMinWidth(100);
        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-font-size: 15");
        exitBtn.setMinWidth(100);
        grid.add(connectBtn, 1, 15, 2, 2);
        grid.add(exitBtn, 5, 15, 2, 2);

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                shutDownProcedures();
                System.exit(0);
            }
        });

        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //disable button here
                refreshBtn.setDisable(true);

                if(client.openSocket()){
                    users.setText(client.checkUsers());
                }
                else{
                    users.setText("Error: No connection");
                }
                client.closeSocket();
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(3000)));
                timeline.setOnFinished(actionEvent -> refreshBtn.setDisable(false));
                timeline.play();

            }
        });

        locateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //open filebrowser to get location of remote desktop
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Remote Desktop Shortcut");
                try {
                    fileLocation = fileChooser.showOpenDialog(primaryStage).toString();
                }catch(Exception exc){
                    fileLocation = client.getRdpLocation();
                    System.out.println("Not a valid filepath or user hit cancel on filebrowser");
                }
                client.setRdpLocation(fileLocation);
                client.saveSettings();

            }
        });

        nameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                TextInputDialog dialog = new TextInputDialog();

                dialog.setTitle("User Name");
                dialog.setHeaderText("Please enter a user name");
                dialog.setContentText("Name:");
                dialog.initStyle(StageStyle.UTILITY);
                Optional<String> result = dialog.showAndWait();
                if(result.isPresent()){
                    client.setName(result.get());
                    nameBtn.setText(client.getName());
                    client.saveSettings();
                }

            }
        });

        addressBtn.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent e) {

                // Create the custom dialog.
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Server Settings");
                dialog.setHeaderText("Server Address and Port");

                // Set the button types.
                ButtonType acceptButtonType = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(acceptButtonType, ButtonType.CANCEL);

                // Create the server and port labels and fields.
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField server = new TextField();
                server.setPromptText(client.getAddress());
                TextField portNum = new TextField();
                portNum.setPromptText(Integer.toString(client.getPort()));

                grid.add(new Label("Server:"), 0, 0);
                grid.add(server, 1, 0);
                grid.add(new Label("Port:"), 0, 1);
                grid.add(portNum, 1, 1);

                // Enable/Disable accept button depending on whether a server was entered.
                Node acceptButton = dialog.getDialogPane().lookupButton(acceptButtonType);
                acceptButton.setDisable(true);

                // Do some validation (using the Java 8 lambda syntax).
                server.textProperty().addListener((observable, oldValue, newValue) -> {
                    acceptButton.setDisable(newValue.trim().isEmpty());
                });

                dialog.getDialogPane().setContent(grid);


               // Convert the result to a server-port-pair when the accept button is clicked.
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == acceptButtonType) {
                        return new Pair<>(server.getText(), portNum.getText());
                    }
                    return null;
                });

                Optional<Pair<String, String>> result = dialog.showAndWait();
                result.ifPresent(serverPort -> {
                    client.setAddress(serverPort.getKey());
                    client.setPort(Integer.parseInt(serverPort.getValue()));
                    client.saveSettings();
                });
            }
        });

        connectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               //launch qb remote server here
                Runtime runtime = Runtime.getRuntime();
                try{

                    //launch rdp
                    //Process process = runtime.exec("C:\\Windows\\System32\\mstsc "+fileLocation);
                    client.openSocket();
                    client.notifyServer();
                    users.setText(client.checkUsers());
                    client.closeSocket();

                    //wait for rdp to close
                    //process.waitFor();

/*
                    //change logged in user to none
                    String lastUsed = client.getName();
                    client.setName("");
                    client.openSocket();
                    client.notifyServer();
                    users.setText(client.checkUsers());
                    client.closeSocket();
                    client.setName(lastUsed);
*/

                }
                catch (Exception exc){
                    //exc.printStackTrace();

                }
            }
        });

        primaryStage.show();
    }

    @Override
    public void stop(){
        shutDownProcedures();
    }

    private void shutDownProcedures(){
        if(client.checkUsers().equals("User logged in: "+client.getName())){
            client.setName("");
            client.openSocket();
            client.notifyServer();
            client.closeSocket();
        }
    }

}
