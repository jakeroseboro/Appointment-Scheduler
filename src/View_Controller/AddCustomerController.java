package View_Controller;

import javafx.fxml.Initializable;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Model.Customer;

public class AddCustomerController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField address1Field;
    @FXML private ComboBox DivisionComboBox;
    @FXML private TextField zipField;
    @FXML private TextField phoneField;
    @FXML private ComboBox cityComboBox;
    @FXML private TableView<Customer> customersTableView;
    @FXML private TableColumn<Customer, Integer> custIDCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableColumn<Customer, String> custAddress1Col;;
    @FXML private TableColumn<Customer, String> custCityCol;
    @FXML private TableColumn<Customer, String> custZipCol;
    @FXML private TableColumn<Customer, String> custCountryCol;
    @FXML private TableColumn<Customer, String> custPhoneCol;
    ObservableList<Customer> customersTable = FXCollections.observableArrayList();
    ObservableList<String> divisions = FXCollections.observableArrayList();

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
     * Handles saving the form and adding it to the SQL Database
     * @param event
     * @throws IOException
     */
    @FXML private void SaveHandler (ActionEvent event) throws IOException{
        try{
            String addName = nameField.getText();
            String addAddress1 = address1Field.getText();
            String addDivision = DivisionComboBox.getValue().toString();
            String addZip = zipField.getText();
            String addPhone = phoneField.getText();
            String addCity = cityComboBox.getValue().toString();

            if (!addName.isEmpty() && !addAddress1.isEmpty() && !addCity.isEmpty() && !addZip.isEmpty() && !addPhone.isEmpty()) {

                Data.addCustomer(addName,addAddress1,addDivision, addZip, addPhone);

                Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/AddCustomer.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Customer Added");
                alert.setHeaderText("Customer Added!");
                alert.setContentText(addName + " was added to Customers.");
                alert.showAndWait();
                }
            else {
                if (addName.isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText( "Name field was left blank.");
                    alert.showAndWait();
                }
                if (addAddress1.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText("Address field was left blank.");
                    alert.showAndWait();
                }
                if (addZip.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText("Zip Code field was left blank.");
                    alert.showAndWait();
                }
                if (addPhone.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText("Phone number field was left blank.");
                    alert.showAndWait();
                }
                if (addCity.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText("City field was left blank.");
                    alert.showAndWait();
                }
                if (addDivision.isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Blank Field");
                    alert.setContentText("Division field was left blank.");
                    alert.showAndWait();
                }
              }
            } catch (SQLException s) {
                System.out.println("Couldn't add due to SQL Exception. Something wrong with fields");

                if (s.getMessage().contains("Postal_Code")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error adding data");
                    alert.setContentText(s.getMessage() + " (Zip Code)");
                    alert.showAndWait();
                }

                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error adding data");
                    alert.setContentText(s.getMessage());
                    alert.showAndWait();
                }
            }

         }

    /**
     * This filters the division list based on country selection
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
            DivisionComboBox.setItems(divisions);
        }catch (SQLException e){System.out.println(e.getMessage());}
        return divisions;
    }

    /**
     * Inititalizes the form and populates tables
     * @param location
     * @param rb
     */
    @Override
    public void initialize(URL location, ResourceBundle rb) {

        cityComboBox.setItems(Data.getCountries());
        DivisionComboBox.setItems(Data.getDivisions());

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

        custIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custAddress1Col.setCellValueFactory(new PropertyValueFactory<>("address"));
        custCityCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        custZipCol.setCellValueFactory(new PropertyValueFactory<>("zip"));
        custCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        customersTableView.setItems(customersTable);
    }
}
