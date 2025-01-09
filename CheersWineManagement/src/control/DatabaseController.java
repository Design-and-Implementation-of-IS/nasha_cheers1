package control;

import entites.Consts;
import entites.Wine;
import entites.Manufacturer;

import java.net.URLDecoder;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import boundary.CombinedManagementUI;

public class DatabaseController {

    protected static final String DB_FILEPATH = getDBPath();
    public static final String CONN_STR = "jdbc:ucanaccess://" + DB_FILEPATH + ";COLUMNORDER=DISPLAY";
    
    public ArrayList<Wine> getAllWines() {
        ArrayList<Wine> wines = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Wines")) {

            while (rs.next()) {
                Wine wine = new Wine(
                        rs.getInt("wineID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getDouble("PricePerBottle"),
                        rs.getString("SweetnessLevel"),
                        rs.getBytes("ProductImage"),
                        rs.getInt("ManufacturerID"),
                        rs.getInt("ProductionYear"),
                        rs.getString("TypeName"),
                        rs.getString("CatalogNumber")

                );
                wines.add(wine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wines;
    }

    public boolean addWine(Wine wine) {
        String query = "INSERT INTO Wines (Name, Description, PricePerBottle, SweetnessLevel, ProductImage, ManufacturerID, ProductionYear, TypeName, CatalogNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, wine.getName());
            pstmt.setString(2, wine.getDescription());
            pstmt.setDouble(3, wine.getPricePerBottle());
            pstmt.setString(4, wine.getSweetnessLevel());
            pstmt.setBytes(5, wine.getProductImage());
            pstmt.setInt(6, wine.getManufacturerID());
            pstmt.setInt(7, wine.getProductionYear());
            pstmt.setString(8, wine.getTypeName());
            pstmt.setString(9, wine.getCatalogNumber()); // Add this field
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteWine(int wineID) {
        String query = "DELETE FROM Wines WHERE wineID = ?";
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, wineID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWine(Wine wine) {
        String query = "UPDATE Wines SET Name = ?, Description = ?, PricePerBottle = ?, SweetnessLevel = ?, ProductImage = ?, ManufacturerID = ?, ProductionYear = ?, TypeName = ? WHERE wineID = ?";
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, wine.getName());
            pstmt.setString(2, wine.getDescription());
            pstmt.setDouble(3, wine.getPricePerBottle());
            pstmt.setString(4, wine.getSweetnessLevel());
            pstmt.setBytes(5, wine.getProductImage());
            pstmt.setInt(6, wine.getManufacturerID());
            pstmt.setInt(7, wine.getProductionYear());
            pstmt.setString(8, wine.getTypeName());
            pstmt.setInt(9, wine.getWineID());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
    }
    
    
    public void addListOfWieElements(NodeList elementsList) {
        for (int i = 0; i < elementsList.getLength(); i++) {
            Node node = elementsList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) { // Checks if the node is an element
                Element elem = (Element) node;

                // Retrieve the text content of child elements
                String name = elem.getElementsByTagName("Name").item(0).getTextContent();
                String description = elem.getElementsByTagName("Description").item(0).getTextContent();
                double pricePerBottle = Double.parseDouble(elem.getElementsByTagName("PricePerBottle").item(0).getTextContent());
                String sweetnessLevel = elem.getElementsByTagName("SweetnessLevel").item(0).getTextContent();
                byte[] productImage = null; // Handle image loading
                int manufacturerID = Integer.parseInt(elem.getElementsByTagName("ManufacturerID").item(0).getTextContent());
                int productionYear = Integer.parseInt(elem.getElementsByTagName("ProductionYear").item(0).getTextContent());
                String typeName = elem.getElementsByTagName("TypeName").item(0).getTextContent();
                String catalogNumber = elem.getElementsByTagName("CatalogNumber").item(0).getTextContent(); // Add this field

                Wine wineToInsert = new Wine(
                        -1, name, description, pricePerBottle, sweetnessLevel, productImage,
                        manufacturerID, productionYear, typeName, catalogNumber
                );

                this.addWine(wineToInsert);
            }
        }
    }

    
    public void addListOfManufacturerElements(NodeList elementsList) {
        
        for (int i = 0; i < elementsList.getLength(); i++) {
            Node node = elementsList.item(i);
            System.out.println("\nCurrent Node Tag: " + node.getNodeName()); // Prints the tag name of the current node

            
            if (node.getNodeType() == Node.ELEMENT_NODE) { // Checks if the node is an element
                Element elem = (Element) node;

                // Retrieve the text content of child elements
                int manufacturerID = Integer.parseInt(elem.getElementsByTagName("ManufacturerID").item(0).getTextContent());
                String name = elem.getElementsByTagName("Name").item(0).getTextContent();
                String phoneNumber = elem.getElementsByTagName("PhoneNumber").item(0).getTextContent();
                String address = elem.getElementsByTagName("Address").item(0).getTextContent();
                String email = elem.getElementsByTagName("Email").item(0).getTextContent();

                Manufacturer manufacturertToInsert = new Manufacturer(manufacturerID, name, phoneNumber, address, email);
                
                this.addManufacturer(manufacturertToInsert);
            }
        }
    }    
    private boolean addManufacturer(Manufacturer manufacturertToInsert) {
        if (manufacturertToInsert == null || manufacturertToInsert.getName() == null || manufacturertToInsert.getEmail() == null) {
            System.err.println("Invalid manufacturer data. Manufacturer object or required fields are null.");
            return false;
        }

        // Query to insert a new manufacturer
        String query = "INSERT INTO Manufacturers (ManufacturerID, Name, PhoneNumber, Address, Email) VALUES (?, ?, ?, ?, ?)";

        // Query to check if ManufacturerID already exists
        String checkQuery = "SELECT COUNT(*) FROM Manufacturers WHERE ManufacturerID = ?";

        try (Connection conn = DriverManager.getConnection(CONN_STR)) {

            // Check if the ManufacturerID already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, manufacturertToInsert.getManufacturerID());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, 
                        "Manufacturer ID [" + manufacturertToInsert.getManufacturerID() + "] already exists!", 
                        "Integrity Constraint Violation", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Insert the manufacturer
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, manufacturertToInsert.getManufacturerID());
                pstmt.setString(2, manufacturertToInsert.getName());
                pstmt.setString(3, manufacturertToInsert.getPhoneNumber());
                pstmt.setString(4, manufacturertToInsert.getAddress());
                pstmt.setString(5, manufacturertToInsert.getEmail());
                pstmt.executeUpdate();
                System.out.println("Manufacturer added successfully: " + manufacturertToInsert.getName());
                return true;
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, 
                "Manufacturer ID [" + manufacturertToInsert.getManufacturerID() + "] already exists!", 
                "Integrity Constraint Violation", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            System.err.println("Error inserting manufacturer into database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


	private static String getDBPath() {
    	try {
    	String path = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    	String decoded = URLDecoder.decode(path, "UTF-8");
    	if (decoded.contains(".jar")) {
    	decoded = decoded.substring(0, decoded.lastIndexOf('/'));
    	return decoded + "/database/MyCheers1.accdb";
    	} else {
    	decoded = decoded.substring(0, decoded.lastIndexOf("bin/"));
    	return decoded + "src/entites/MyCheers1.accdb";
    	}
    	} catch (Exception e) {
    	e.printStackTrace();
    	 return null;
    	}
    }

	
	public ArrayList<Wine> getWinesByManufacturer(int manufacturerID) {
	    ArrayList<Wine> wines = new ArrayList<>();

	    String query = "SELECT * FROM Wines WHERE ManufacturerID = ?";

	    try (Connection conn = DriverManager.getConnection(CombinedManagementUI.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, manufacturerID);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            Wine wine = new Wine(
	                rs.getInt("WineID"),
	                rs.getString("Name"),
	                rs.getString("Description"),
	                rs.getDouble("PricePerBottle"),
	                rs.getString("SweetnessLevel"),
	                null, // Assuming no product image is used for now
	                rs.getInt("ManufacturerID"),
	                rs.getInt("ProductionYear"),
	                rs.getString("TypeName"),
	                rs.getString("CatalogNumber")
	            );
	            wines.add(wine);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return wines;
	}

    
}
