// Model class
public class Item {
    private String product, unit, category;
    private double price;

    public Item(String item, double price, String unit, String category) {
        this.product = item;
        this.price = price;
        this.unit = unit;
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String item) {
        this.product = item;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Stocks{" +
                "product='" + product + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
