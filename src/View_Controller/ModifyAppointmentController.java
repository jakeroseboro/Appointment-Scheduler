package View_Controller;

import Database.Database;
import Model.Customer;
import Model.Data;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Model.Appointment;
import Model.Data.LocalDateTime_Interface;

import static Model.Data.*;

public class ModifyAppointmentController implements Initializable {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private ComboBox contactComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox startTimeComboBox;
    @FXML
    private ComboBox endTimeComboBox;
    @FXML
    private ComboBox userCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button searchButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, String> apptIdCol;
    @FXML
    private TableColumn<Appointment, String> custCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> descCol;
    @FXML
    private TableColumn<Appointment, String> locCol;
    @FXML
    private TableColumn<Appointment, String> contactCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> urlCol;
    @FXML
    private TableColumn<Appointment, String> dateCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, String>UserColumn;

    ObservableList<Appointment> appointmentTable = FXCollections.observableArrayList();

    /**
     * This handles canceling and returns to main screen.
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
     * This deletes an appointment from the database.
     * @param event
     * @throws IOException
     */
    @FXML
    private void DeleteHandler(ActionEvent event) throws IOException {
        Appointment appointmentToDelete = (Appointment) appointmentTableView.getSelectionModel().getSelectedItem();

        if (appointmentToDelete == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Selected!");
            alert.setContentText("You did not select an appointment to delete");
            alert.showAndWait();
        }
        int deleteApptId = appointmentToDelete.getId();
        String custName = appointmentToDelete.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Appointment?");
        alert.setHeaderText("Please Confirm...");
        alert.setContentText("Are you sure you want to delete appointment ID #" + deleteApptId + " for customer " + custName + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteAppointment(deleteApptId);

            System.out.println("Delete Successful! Refresh page.");
            Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyAppointment.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert ConfirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Successful");
            alert.setHeaderText("Deletion Successful!");
            alert.setContentText("Appointment for customer " + custName + " was successfully deleted from the database.");
            alert.showAndWait();

        }
    }

    /**
     * This populates the form with user selected appointment.
     * @param event
     * @throws IOException
     */
    @FXML
    private void EditHandler(ActionEvent event) throws IOException {
        Appointment selectedAppointment = (Appointment) appointmentTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Selected!");
            alert.setContentText("You did not select an appointment to edit.");
            alert.showAndWait();
        }
        idField.setText(Integer.toString(selectedAppointment.getId()));
        nameField.setText(selectedAppointment.getName());
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        contactComboBox.setValue(selectedAppointment.getContact());
        typeComboBox.setValue(selectedAppointment.getType());
        startTimeComboBox.setValue(selectedAppointment.getStart() + ":00");
        endTimeComboBox.setValue(selectedAppointment.getEnd() + ":00");
        userCombo.setValue(selectedAppointment.getUserName());

        String dateString = selectedAppointment.getDate();
        LocalDate localDateObj = LocalDate.parse(dateString);
        datePicker.setValue(localDateObj);


    }

    /**
     * This allows the user to save the appointment to the database
     * @param event
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    private void SaveHandler(ActionEvent event) throws IOException, SQLException {
        String apptId = idField.getText();
        String custName = nameField.getText();
        String editTitle = titleField.getText();
        String editDescription = descriptionField.getText();
        String editContact = contactComboBox.getValue().toString();
        String editType = typeComboBox.getValue().toString();
        String editLocation = locationField.getText();
        String editDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int checkAddTime = startTimeComboBox.getSelectionModel().getSelectedIndex();
        int checkEndTime = endTimeComboBox.getSelectionModel().getSelectedIndex();
        String editStartTime = startTimeComboBox.getValue().toString();
        String editEndTime = endTimeComboBox.getValue().toString();
        String user = userCombo.getValue().toString();


        System.out.println("Start Time Index: " + checkAddTime + " End Time Index: " + checkEndTime);

        if (editTitle.isEmpty() || editDescription.isEmpty() || editLocation.isEmpty() || editType.isEmpty() || editContact.isEmpty() || editDate.isEmpty() || editStartTime.isEmpty() || editEndTime.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Blank Field");
            alert.setContentText("All fields must be filled to continue!");
            alert.showAndWait();
        }
        if (checkEndTime == checkAddTime) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Time Error");
            alert.setHeaderText("Appointment Time Error!");
            alert.setContentText("Appointment start and end times cannot be the same.");
            alert.showAndWait();
        }
        if (checkEndTime < checkAddTime) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Time Error");
            alert.setHeaderText("Appointment Time Error!");
            alert.setContentText("Appointment start time must be earlier than the end time.");
            alert.showAndWait();
        }
        if (!checkYourself(editStartTime, editEndTime, editDate, custName, apptId)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Time Error");
            alert.setHeaderText("Appointment Time Error!");
            alert.setContentText("Appointment could not be scheduled due to overlap with another existing appiontment.");
            alert.showAndWait();
        }
        if (!insideBusinessHours(editStartTime, editEndTime, editDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Time Error");
            alert.setHeaderText("Appointment Time Error!");
            alert.setContentText("Please schedule your appointment within the business hours of 8:00 a.m. to 10:00 p.m. EST");
            alert.showAndWait();
        } else {
            if (checkYourself(editStartTime, editEndTime, editDate, custName, apptId) && insideBusinessHours(editStartTime, editEndTime, editDate)) {
                Data.editAppointment(apptId, editTitle, editDescription,editContact, editType, editLocation, editDate, editStartTime, editEndTime, user);

                System.out.println("Edit successful! Refresh page.");
                Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyAppointment.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Appointment Information Saved");
                alert.setHeaderText("Appointment Information Saved!");
                alert.setContentText("Changes made to appointment ID #" + apptId + " for customer " + custName + " have been saved.");
                alert.showAndWait();
            }
        }
    }

    /**
     * The following handles searches.
     * @param searchId
     * @return
     */
    public Appointment lookupId(String searchId) {
        for (Appointment a : appointmentTable) {
            if (Integer.toString(a.getId()).contains(searchId)) {
                return a;
            }
            else if (a.getName().contains(searchId)){
                return a;
            }
        }
        return null;
    }

    /**
     * The following handles searches.
     * @return
     */
    @FXML private void SearchHandler (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
            appointmentTableView.getSelectionModel().select(lookupId(searchString));
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
     * Inititalizes the form and populates tables. It includes two lambda functions. One to convert time to user default, the second to disable past dates in the date combo box.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startTimeComboBox.setItems(Data.getTimes());
        endTimeComboBox.setItems(Data.getTimes());
        typeComboBox.setItems(Data.getTypes());
        contactComboBox.setItems(Data.getContacts());
        userCombo.setItems(Data.getUsers());


        LocalDateTime_Interface convert = (String dateTime) -> {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return ldt;
        };
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    setDisable(true);
            }
        });


        Connection connection;
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

                appointmentTable.add(new Appointment(rs.getInt("Appointment_ID"),
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
        custCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        UserColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));

        appointmentTableView.setItems(appointmentTable);
    }
}
