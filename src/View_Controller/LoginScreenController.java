package View_Controller;

import Database.Database;
import static Model.Data.fifteenMinutes;
import static Model.Data.login;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Model.User;


public class LoginScreenController implements Initializable{
    @FXML private Label appointmentManagementSystemLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;
    @FXML private Label timeZoneLabel;

    private String errorTitle;
    private String errorHeaderMissing;
    private String errorHeaderIncorrect;
    private String errorContentMissing;
    private String errorContentIncorrect;

    /**
     * Exits the program
     * @param event
     * @throws IOException
     */
    @FXML
    public void ExitHandler(ActionEvent event) {

        System.exit(0);

    }

    /**
     * Handles login and accesses main page if the info is correct.
     * @param event
     * @throws IOException
     */
    @FXML
    private void LoginHandler (ActionEvent event) throws IOException{
        String usernameInput, passwordInput;
        usernameInput = usernameField.getText();
        passwordInput = passwordField.getText();

        if (login(usernameInput, passwordInput)) {
            String filename = "src/Main/Login_Activity.txt";
            FileWriter fWriter = new FileWriter(filename, true);
            PrintWriter outputFile = new PrintWriter(fWriter);
            outputFile.println(usernameField.getText() + " logged in on " + LocalDateTime.now());
            System.out.println(usernameField.getText() + " logged in on " + LocalDateTime.now());
            outputFile.close();

            Connection con;
            try {
                con = Database.getConnection();
                ResultSet getUserInfo = con.createStatement().executeQuery(String.format("SELECT User_ID, User_Name FROM users WHERE user_Name='%s'", usernameInput));
                getUserInfo.next();
                User currentUser = new User(getUserInfo.getString("User_Name"), getUserInfo.getString("User_ID"), true);
                System.out.println("Current userId: " + User.getCurrentUserid() + " userName: " + User.getCurrentUsername());

            } catch (SQLException ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Login Successful! Login Screen -> Main Screen");
            Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            if (fifteenMinutes()) {
                System.out.println("User alerted");
            }
            else {
                System.out.println("User not alerted of any appointment soon.");
            }
        }
        else {
            if(!login(usernameInput, passwordInput)) {
                String filename = "src/Main/Login_Activity.txt";
                FileWriter fWriter = new FileWriter(filename, true);
                PrintWriter outputFile = new PrintWriter(fWriter);
                outputFile.println("Failed login attempt on " + LocalDateTime.now());
                System.out.println("Failed login attempt on " + LocalDateTime.now());
                outputFile.close();
            }

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(errorTitle);
                alert.setHeaderText(errorHeaderMissing);
                alert.setContentText(errorContentMissing);
                alert.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(errorTitle);
                alert.setHeaderText(errorHeaderIncorrect);
                alert.setContentText(errorContentIncorrect);
                alert.showAndWait();
            }
        }
    }

    /**
     * Inititalizes the form and populates tables. Includes a lambda that displays user timezone/location
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            User.TimeZoneInterface display = () -> "Your Time Zone: " + ZoneId.systemDefault().toString(); //Lambda that displays user timezone

            timeZoneLabel.setText(display.getUserTimeZone());

            rb = ResourceBundle.getBundle("Languages/lang", Locale.getDefault());
            if (Locale.getDefault().getLanguage().equals("fr") || Locale.getDefault().getLanguage().equals("en")) {
                appointmentManagementSystemLabel.setText( rb.getString("Title"));
                usernameLabel.setText(rb.getString("Username"));
                passwordLabel.setText(rb.getString("Password"));
                loginButton.setText(rb.getString("loginButton"));
                exitButton.setText(rb.getString("exitButton"));
                errorTitle = rb.getString("errorTitle");
                errorHeaderMissing = rb.getString("errorHeaderMissing");
                errorHeaderIncorrect = rb.getString("errorHeaderIncorrect");
                errorContentMissing= rb.getString("errorContentMissing");
                errorContentIncorrect = rb.getString("errorContentIncorrect");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
