package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Appointment {
    String name, title, description, location, contact, type, date, start, end, UserName;
    int apptID;

    public Appointment() {}

    public Appointment(int apptID, String name, String title, String description, String location, String contact, String type, String date, String start, String end, String UserName) {
        this.apptID = apptID;
        this.name = name;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
        this.UserName = UserName;
    }


    public Appointment(int apptID, String name, String title, String description, String location, String type, String date, String start, String end) {
        this.apptID = apptID;
        this.name = name;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public Appointment(int apptID, String name, String title, String description, String location, String type, String date, String start, String end, String UserName) {
        this.apptID = apptID;
        this.name = name;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
        this.UserName = UserName;
    }

    /**
     * Below are the getters and setters for the class
     * @return
     */
    public int getId() {
        return apptID;
    }

    public void setId(int apptID) {
        this.apptID = apptID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUserName(){
        return UserName;
    }

    public void setUserName(String UserName){
        this.UserName = UserName;
    }
}
