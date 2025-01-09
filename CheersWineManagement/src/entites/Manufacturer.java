package entites;

public class Manufacturer {
    private int manufacturerID;
    private String name;
    private String phoneNumber;
    private String address;
    private String email;

    public Manufacturer(int manufacturerID, String name, String phoneNumber, String address, String email) {
        this.manufacturerID = manufacturerID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
    }

    // Getters and Setters
    public int getManufacturerID() { return manufacturerID; }
    public void setManufacturerID(int manufacturerID) { this.manufacturerID = manufacturerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
