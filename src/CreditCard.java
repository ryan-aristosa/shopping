public class CreditCard extends Account {
    private final String creditCardNumber = "4028123456789012";
    private final String expiryDate = "12/2022";

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}