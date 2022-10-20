public class Transaction {
    private double quantity, subTotal;

    public Transaction(double quantity, double subTotal) {
        this.quantity = quantity;
        this.subTotal = subTotal;
    }
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "quantity=" + quantity +
                ", subTotal=" + subTotal +
                '}';
    }
}
