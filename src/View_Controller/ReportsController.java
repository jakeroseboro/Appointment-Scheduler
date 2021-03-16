package View_Controller;

import Database.Database;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Model.Data.LocalDateTime_Interface;
import Model.User;

public class ReportsController implements Initializable{
    @FXML private ChoiceBox reportChoiceBox;
    @FXML private TextArea textAreaForReports;
    @FXML private Button resetButton;
    @FXML private Button generateReportButton;

    static ObservableList<String> reports = FXCollections.observableArrayList();

    /**
     * Below is a standalone lambda used to convert UTC to local date time.
     */
    LocalDateTime_Interface convert = (String dateTime) -> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };

    /**
     * Returns you to the main screen.
     * @param event
     * @throws IOException
     */
    @FXML
    private void CancelHandler(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all text field values, do you want to continue?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Object scene = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
            stage.setScene(new Scene((Parent) scene));
            stage.show();

        }
    }
    /**
     * Generates the chosen report
     * @param event
     * @throws IOException
     * @throws SQLException
     */
    @FXML private void GenerateHandler (ActionEvent event) throws IOException, SQLException {
        textAreaForReports.clear();

        String chosenReport = reportChoiceBox.getValue().toString();

        switch (chosenReport) {
            case "Appointment Types by Month":
                textAreaForReports.setText(reportApptTypesByMonth());
                break;
            case "Schedule for Consultant":
                textAreaForReports.setText(reportConsultantSchedule());
                break;
            case "Appointments per Customer":
                textAreaForReports.setText(reportApptsPerCustomer());
                break;
            default:
                break;
        }
    }
    /**
     * returns the selected report
     * @return
     */
    public static ObservableList<String> getReports() {
        reports.removeAll(reports);
        reports.add("Appointment Types by Month");
        reports.add("Schedule for Consultant");
        reports.add("Appointments per Customer");
        return reports;
    }
    /**
     * Below are the three types of reports. By month, Consultant/User Schedule, and my own choice of amount of appointments per customer.
     * @return
     * @throws SQLException
     */
    public String reportApptTypesByMonth() throws SQLException {
        Connection conn = Database.getConnection();
        try {


            StringBuilder report1text = new StringBuilder();
            report1text.append("Month    | # of each Type  \n______________________________________________________________________\n");


            ResultSet rs = conn.createStatement().executeQuery(String.format("SELECT MONTHNAME(Start), Type, COUNT(*) as Amount\n" +
                    "FROM appointments GROUP BY MONTHNAME(Start), Type;"));
            while (rs.next()) {
                report1text.append(rs.getString("MONTHNAME(Start)") + "          " + rs.getString("Amount") + "   " + rs.getString("Type") + "\n");
            }

            return report1text.toString();

        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }

    }

    public String reportConsultantSchedule() throws SQLException {
        Connection conn = Database.getConnection();
        try {

            StringBuilder report2text = new StringBuilder();
            report2text.append("Consultant " + User.getCurrentUsername() + "'s Schedule\n_______________________________________________________________________________________________\n" +
                    "Date                Start       End           Appointment Info\n_______________________________________________________________________________________________\n");

            ResultSet getSchedule = conn.createStatement().executeQuery(String.format("SELECT DATE(Start) Date, Start, End, Customer_Name, Title, Description, Type, Location "
                    + "FROM appointments a INNER JOIN customers c ON a.Customer_ID=c.Customer_ID WHERE User_ID='%s' ORDER BY Start;", User.getCurrentUserid()));

            while (getSchedule.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(getSchedule.getString("Start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(getSchedule.getString("End"));

                String date = getSchedule.getString("Date");
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);
                String name = getSchedule.getString("Customer_Name");
                String title = getSchedule.getString("Title");
                String description = getSchedule.getString("Description");
                String type = getSchedule.getString("Type");
                String location = getSchedule.getString("Location");

                report2text.append(date + "\t" + zonedStartString + "\t" + zonedEndString + "\t" + name + "     " + title + "     " + description + "     " +  type + "     " +  location + "\n\n");
            }


            return report2text.toString();

        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }

    public String reportApptsPerCustomer() throws SQLException {
        Connection conn = Database.getConnection();
        try {


            StringBuilder report3text = new StringBuilder();
            report3text.append("Customer - Number of Appointments\n___________________________________\n");


            ResultSet getApptsPerMonth = conn.createStatement().executeQuery(String.format("SELECT Customer_Name, COUNT(*)" +
                    "Amount FROM appointments a INNER JOIN customers c on a.Customer_ID = c.Customer_ID GROUP BY Customer_Name;"));


            while (getApptsPerMonth.next()) {
                String Customer = getApptsPerMonth.getString("Customer_Name");
                String amount = getApptsPerMonth.getString("Amount");

                report3text.append(Customer + " - " + amount + "\n");
            }

            return report3text.toString();

        } catch (SQLException e) {
            System.out.println("Error getting report: " + e.getMessage());
            return "Didn't work";
        }
    }
    /**
     * Inititalizes the form and populates tables
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportChoiceBox.setItems(getReports());
    }
}
