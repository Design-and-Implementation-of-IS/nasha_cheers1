package entites;

public class Wine {
    private int wineID;
    private String name;
    private String description;
    private double pricePerBottle;
    private String sweetnessLevel;
    private byte[] productImage; // For attachments like images
    private int manufacturerID;
    private int productionYear;
    private String typeName;
    private String catalogNumber;

   

    public Wine(int wineID, String name, String description, double pricePerBottle, String sweetnessLevel,
			byte[] productImage, int manufacturerID, int productionYear, String typeName, String catalogNumber) {
		super();
		this.wineID = wineID;
		this.name = name;
		this.description = description;
		this.pricePerBottle = pricePerBottle;
		this.sweetnessLevel = sweetnessLevel;
		this.productImage = productImage;
		this.manufacturerID = manufacturerID;
		this.productionYear = productionYear;
		this.typeName = typeName;
		this.catalogNumber = catalogNumber;
	}
	public String getCatalogNumber() {
		return catalogNumber;
	}
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	// Getters and Setters
    public int getWineID() { return wineID; }
    public void setWineID(int wineID) { this.wineID = wineID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerBottle() { return pricePerBottle; }
    public void setPricePerBottle(double pricePerBottle) { this.pricePerBottle = pricePerBottle; }

    public String getSweetnessLevel() { return sweetnessLevel; }
    public void setSweetnessLevel(String sweetnessLevel) { this.sweetnessLevel = sweetnessLevel; }

    public byte[] getProductImage() { return productImage; }
    public void setProductImage(byte[] productImage) { this.productImage = productImage; }

    public int getManufacturerID() { return manufacturerID; }
    public void setManufacturerID(int manufacturerID) { this.manufacturerID = manufacturerID; }

    public int getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
