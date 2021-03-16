package Model;

public class Customer {

    String name, address, zip, country, phone, division;
    int id;
    static int divisionID;

    public Customer() {}

    public Customer(int id, String name, String address, String division, String zip, String country, String phone, int divisionID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.division = division;
        this.zip = zip;
        this.country = country;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Getters and setters for the class
     * @return
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public static int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(String state) {
        this.divisionID = divisionID;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}