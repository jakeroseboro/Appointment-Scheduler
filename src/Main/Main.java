package Main;

import Database.Database;
import java.sql.SQLException;

import Model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        launch(args);
        Database.closeConnection();
    }
}
