package Model;

import static Database.Database.connection;

import java.io.IOError;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Model.User;
import Model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class Data {
    static ObservableList<String> contacts = FXCollections.observableArrayList();
    static ObservableList<String> states = FXCollections.observableArrayList();
    static ObservableList<String> countries = FXCollections.observableArrayList();
    static ObservableList<String> types = FXCollections.observableArrayList();
    static ObservableList<String> times = FXCollections.observableArrayList();
    static ObservableList<String> users = FXCollections.observableArrayList();

    /**
     *This checks the SQL server to confirm username and password
     * @param usernameText
     * @param passwordText
     * @return
     */
    public static boolean login(String usernameText, String passwordText){
        try{
            Database.createConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE User_Name=? AND Password=?");
            statement.setString(1, usernameText);
            statement.setString(2,passwordText);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return true;
            }
            else{
                return false;
            }
            } catch (ClassNotFoundException | SQLException e){
            System.out.println("Error! " + e.getMessage());
            return false;
            }
        }

    /**
     * This alerts users that they have an appointment in 15 minutes
     * @return
     */
    public static boolean fifteenMinutes(){
        try {
            ResultSet earliestAppt = connection.createStatement().executeQuery(String.format("SELECT Customer_Name, Appointment_ID, Start "
                            + "FROM customers c INNER JOIN appointments a ON c.Customer_ID=a.Customer_ID INNER JOIN users u ON a.User_ID=u.User_ID "
                            + "WHERE a.User_ID='%s' AND a.Start BETWEEN '%s' AND '%s'",
                    User.getCurrentUserid(), LocalDateTime.now(ZoneId.of("UTC")), LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15)));
            earliestAppt.next();

            String name = earliestAppt.getString("Customer_Name");
            String ID = earliestAppt.getString("Appointment_ID");
            String Start = earliestAppt.getString("Start");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Soon");
            alert.setHeaderText("Appointment Soon!");
            alert.setContentText("You have an appointment with User ID: "+ID + ": " + name + " at " + Start + ".");
            alert.showAndWait();
            return true;
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment");
            alert.setHeaderText("No Appointment");
            alert.setContentText("You do not have an appointment soon");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * This allows you to add a customer to the SQL database. It logs the User ID which is what associates a user to an appointment. If you generate user report, only 'test' appointments show up for 'test'.
     * @param name
     * @param address
     * @param state
     * @param zip
     * @param phone
     * @throws SQLException
     */
    public static void addCustomer(String name, String address,String state, String zip, String phone) throws SQLException {
        ResultSet getDivision_ID = connection.createStatement().executeQuery(String.format("SELECT Division_ID FROM first_level_divisions WHERE Division = '%s'", state));
        getDivision_ID.next();

        connection.createStatement().executeUpdate(String.format("INSERT INTO customers "
                        + "(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                        "VALUES ('%s', '%s','%s', '%s', '%s', '%s', '%s', '%s', '%s')",
              name, address, zip, phone, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername(), getDivision_ID.getInt("Division_ID")));


    }

    /**
     * This allows you to delete a customer from the database
     * @param id
     */
    public static void deleteCustomer(int id){
        try {
            ResultSet getAddressId = connection.createStatement().executeQuery(String.format("SELECT * FROM customers WHERE Customer_ID='%s'", id));
            getAddressId.next();

            connection.createStatement().executeUpdate(String.format("DELETE FROM customers"
                    + " WHERE Customer_ID='%s'", id));

            connection.createStatement().executeUpdate(String.format("DELETE FROM appointments"
                    + " WHERE Customer_ID='%s'", id));

        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    /**
     * This allows you to make updates to a customers data in the database
     * @param id
     * @param name
     * @param address1
     * @param zip
     * @param state
     * @param phone
     */
    public static void editCustomer(int id, String name, String address1, String zip, String state, String phone) {
        try {
            ResultSet getDivision_ID = connection.createStatement().executeQuery(String.format("SELECT Division_ID FROM first_level_divisions WHERE Division = '%s'", state));
            getDivision_ID.next();

            connection.createStatement().executeUpdate(String.format("UPDATE customers"
                            + " SET Customer_Name='%s', Address='%s', Postal_Code='%s', Phone='%s', Create_Date='%s', Created_By='%s', Last_Update='%s', Last_Updated_By='%s', Division_ID='%s'"
                            + " WHERE Customer_ID='%s'",
                    name, address1, zip, phone, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername(), getDivision_ID.getInt("Division_ID"), id));


        } catch (Exception e) {
            System.out.println("Error editing customer: " + e.getMessage());
        }
    }

    /**
     * This returns the types of appointments available
     * @return
     */
    public static ObservableList<String> getTypes() {
        types.removeAll(types);
        types.add("Intake");
        types.add("Cleaning");
        types.add("Cavity Filling");
        types.add("Root Canal");
        types.add("X-Rays");
        return types;
    }

    /**
     * This returns the name of contacts provided in the SQL data
     * @return
     */
    public static ObservableList<String> getContacts(){
        try{
            contacts.removeAll(contacts);
            ResultSet contactList = connection.createStatement().executeQuery("SELECT Contact_Name FROM contacts;");
            while (contactList.next()){
                contacts.add(contactList.getString("Contact_Name"));
            }
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return contacts;
    }

    public static ObservableList<String> getUsers(){
        try{
           users.removeAll(users);
            ResultSet contactList = connection.createStatement().executeQuery("SELECT User_Name FROM users;");
            while (contactList.next()){
                users.add(contactList.getString("User_Name"));
            }
        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return users;
    }

    /**
     * This returns times that are then added to the combobox
     * @return
     */
    public static ObservableList<String> getTimes() {
        try {
            times.removeAll(times);
            for (int i = 0; i < 24; i++ ) {
                String hour;
                if(i < 10) {
                    hour = "0" + i;
                }

                else {
                    hour = Integer.toString(i);
                }
                times.add(hour + ":00:00");
                times.add(hour + ":15:00");
                times.add(hour + ":30:00");
                times.add(hour + ":45:00");
            }
            times.add("24:00:00");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return times;
    }

    /**
     * This returns a name of all divisions provided
     * @return
     */
    public static ObservableList<String> getDivisions() {
        try {
            states.removeAll(states);
            ResultSet stateList = connection.createStatement().executeQuery("SELECT Division FROM first_level_divisions;");
            while (stateList.next()) {
                states.add(stateList.getString("Division"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return states;
    }

    /**
     * This returns all countries provided for the database
     * @return
     */
    public static ObservableList<String> getCountries(){
        try{
        countries.removeAll(countries);
        ResultSet countryList = connection.createStatement().executeQuery("SELECT Country FROM countries;");
        while (countryList.next()) {
            countries.add(countryList.getString("Country"));
        }
    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
        return countries;
    }

    /**
     * This converts to local date time
     * @param time
     * @param date
     * @return
     */
    public static LocalDateTime stringToLDT_UTC(String time, String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =  LocalDateTime.parse(date + " " + time, format).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return ldt;
    }

    /**
     * This converts to EST and is used to check for business hours
     * @param time
     * @param date
     * @return
     */
    public static LocalDateTime stringToLDT_EST(String time, String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =  LocalDateTime.parse(date + " " + time, format).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDateTime();
        return ldt;
    }

    /**
     * This is an interface that uses the previous function
     */
    public interface LocalDateTime_Interface{
        LocalDateTime stringToLocalDateTime(String dateTime);
    }

    /**
     * This checks to confirm that the scheduled appt is inside business hours
     * @param startTime
     * @param endTime
     * @param date
     * @return
     */
    public static boolean insideBusinessHours(String startTime, String endTime, String date) {

        try {
            LocalDateTime localStart = stringToLDT_EST(startTime, date);
            LocalDateTime localEnd = stringToLDT_EST(endTime, date);
            String UTCstart = localStart.toString().substring(11, 16);
            String UTCend = localEnd.toString().substring(11, 16);

            LocalTime enteredStart = LocalTime.parse(UTCstart);
            LocalTime enteredEnd = LocalTime.parse(UTCend);
            LocalTime openingHour = LocalTime.parse("08:59");
            LocalTime closingHour = LocalTime.parse("23:01");
            Boolean startTimeAllowed = enteredStart.isAfter(openingHour);
            Boolean startTimeToo = enteredStart.isBefore(closingHour);
            Boolean endTimeAllowed = enteredEnd.isBefore(closingHour);
            Boolean endTimeToo = enteredEnd.isAfter(openingHour);

            if (startTimeAllowed && endTimeAllowed && startTimeToo && endTimeToo) {
                System.out.println("No Time Errors");
                return true;
            } else {
                System.out.println("Time Error");
                return false;
            }
        }catch (IOError e){
            e.getMessage();
        }
return false;
    }

    /**
     * This checks for appt overlap
     * @param startTime
     * @param endTime
     * @param date
     * @return
     */
    public static boolean checkForOverlap(String startTime, String endTime, String date) {
        try {

            LocalDateTime localStart = stringToLDT_UTC(startTime, date);
            LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
            String UTCstart = localStart.toString();
            String UTCend = localEnd.toString();

            ResultSet getOverlap = connection.createStatement().executeQuery(String.format(
                    "SELECT Start, End, Customer_Name FROM appointments a INNER JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                            "WHERE ('%s' >= Start AND '%s' <= End) " +
                            "OR ('%s' <= Start AND '%s' >= End) " +
                            "OR ('%s' <= Start AND '%s' >= Start) " +
                            "OR ('%s' <= End AND '%s' >= End)",
                    UTCstart, UTCstart, UTCend, UTCend, UTCstart, UTCend, UTCstart, UTCend));
            getOverlap.next();
            System.out.println("APPOINTMENT OVERLAP: " + getOverlap.getString("Customer_Name"));
            return false;
        } catch (SQLException e) {
            System.out.println("No overlap errors");
            return true;
        }
    }

    /**
     * This checks for self-overlap.
     * @param startTime
     * @param endTime
     * @param date
     * @param name
     * @param apptId
     * @return
     */
    public static boolean checkYourself(String startTime, String endTime, String date, String name, String apptId) {
        try {

            LocalDateTime localStart = stringToLDT_UTC(startTime, date);
            LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
            String UTCstart = localStart.toString();
            String UTCend = localEnd.toString();

            ResultSet getOverlap = connection.createStatement().executeQuery(String.format(
                    "SELECT Start, End, Customer_Name, Appointment_ID FROM appointments a INNER JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                            "WHERE ('%s' >= Start AND '%s' <= End) " +
                            "OR ('%s' <= Start AND '%s' >= End) " +
                            "OR ('%s' <= Start AND '%s' >= Start) " +
                            "OR ('%s' <= End AND '%s' >= End)",
                    UTCstart, UTCstart, UTCend, UTCend, UTCstart, UTCend, UTCstart, UTCend));
            getOverlap.next();

            String checkStart = getOverlap.getString("Start").substring(0,16);
            String checkUTCstart = UTCstart.replace('T', ' ');
            String checkEnd = getOverlap.getString("End").substring(0,16);
            String checkUTCend = UTCend.replace('T', ' ');


            if (getOverlap.getString("Customer_Name").equals(name) && getOverlap.getString("Appointment_ID").equals(apptId)){
                System.out.println("Only conflicting appointment is your own. Save over self: " + getOverlap.getString("Customer_Name"));
                return true;
            }
            else {
                System.out.println("Went to else");
                System.out.println(getOverlap.getString("Customer_Name") + " " + name + " " + getOverlap.getString("Appointment_ID") + " " + apptId + " "  + checkStart + " " + checkUTCstart + " " + checkEnd + " " + checkUTCend);
                return false;
            }

        } catch (SQLException e) {
            return true;
        }


    }

    /**
     * This allows you to add an appointment
     * @param id
     * @param name
     * @param title
     * @param description
     * @param location
     * @param type
     * @param date
     * @param startTime
     * @param endTime
     * @param contact
     * @throws SQLException
     */
    public static void addAppointment(int id, String name, String title, String description, String location, String type, String date, String startTime, String endTime,String contact, String UserName) throws SQLException {

        LocalDateTime localStart = stringToLDT_UTC(startTime, date);
        LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
        String UTCstart = localStart.toString();
        String UTCend = localEnd.toString();

        ResultSet getContact_ID = connection.createStatement().executeQuery(String.format("SELECT Contact_ID FROM contacts WHERE Contact_Name = '%s'",  contact));
        getContact_ID.next();

        ResultSet getUserID = connection.createStatement().executeQuery(String.format("SELECT User_ID FROM users WHERE User_Name = '%s'", UserName));
        getUserID.next();

        System.out.println("Converted date and start time (UTC): " + UTCstart);

        connection.createStatement().executeUpdate(String.format("INSERT INTO appointments "
                        + "(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                 title, description, location, type, UTCstart, UTCend, LocalDateTime.now(), User.getCurrentUsername(), LocalDateTime.now(), User.getCurrentUsername(), id, getUserID.getInt("User_ID"), getContact_ID.getInt("Contact_ID")));
    }

    /**
     * This allows you to delete an appt
     * @param id
     */
    public static void deleteAppointment(int id){
        try {
            connection.createStatement().executeUpdate(String.format("DELETE FROM appointments WHERE Appointment_ID='%s'", id));

        } catch (Exception e) {
            System.out.println("Error deleting appointment: " + e.getMessage());
        }
    }

    /**
     * This allows you to update an appts details
     * @param apptId
     * @param title
     * @param description
     * @param type
     * @param location
     * @param date
     * @param startTime
     * @param endTime
     */
    public static void editAppointment(String apptId, String title, String description,String contact, String type, String location, String date, String startTime, String endTime, String UserName) {
        try {
            LocalDateTime localStart = stringToLDT_UTC(startTime, date);
            LocalDateTime localEnd = stringToLDT_UTC(endTime, date);
            String UTCstart = localStart.toString();
            String UTCend = localEnd.toString();

            System.out.println("Converted date and start time (UTC): " + UTCstart);

            ResultSet getContact_ID = connection.createStatement().executeQuery(String.format("SELECT Contact_ID FROM contacts WHERE Contact_Name = '%s'",  contact));
            getContact_ID.next();


            ResultSet getCustomerId = connection.createStatement().executeQuery(String.format("SELECT Customer_ID FROM appointments WHERE Appointment_ID = '%s'", apptId));
            getCustomerId.next();

            ResultSet getUserID = connection.createStatement().executeQuery(String.format("SELECT User_ID FROM users WHERE User_Name = '%s'", UserName));
            getUserID.next();

            connection.createStatement().executeUpdate(String.format("UPDATE appointments "
                            + "SET Customer_ID='%s', User_ID='%s', Title='%s', Contact_ID='%s',Description='%s', Location='%s', Type='%s', Start='%s', End='%s', Last_Update='%s', Last_Updated_By='%s' " +
                            "WHERE Appointment_ID='%s'",
                    getCustomerId.getString("Customer_ID"), getUserID.getInt("User_ID"), title, getContact_ID.getInt("Contact_ID"), description, location, type, UTCstart, UTCend, LocalDateTime.now(), User.getCurrentUsername(), apptId));
        } catch (SQLException e) {
            System.out.println("Error editing appointment: " + e.getMessage());
        }
    }
}


