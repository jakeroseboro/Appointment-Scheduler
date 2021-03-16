package View_Controller;

import Database.Database;
import Model.Data;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Model.Customer;

import static Model.Data.*;

public class ModifyCustomerController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField address1Field;
    @FXML
    private ComboBox divisionComboBox;
    @FXML
    private TextField zipField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox cityComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TableView<Customer> customersTableView;
    @FXML
    private TableColumn<Customer, Integer> custIdCol;
    @FXML
    private TableColumn<Customer, String> custNameCol;
    @FXML
    private TableColumn<Customer, String> custAddress1Col;
    @FXML
    private TableColumn<Customer, String> custAddress2Col;
    @FXML
    private TableColumn<Customer, String> custCityCol;
    @FXML
    private TableColumn<Customer, String> custZipCol;
    @FXML
    private TableColumn<Customer, String> custCountryCol;
    @FXML
    private TableColumn<Customer, String> custPhoneCol;

    ObservableList<Customer> customersTable = FXCollections.observableArrayList();
    ObservableList<String> divisions = FXCollections.observableArrayList();

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
     * Populates the form based on user selection
     * @param event
     * @throws IOException
     */
    @FXML
    private void EditHandler(ActionEvent event) throws IOException {
        Customer selectedCustomer = (Customer) customersTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Selected!");
            alert.setContentText("You did not select an appointment to edit.");
            alert.showAndWait();
        }
        idField.setText(Integer.toString(selectedCustomer.getId()));
        nameField.setText(selectedCustomer.getName());
        address1Field.setText(selectedCustomer.getAddress());
        divisionComboBox.setValue(selectedCustomer.getDivision());
        cityComboBox.setValue(selectedCustomer.getCountry());
        zipField.setText(selectedCustomer.getZip());
        phoneField.setText(selectedCustomer.getPhone());

    }
    /**
     * Deletes the customer from the database
     * @param event
     * @throws IOException
     */
    @FXML
    private void DeleteHandler(ActionEvent event) throws IOException {
        Customer customerToDelete = customersTableView.getSelectionModel().getSelectedItem();

        if (customerToDelete == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Nothing Selected!");
            alert.setContentText("You did not select an appointment to delete");
            alert.showAndWait();
        }
        int deleteCustomerId = customerToDelete.getId();
        String custName = customerToDelete.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Customer?");
        alert.setHeaderText("Please Confirm...");
        alert.setContentText("Are you sure you want to delete Customer ID #" + deleteCustomerId + " for customer " + custName + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteCustomer(deleteCustomerId);
            System.out.println("Delete Successful! Refresh page.");
            Parent parent = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyCustomer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert ConfirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Successful");
            alert.setHeaderText("Deletion Successful!");
            alert.setContentText("Customer: " + custName + " was successfully deleted from the database.");
            alert.showAndWait();
        }
    }
    /**
     * Handles saving the form and adding it to the SQL Database
     * @param event
     * @throws IOException
     */
    @FXML
    private void SaveHandler(ActionEvent event) throws IOException {
        int id = Integer.parseInt(idField.getText());
        String editName = nameField.getText();
        String editAddress1 = address1Field.getText();
        String editZip = zipField.getText();
        String editPhone = phoneField.getText();
        String editDivision = (String) divisionComboBox.getValue().toString();

        if (editName.isEmpty() || editAddress1.isEmpty() || editDivision.isEmpty() || editZip.isEmpty() || editPhone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Blank Field");
            alert.setContentText("All fields must be filled to continue!");
            alert.showAndWait();
        }
         else {

            Data.editCustomer(id, editName, editAddress1, editZip, editDivision, editPhone);

            System.out.println("Edit successful! Refresh page.");
            Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/ModifyCustomer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("Customer Information Saved!");
            alert.setContentText("Changes made to customer ID #" + id + " for customer " + editName + " have been saved.");
            alert.showAndWait();
            }
        }

    public Customer lookupCustomerName(String searchName) {
        for (Customer cust : customersTable) {
            if (cust.getName().contains(searchName)) {
                return cust;
            }
            else if (Integer.toString(cust.getId()).contains(searchName)){
                return cust;
            }
        }
        return null;
    }

    /**
     * This filters the division list based on country selection.
     * @param event
     * @return
     * @throws IOException
     */
    @FXML
    private ObservableList<String> divisionHandler(ActionEvent event) throws IOException{
        divisions.removeAll(divisions);


        Connection connection;
        try {
            String country = cityComboBox.getValue().toString();
            connection = Database.getConnection();
            ResultSet getID = connection.createStatement().executeQuery(String.format("SELECT Country_ID FROM countries WHERE country = '%s'", country));
            getID.next();

            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = '%s'", getID.getInt("Country_ID")));
            while (rs.next()) {
                divisions.add(rs.getString("Division"));
            }
            divisionComboBox.setItems(divisions);
        }catch (SQLException e){System.out.println(e.getMessage());}
        return divisions;
    }

    @FXML private void SearchHandler (ActionEvent event) throws IOException {
        String searchString = searchField.getText();
        if (!searchString.isEmpty()) {
            customersTableView.getSelectionModel().select(lookupCustomerName(searchString));
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
     * Inititalizes the form and populates tables
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        divisionComboBox.setItems(Data.getDivisions());
        cityComboBox.setItems(Data.getCountries());

        Connection connection;
        try {
            connection = Database.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, c.Division_ID, Division, o.Country_ID, o.Country\n" +
                    "FROM customers c INNER JOIN first_level_divisions f ON c.Division_ID = f.Division_ID\n" +
                    "INNER JOIN countries o ON o.Country_ID = f.Country_ID ORDER BY Customer_ID;");
            while (rs.next()) {
                customersTable.add(new Customer(rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Division"),
                        rs.getString("Postal_Code"),
                        rs.getString("Country"),
                        rs.getString("Phone"),
                        rs.getInt("Division_ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custAddress1Col.setCellValueFactory(new PropertyValueFactory<>("address"));
        custCityCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        custZipCol.setCellValueFactory(new PropertyValueFactory<>("zip"));
        custCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        customersTableView.setItems(customersTable);
    }
}


