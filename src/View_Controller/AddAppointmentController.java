package View_Controller;

import Database.Database;

import static Model.Data.checkForOverlap;
import static Model.Data.insideBusinessHours;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
import Model.Customer;
import Model.Data;

public class AddAppointmentController implements Initializable{
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private ComboBox contactComboBox;
    @FXML private ComboBox typeComboBox;
    @FXML private TextField searchField;
    @FXML private ComboBox startTimeComboBox;
    @FXML private ComboBox endTimeComboBox;
    @FXML private ComboBox userCombo;
    @FXML private DatePicker datePicker;
    @FXML private Button searchButton;
    @FXML private Button selectCustomerButton;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    @FXML private TableView<Customer> custNamesTableView;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> apptIdCol;
    @FXML private TableColumn<Appointment, String> nameCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> contactCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    @FXML private TableColumn<Appointment, String> UserColumn;

    ObservableList<Customer> SearchTable = FXCollections.observableArrayList();
    ObservableList<Appointment> currApptTable = FXCollections.observableArrayList();
    static ObservableList<Customer> idAndNamesTable = FXCollections.observableArrayList();

    /**
     * Returns you to the main screen.
     * @param event
     * @throws IOException
     */
    @FXML private void CancelHandler (ActionEvent event) throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all text field values, do you want to continue?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {

            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Object scene = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
            stage.setScene(new Scene((Parent) scene));
            stage.show();

        }
    }

    /**
     * Allows you to search a customer by name or id, includes partial search results.
     * @param Name
     * @return
     */
    public static Customer lookupCustomer(String Name) {
        ObservableList<Customer>searchList = FXCollections.observableArrayList();
        for (Customer cust : idAndNamesTable) {
            if (cust.getName().contains(Name)) {
            }
            else if(Integer.toString(cust.getId()).contains(Name)){
                return cust;
            }
        }
        return null;
    }

    /**
     * Utilizes the previous function
     * @param event
     * @throws IOException
     */
    @FXML private void SearchHandler (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
            custNamesTableView.getSelectionModel().select(lookupCustomer(searchString));
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Searched!");
            alert.setContentText("You did not type anything into the search box to search.");
            alert.showAndWait();
        }
    }

    /**
     * Populates the form based on user selection
     * @param event
     * @throws IOException
     */
    @FXML private void SelectHandler (ActionEvent event) throws IOException{
        Customer ApptToAdd = custNamesTableView.getSelectionModel().getSelectedItem();
        try{
            idField.setText(Integer.toString(ApptToAdd.getId()));
            nameField.setText(ApptToAdd.getName());
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Selected!");
            alert.setContentText("You must select an appointment to edit!");
            alert.showAndWait();
        }
    }

    /**
     * Handles saving the form and adding it to the SQL Database
     * @param event
     * @throws IOException
     */
    @FXML private void SaveHandler (ActionEvent event) throws IOException{
        try {
            int addId = Integer.parseInt(idField.getText());
            String idCheck = idField.getText();
            String addName = nameField.getText();
            String addTitle = titleField.getText();
            String addDescription = descriptionField.getText();
            String addLocation = locationField.getText();
            String addType = typeComboBox.getValue().toString();
            String addContact = contactComboBox.getValue().toString();
            String addDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int checkAddTime = startTimeComboBox.getSelectionModel().getSelectedIndex();
            int checkEndTime = endTimeComboBox.getSelectionModel().getSelectedIndex();
            String addStartTime = (String) startTimeComboBox.getValue().toString();
            String addEndTime = (String) endTimeComboBox.getValue().toString();
            String user = userCombo.getValue().toString();


            if (idCheck.isEmpty() || addTitle.isEmpty() || addDescription.isEmpty() || addLocation.isEmpty() || addType.isEmpty() || addContact.isEmpty() || addDate.isEmpty() || addStartTime.isEmpty() || user.isEmpty() || addEndTime.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Blank Field");
                alert.setContentText("Please complete all fields.");
                alert.showAndWait();
            }

            if (checkEndTime == checkAddTime) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Time Error!");
                alert.setContentText("Appointment start and end time cannot be the same.");
                alert.showAndWait();
            }

            if (checkEndTime < checkAddTime) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Appointment Time Error!");
                alert.setContentText("Appointment start time must be before end time");
                alert.showAndWait();
            }
            if (!checkForOverlap(addStartTime, addEndTime, addDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Time Error");
                alert.setHeaderText("Appointment Time Error!");
                alert.setContentText("Appointment could not be scheduled due to overlap with another existing appointment.");
                alert.showAndWait();
            }
            if (!insideBusinessHours(addStartTime, addEndTime, addDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Time Error");
                alert.setHeaderText("Appointment Time Error!");
                alert.setContentText("Please schedule your appointment within the business hours of 8:00 a.m. to 10:00 p.m. EST");
                alert.showAndWait();
            }

            else if(checkForOverlap(addStartTime, addEndTime, addDate) && insideBusinessHours(addStartTime, addEndTime, addDate)) {
                try {
                    Data.addAppointment(addId, addName, addTitle, addDescription, addLocation, addType, addDate, addStartTime, addEndTime, addContact, user);
                    Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddAppointment.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Appointment Added");
                    alert.setHeaderText("Appointment Added!");
                    alert.setContentText("Appointment was added for customer " + addName + ".");
                    alert.showAndWait();
                } catch (SQLException e) {
                    System.out.println("ERROR WITH SQL: " + e.getMessage());
                }
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
         }
        }

    /**
     * Inititalizes the form and populates tables. It includes two lambda functions. One to convert time to user default, the second to disable past dates in the date combo box.
      * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        Data.LocalDateTime_Interface convert = (String dateTime) -> {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return ldt;
        };


        idField.setDisable(true);
        nameField.setDisable(true);
        typeComboBox.setItems(Data.getTypes());
        userCombo.setItems(Data.getUsers());

        datePicker.setDayCellFactory(picker -> new DateCell() { //This is a lambda that disables past dates
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    setDisable(true);
            }
        });

        startTimeComboBox.setDisable(false);
        startTimeComboBox.setItems(Data.getTimes());
        endTimeComboBox.setDisable(false);
        endTimeComboBox.setItems(Data.getTimes());
        cancelButton.setDisable(false);
        addButton.setDisable(false);
        contactComboBox.setItems(Data.getContacts());

        Connection connection;
        try {
            connection = Database.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Customer_ID, Customer_Name FROM customers ORDER BY Customer_ID;");
            while (rs.next()) {
                idAndNamesTable.add(new Customer(rs.getInt("Customer_ID"), rs.getString("Customer_Name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custNamesTableView.setItems(idAndNamesTable);

        try {
            connection = Database.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Appointment_ID, Customer_Name,User_Name, Title, Description, Location, Type, DATE(Start) date, Start, End, Contact_Name\n" +
                    "FROM customers c INNER JOIN appointments a ON c.Customer_ID = a.Customer_ID\n" +
                    "INNER JOIN users u ON u.User_ID = a.User_ID " +
                    "INNER JOIN contacts b ON b.Contact_ID = a.Contact_ID ORDER BY Start;");

            while (rs.next()) {
                LocalDateTime zonedStart = convert.stringToLocalDateTime(rs.getString("Start"));
                LocalDateTime zonedEnd = convert.stringToLocalDateTime(rs.getString("End"));
                String zonedStartString = zonedStart.toString().substring(11,16);
                String zonedEndString = zonedEnd.toString().substring(11,16);

                currApptTable.add(new Appointment(rs.getInt("Appointment_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Contact_Name"),
                        rs.getString("Type"),
                        rs.getString("Date"),
                        zonedStartString,
                        zonedEndString,
                        rs.getString("User_Name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModifyAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        UserColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));

        appointmentTableView.setItems(currApptTable);
    }
}
