package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import control.DatabaseController;
import entites.Consts;
import entites.Wine;

import java.awt.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class CombinedManagementUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel wineTableModel;
    private DefaultTableModel manufacturerTableModel;	
    private DatabaseController dbController;

   
    public CombinedManagementUI() {
        setTitle("Combined Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        dbController = new DatabaseController();

        // Main Layout
        getContentPane().setLayout(new BorderLayout());

        // Tabs for Wine and Manufacturer Management
        JTabbedPane tabbedPane = new JTabbedPane();

        // Wine Management Panel
        JPanel winePanel = createWineManagementPanel();
        tabbedPane.addTab("Wine Management", winePanel);

        // Manufacturer Management Panel
        JPanel manufacturerPanel = createManufacturerManagementPanel();
        tabbedPane.addTab("Manufacturer Management", manufacturerPanel);

        // Add Tabs to Frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
    }

    private JPanel createWineManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for displaying wines
        wineTableModel = new DefaultTableModel(new Object[]{"Wine ID", "Name", "Description", "Price", "Sweetness", "Year", "Type","manufacturerID","CatalogNumber"}, 0);
        JTable wineTable = new JTable(wineTableModel);
        JScrollPane tableScrollPane = new JScrollPane(wineTable);

        // Buttons
        JButton addButton = new JButton("Add Wine");
        JButton removeButton = new JButton("Remove Wine");
        JButton updateButton = new JButton("Update Wine");
        JButton viewAllButton = new JButton("View All Wines");
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewAllButton);
        
        // Add Components to Panel
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton ImportXML = new JButton("Import XML data");
        ImportXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser();

                // Set the file filter to only allow XML files
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML Files", "xml"));

                // Show the open dialog and check the user's choice
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file
                    java.io.File selectedFile = fileChooser.getSelectedFile();
                    
                    // Process the file (e.g., read and parse the XML)
                    try {
                        // Example: Print the selected file's absolute path
                        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
//                        Document doc = DocumentBuilderFactory.newInstance()
//                         .newDocumentBuilder()
//                        .parse(selectedFile);
//                        doc.getDocumentElement().normalize();

                        
                        Document doc = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder()
                                .parse(selectedFile);

                        doc.getDocumentElement().normalize();
                        NodeList nList = doc.getElementsByTagName("Wine");
                        dbController.addListOfWieElements(nList);
                        NodeList manufacturersList = doc.getElementsByTagName("Manufacturer");
                        dbController.addListOfManufacturerElements(manufacturersList);


                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error processing the XML file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                }
            }
        });
        buttonPanel.add(ImportXML);

        // Button Actions
        addButton.addActionListener(e -> showAddWineDialog());
        removeButton.addActionListener(e -> removeSelectedWine(wineTable));
        updateButton.addActionListener(e -> showUpdateWineDialog(wineTable));
        viewAllButton.addActionListener(e -> loadWines());
        
        // Load Initial Data
        loadWines();

        return panel;
    }
    
        
    private JPanel createManufacturerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for displaying manufacturers
        manufacturerTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Address", "Email"}, 0);
        JTable manufacturerTable = new JTable(manufacturerTableModel);
        JScrollPane tableScrollPane = new JScrollPane(manufacturerTable);

        // Buttons
        JButton addButton = new JButton("Add Manufacturer");
        JButton removeButton = new JButton("Remove Manufacturer");
        JButton updateButton = new JButton("Update Manufacturer");
        JButton viewAllButton = new JButton("View All Manufacturers");
        JButton viewWinesButton = new JButton("View Manufacturers Wines");

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(viewWinesButton); // Add the "View Wines" button

        // Add Components to Panel
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> showAddManufacturerDialog());
        removeButton.addActionListener(e -> removeSelectedManufacturer(manufacturerTable));
        updateButton.addActionListener(e -> showUpdateManufacturerDialog(manufacturerTable));
        viewAllButton.addActionListener(e -> loadManufacturers());
        viewWinesButton.addActionListener(e -> {
            int selectedRow = manufacturerTable.getSelectedRow();
            if (selectedRow >= 0) {
                int manufacturerID = (int) manufacturerTableModel.getValueAt(selectedRow, 0);
                displayWinesForManufacturer(manufacturerID);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a manufacturer to view their wines.");
            }
        });

        // Load Initial Data
        loadManufacturers();

        return panel;
    }
    private void displayWinesForManufacturer(int manufacturerID) {
        // Create a new JFrame to display the wines
        JFrame winesFrame = new JFrame("Wines by Manufacturer");
        winesFrame.setSize(800, 600);
        winesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        winesFrame.setLocationRelativeTo(null);

        // Create a table model and table for displaying wines
        DefaultTableModel winesTableModel = new DefaultTableModel(new Object[]{
            "Wine ID", "Name", "Description", "Price", "Sweetness", "Year", "Type", "Catalog Number"
        }, 0);
        JTable winesTable = new JTable(winesTableModel);

        // Fetch wines for the selected manufacturer
        ArrayList<Wine> wines = dbController.getWinesByManufacturer(manufacturerID);

        // Populate the table with wine data
        for (Wine wine : wines) {
            winesTableModel.addRow(new Object[]{
                wine.getWineID(),
                wine.getName(),
                wine.getDescription(),
                wine.getPricePerBottle(),
                wine.getSweetnessLevel(),
                wine.getProductionYear(),
                wine.getTypeName(),
                wine.getCatalogNumber()
            });
        }

        // Add the table to the frame
        JScrollPane scrollPane = new JScrollPane(winesTable);
        winesFrame.add(scrollPane);

        // Show the frame
        winesFrame.setVisible(true);
    }



    private void loadWines() {
        wineTableModel.setRowCount(0); // Clear the table

        ArrayList<Wine> wines = dbController.getAllWines();

        for (Wine wine : wines) {
            wineTableModel.addRow(new Object[]{
                wine.getWineID(),
                wine.getName(),
                wine.getDescription(),
                wine.getPricePerBottle(),
                wine.getSweetnessLevel(),
                wine.getProductionYear(),
                wine.getTypeName(),
                wine.getManufacturerID(),
                wine.getCatalogNumber() // Add Catalog Number to the table
            });
        }
    }


        
//        try (Connection conn = DriverManager.getConnection(CONN_STR);
//             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Wines");
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                wineTableModel.addRow(new Object[]{
//                        rs.getInt("WineID"),
//                        rs.getString("Name"),
//                        rs.getString("Description"),
//                        rs.getDouble("PricePerBottle"),
//                        rs.getString("SweetnessLevel"),
//                        rs.getInt("ProductionYear"),
//                        rs.getString("TypeName")
//                });
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading wines: " + e.getMessage());
//        }
    

    private void loadManufacturers() {
        manufacturerTableModel.setRowCount(0); // Clear the table
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Manufacturers");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                manufacturerTableModel.addRow(new Object[]{
                        rs.getInt("ManufacturerID"),
                        rs.getString("Name"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getString("Email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading manufacturers: " + e.getMessage());
        }
    }
    private void showAddWineDialog() {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField sweetnessField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField catalogNumberField = new JTextField(); // New field for Catalog Number

        // Create a dropdown for selecting the manufacturer
        JComboBox<String> manufacturerDropdown = new JComboBox<>();
        ArrayList<Integer> manufacturerIDs = new ArrayList<>();

        // Load manufacturers into the dropdown
        try (Connection conn = DriverManager.getConnection(CONN_STR);
             PreparedStatement stmt = conn.prepareStatement("SELECT ManufacturerID, Name FROM Manufacturers");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ManufacturerID");
                String name = rs.getString("Name");

                manufacturerDropdown.addItem(name);
                manufacturerIDs.add(id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading manufacturers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Sweetness:"));
        panel.add(sweetnessField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Catalog Number:"));
        panel.add(catalogNumberField); // Add field for Catalog Number
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Wine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int selectedManufacturerIndex = manufacturerDropdown.getSelectedIndex();
                int selectedManufacturerID = manufacturerIDs.get(selectedManufacturerIndex);

                Wine wineToInsert = new Wine(
                    -1,
                    nameField.getText(),
                    descriptionField.getText(),
                    Double.parseDouble(priceField.getText()),
                    sweetnessField.getText(),
                    null,
                    selectedManufacturerID,
                    Integer.parseInt(yearField.getText()),
                    typeField.getText(),
                    catalogNumberField.getText() // Set Catalog Number
                );

                dbController.addWine(wineToInsert);
                JOptionPane.showMessageDialog(this, "Wine added successfully!");
                loadWines();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for numeric fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding wine: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void removeSelectedWine(JTable wineTable) {
        int selectedRow = wineTable.getSelectedRow();
        if (selectedRow >= 0) {
            int wineID = (int) wineTableModel.getValueAt(selectedRow, 0);
            try (Connection conn = DriverManager.getConnection(CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Wines WHERE WineID = ?")) {

                stmt.setInt(1, wineID);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Wine removed successfully!");
                loadWines();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error removing wine: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a wine to remove.");
        }
    }

    private void showUpdateWineDialog(JTable wineTable) {
        int selectedRow = wineTable.getSelectedRow();
        if (selectedRow >= 0) {
            int wineID = (int) wineTableModel.getValueAt(selectedRow, 0);

            JTextField nameField = new JTextField((String) wineTableModel.getValueAt(selectedRow, 1));
            JTextField descriptionField = new JTextField((String) wineTableModel.getValueAt(selectedRow, 2));
            JTextField priceField = new JTextField(String.valueOf(wineTableModel.getValueAt(selectedRow, 3)));
            JTextField sweetnessField = new JTextField(String.valueOf(wineTableModel.getValueAt(selectedRow, 4)));
            JTextField yearField = new JTextField(String.valueOf(wineTableModel.getValueAt(selectedRow, 5)));
            JTextField typeField = new JTextField((String) wineTableModel.getValueAt(selectedRow, 6));

            JPanel panel = new JPanel(new GridLayout(6, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Sweetness:"));
            panel.add(sweetnessField);
            panel.add(new JLabel("Year:"));
            panel.add(yearField);
            panel.add(new JLabel("Type:"));
            panel.add(typeField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Wine", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection(CONN_STR);
                     PreparedStatement stmt = conn.prepareStatement("UPDATE Wines SET Name = ?, Description = ?, PricePerBottle = ?, SweetnessLevel = ?, ProductionYear = ?, TypeName = ? WHERE WineID = ?")) {

                    stmt.setString(1, nameField.getText());
                    stmt.setString(2, descriptionField.getText());
                    stmt.setDouble(3, Double.parseDouble(priceField.getText()));
                    stmt.setString(4,  descriptionField.getText());
                    stmt.setInt(5, Integer.parseInt(yearField.getText()));
                    stmt.setString(6, typeField.getText());
                    stmt.setInt(7, wineID);

                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Wine updated successfully!");
                    loadWines();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating wine: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a wine to update.");
        }
    }

    private void showAddManufacturerDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField emailField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Manufacturer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Validate input
            if (nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO Manufacturers (Name, PhoneNumber, Address, Email) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, nameField.getText());
                stmt.setString(2, phoneField.getText());
                stmt.setString(3, addressField.getText());
                stmt.setString(4, emailField.getText());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Manufacturer added successfully!");
                loadManufacturers(); // Refresh the list or table displaying manufacturers
            } catch (SQLIntegrityConstraintViolationException e) {
                JOptionPane.showMessageDialog(this, "Manufacturer with this name or email already exists!", "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding manufacturer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void removeSelectedManufacturer(JTable manufacturerTable) {
        int selectedRow = manufacturerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int manufacturerID = (int) manufacturerTableModel.getValueAt(selectedRow, 0);
            try (Connection conn = DriverManager.getConnection(CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Manufacturers WHERE ManufacturerID = ?")) {

                stmt.setInt(1, manufacturerID);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Manufacturer removed successfully!");
                loadManufacturers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error removing manufacturer: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a manufacturer to remove.");
        }
    }
    public ArrayList<Wine> getWinesByManufacturer(int manufacturerID) {
        ArrayList<Wine> wines = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(CombinedManagementUI.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Wines WHERE ManufacturerID = ?")) {

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
    
    private void showUpdateManufacturerDialog(JTable manufacturerTable) {
        int selectedRow = manufacturerTable.getSelectedRow();
        if (selectedRow >= 0) {
            int manufacturerID = (int) manufacturerTableModel.getValueAt(selectedRow, 0);

            JTextField nameField = new JTextField((String) manufacturerTableModel.getValueAt(selectedRow, 1));
            JTextField phoneField = new JTextField((String) manufacturerTableModel.getValueAt(selectedRow, 2));
            JTextField addressField = new JTextField((String) manufacturerTableModel.getValueAt(selectedRow, 3));
            JTextField emailField = new JTextField((String) manufacturerTableModel.getValueAt(selectedRow, 4));

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Manufacturer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || 
                    addressField.getText().isEmpty() || emailField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(CONN_STR);
                     PreparedStatement stmt = conn.prepareStatement("UPDATE Manufacturers SET Name = ?, PhoneNumber = ?, Address = ?, Email = ? WHERE ManufacturerID = ?")) {

                    stmt.setString(1, nameField.getText());
                    stmt.setString(2, phoneField.getText());
                    stmt.setString(3, addressField.getText());
                    stmt.setString(4, emailField.getText());
                    stmt.setInt(5, manufacturerID);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Manufacturer updated successfully!");
                        loadManufacturers();
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed. Manufacturer not found.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating manufacturer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a manufacturer to update.");
        }
    }

    protected static final String DB_FILEPATH = getDBPath();
    public static final String CONN_STR = "jdbc:ucanaccess://" + DB_FILEPATH + ";COLUMNORDER=DISPLAY";
   
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CombinedManagementUI ui = new CombinedManagementUI();
            ui.setVisible(true);
        });
    }
}
