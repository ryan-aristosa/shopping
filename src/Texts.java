import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Texts {
    public void displayCategories() {
        System.out.println(
                """
                        Category:
                        \t1\s-\sPantry\ssupplies
                        \t2\s-\sMeat/Poultry/Seafood
                        \t3\s-\sSnacks"""
        );
    }

    public void displayItems(List<List<Item>> itemList, int selectedCategory) {
        String[] categoryList = {"Pantry", "Meat/Poultry/Seafood", "Snacks"};
        AtomicInteger itemKey = new AtomicInteger(0);

        System.out.println("\n%s Items:".formatted(categoryList[selectedCategory]));
        itemList.get(selectedCategory).forEach(
                item -> System.out.println(
                        "[%d]%-55s price %s/%s".formatted(
                                itemKey.getAndIncrement(), item.product(), item.price(), item.unit()
                        )
                )
        );
    }

    public void displayCartDetails(
            String product, double price, String unit, double quantity, double subTotal, double total, int numberOfItems
    ) {
        NumberFormat amountFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        amountFormat.setMinimumFractionDigits(3);

        String itemAdded = "\nItem added: %s | %s/%s x %s | %s"
                .formatted(product, String.format("%.2f", price), unit, quantity, subTotal);
        String cartContents = """
                %s
                Current cart contents:
                Total amount: %s
                Total amount compact: %s
                Number of Items: %s""";

        System.out.println(cartContents.formatted(
                itemAdded,
                String.format("%.2f", total),
                amountFormat.format(total),
                numberOfItems));
    }

    public void displayCartItems(List<Item> itemCart, List<Transaction> transactCart) {
        String itemFormat = "%s | %s/%s x %s | %s";

        System.out.println("\n");
        for (int i = 0; i < itemCart.size(); i++) {
            Item item = itemCart.get(i);
            Transaction transaction = transactCart.get(i);

            System.out.println(itemFormat.formatted(
                    item.product(),
                    String.format("%.2f", item.price()),
                    item.unit(),
                    transaction.getQuantity(),
                    String.format("%.2f", transaction.getSubTotal())
            ));
        }
    }

    public void displayPaymentMethods() {
        Map<Integer, String> methodMap = new HashMap<>();

        methodMap.put(1, "Savings");
        methodMap.put(2, "Checking");
        methodMap.put(3, "Credit Card");
        methodMap.put(4, "GCash");

        System.out.println("\nChoose Payment Option");
        for (Map.Entry<Integer, String> entry : methodMap.entrySet())
            System.out.println("[" + entry.getKey() + "] " + entry.getValue());
    }

    public String showPaymentDetails(int paymentMethod, double total, int numberOfItems) {
        NumberFormat amountFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        amountFormat.setMinimumFractionDigits(3);

        String accountDetails = "";
        String totalAmountDue = """
                Total amount due:
                Total amount: %s
                Total amount compact: %s
                Number of items: %s""";

        switch (paymentMethod) {
            case 1, 2 -> {
                SavingsChecking savingsChecking = new SavingsChecking();
                accountDetails = """
                        Account name: %s
                        Account number: %s
                        Bank name: %s
                        """.formatted(
                        savingsChecking.getName(), savingsChecking.getAccountNumber(), savingsChecking.getBankName()
                );
            }
            case 3 -> {
                CreditCard creditCard = new CreditCard();
                accountDetails = """
                        Name on card: %s
                        Credit card number: %s
                        Expiry date: %s
                        """.formatted(
                        creditCard.getName(), creditCard.getCreditCardNumber(), creditCard.getExpiryDate()
                );
            }
            case 4 -> {
                GCash gCash = new GCash();
                accountDetails = """
                        Subscriber name: %s
                        Mobile number: %s
                        """.formatted(
                        gCash.getName(), gCash.getMobileNumber()
                );
            }
        }

        return accountDetails.concat(totalAmountDue.formatted(
                String.format("%.2f", total), amountFormat.format(total), numberOfItems
        ));
    }
}