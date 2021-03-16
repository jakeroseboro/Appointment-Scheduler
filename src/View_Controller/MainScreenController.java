package View_Controller;

import Database.Database;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Model.Appointment;
import Model.Data.LocalDateTime_Interface;
import Model.User.TimeZoneInterface;
import Model.User;

public class MainScreenController implements Initializable{
    @FXML private Label timeZoneLabel;
    @FXML private RadioButton weeklyRadio;
    @FXML private RadioButton monthlyRadio;
    @FXML private RadioButton allRadio;
    @FXML private DatePicker datePicker;
    private ToggleGroup calendarRadio;
    private boolean isWeekly;
    private boolean isMonthly;

    @FXML private TableView<Appointment> mainCalendarTableView;
    @FXML private TableColumn<Appointment, String> apptIdCol;
    @FXML private TableColumn<Appointment, String> custCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> startTimeCol;
    @FXML private TableColumn<Appointment, String> endTimeCol;
    @FXML private TableColumn<Appointment, String> UserColumn;



    ObservableList<Appointment> appointmentSchedule = FXCollections.observableArrayList();

    /**
     * This is a standalone lamda that converts to the users default time settings.
     */
    LocalDateTime_Interface convert = (String dateTime) -> {
        //A lambda that converts time to local time
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };
    /**
     * Exits the program
     * @param event
     * @throws IOException
     */
    @FXML public void ExitHandler (ActionEvent event){
        System.exit(0);
    }
    /**
     * Takes you to the login screen
     * @param event
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML private void LogoutHandler (ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        User.setCurrentUserid(null);
        User.setCurrentUsername(null);
        Database.closeConnection();

        System.out.println("LOGGED OUT!");
        Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Takes user to the selected page
     * @param event
     * @throws IOException
     */
    @FXML private void AddCustomerHandler (ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/AddCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Takes user to the selected page
     * @param event
     * @throws IOException
     */
    @FXML private void EditCustomerHandler (ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Takes user to the selected page
     * @param event
     * @throws IOException
     */
    @FXML private void AddApptHandler (ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/AddAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Takes user to the selected page
     * @param event
     * @throws IOException
     */
    @FXML private void EditApptHandler (ActionEvent event) throws IOException {

        Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Takes user to the selected page
     * @param event
     * @throws IOException
     */
    @FXML private void ReportHandler (ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/Reports.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Views the selected date, allows you to view appointments by all, month, or week
     * @param event
     * @throws IOException
     */
    @FXML private void DateHandler (ActionEvent event) throws IOException {
        if (isWeekly) {
            viewByWeek();
        }
        else if (isMonthly) {
            viewByMonth();
        }
        else {
            viewAll();
        }
    }

    public void viewByMonth() {
        isWeekly = false;
        isMonthly = true;

        LocalDate datePicked = datePicker.getValue();
        String monthPicked = datePicked.toString().substring(5,7);
        String yearPicked = datePicked.toString().substring(0,4);

        System.out.println("month number: " + monthPicked + " year: " + yearPicked);

        Connection con;
        try {
            appointmentSchedule.clear();
            con = Database.getConnection();
            ResultSet getApptsByMonth = con.createStatement().executeQuery(String.format("SELECT Appointment_ID, Customer_Name,User_Name, Title, Description, Location, Type, DATE(Start) date, Start, End " +
                    "FROM customers c INNER JOIN appointments a ON c.Customer_ID = a.Customer_ID " +
                    "INNER JOIN users u ON u.User_ID = a.User_ID " +
                    "WHERE MONTH(Start) = '%s' AND YEAR(Start) = '%s' ORDER BY Start", monthPicked, yearPicked));
            while (getApptsByMonth.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(getApptsByMonth.getString("Start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(getApptsByMonth.getString("End"));
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);
                appointmentSchedule.add(new Appointment(getApptsByMonth.getInt("Appointment_ID"),
                        getApptsByMonth.getString("Customer_Name"),
                        getApptsByMonth.getString("Title"),
                        getApptsByMonth.getString("Description"),
                        getApptsByMonth.getString("Location"),
                        getApptsByMonth.getString("Type"),
                        getApptsByMonth.getString("Date"),
                        zonedStartString,
                        zonedEndString,
                        getApptsByMonth.getString("User_Name")));
            }
            mainCalendarTableView.setItems(appointmentSchedule);

            if (appointmentSchedule.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No results");
                alert.setHeaderText("No results");
                alert.setContentText("There are no appointments for the month and year you selected.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            System.out.println("Something wrong with SQL: " + e.getMessage());
        }
    }

    public void viewByWeek() {
        isWeekly = true;
        isMonthly = false;

        LocalDate datePicked = datePicker.getValue();
        String yearPicked = datePicker.getValue().toString().substring(0,4);
        WeekFields weekFields = WeekFields.of(Locale.US);
        int weekNumber = datePicked.get(weekFields.weekOfWeekBasedYear());
        String weekString = Integer.toString(weekNumber);

        System.out.println("week number: " + weekString + " year: " + yearPicked);

        Connection con;
        try {
            appointmentSchedule.clear();
            con = Database.getConnection();
            ResultSet getApptsByWeek = con.createStatement().executeQuery(String.format("SELECT Appointment_ID, Customer_Name,User_Name, Title, Description, Location, Type, DATE(Start) date, Start, End " +
                    "FROM customers c INNER JOIN appointments a ON c.Customer_ID = a.Customer_ID " +
                    "INNER JOIN users u ON u.User_ID = a.User_ID " +
                    "WHERE WEEK(DATE(Start))+1 = '%s' AND YEAR(Start) = '%s' ORDER BY Start", weekString, yearPicked));
            while (getApptsByWeek.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(getApptsByWeek.getString("start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(getApptsByWeek.getString("end"));
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);

                appointmentSchedule.add(new Appointment(getApptsByWeek.getInt("Appointment_ID"),
                        getApptsByWeek.getString("Customer_Name"),
                        getApptsByWeek.getString("Title"),
                        getApptsByWeek.getString("Description"),
                        getApptsByWeek.getString("Location"),
                        getApptsByWeek.getString("Type"),
                        getApptsByWeek.getString("Date"),
                        zonedStartString,
                        zonedEndString,
                        getApptsByWeek.getString("User_Name")));
            }
            mainCalendarTableView.setItems(appointmentSchedule);

            if (appointmentSchedule.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No results");
                alert.setHeaderText("No results");
                alert.setContentText("There are no appointments for the week and year you selected.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            System.out.println("Something wrong with SQL: " + e.getMessage());
        }
    }

    public void viewAll() {
        isWeekly = false;
        isMonthly = false;

        Connection con;
        try {
            appointmentSchedule.clear();
            con = Database.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT Appointment_ID, Customer_Name, User_Name, Title, Description, Location, Type, DATE(Start) date, Start, End\n" +
                    "FROM customers c INNER JOIN appointments a ON c.Customer_ID = a.Customer_ID " +
                    "INNER JOIN users u ON u.User_ID = a.User_ID ORDER BY Start;");
            while (rs.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(rs.getString("Start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(rs.getString("End"));
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);

                appointmentSchedule.add(new Appointment(rs.getInt("Appointment_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getString("Date"),
                        zonedStartString,
                        zonedEndString,
                        rs.getString("User_Name")));

            }
            mainCalendarTableView.setItems(appointmentSchedule);

        } catch (SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML private void WeeklyHandler (ActionEvent event) throws IOException { viewByWeek(); }

    @FXML private void MonthlyHandler (ActionEvent event) throws IOException { viewByMonth(); }

    @FXML private void AllHandler (ActionEvent event) throws IOException { viewAll(); }

    /**
     * Inititalizes the form and populates tables. Includes a lambda that displays user timezone.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        calendarRadio = new ToggleGroup();
        this.weeklyRadio.setToggleGroup(calendarRadio);
        this.monthlyRadio.setToggleGroup(calendarRadio);
        this.allRadio.setToggleGroup(calendarRadio);
        this.allRadio.setSelected(true);
        datePicker.setValue(LocalDate.now());
        isWeekly = false;
        isMonthly = false;
        viewAll();

        TimeZoneInterface display = () -> "Your Time Zone: " + ZoneId.systemDefault().toString(); //Lambda that displays user timezone

        timeZoneLabel.setText(display.getUserTimeZone());

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        custCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        UserColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
    }
}
