import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Controller() throws IOException {
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        if(strFromServer.startsWith("/authok")) {
                            //setAuthorized(true);
                            break;
                        }
                        mainTextArea.appendText(strFromServer + "\n");
                    }
                    while (true) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            break; }
                        mainTextArea.appendText(strFromServer);
                        mainTextArea.appendText("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    TextArea mainTextArea;
    @FXML
    TextField textForSend;

    public void sendMessage(){
        if(!textForSend.getText().isEmpty()) {
            //mainTextArea.appendText(textForSend.getText() + "\n");
            try {
                out.writeUTF(textForSend.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            textForSend.clear();
        }
    }

    public void btnSend(javafx.event.ActionEvent actionEvent) {
        sendMessage();
    }

    public void textSend(ActionEvent actionEvent) {
        sendMessage();
    }
}
