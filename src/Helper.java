import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helper {
    private static final List<Item> pantry = new ArrayList<>();
    private static final List<Item> meat = new ArrayList<>();
    private static final List<Item> snacks = new ArrayList<>();
    protected static List<List<Item>> itemList = new ArrayList<>();
    protected static List<Item> itemCart = new ArrayList<>();
    protected static List<Transaction> transactCart = new ArrayList<>();

    public static void readStocks() {
        Path stocksPath = Paths.get("src//stocks.csv");
        try {
            Files.readString(stocksPath)
                    .lines()
                    .filter(Predicate.not(String::isBlank))
                    .forEach(line -> {
                        String[] tempArray = line.split(",");
                        Item item = new Item(tempArray[0], Double.parseDouble(tempArray[1]), tempArray[2], tempArray[3]);

                        switch (tempArray[3]) {
                            case "Pantry" -> pantry.add(item);
                            case "Meat/Poultry/Seafood" -> meat.add(item);
                            case "Snacks" -> snacks.add(item);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemList.add(pantry);
        itemList.add(meat);
        itemList.add(snacks);
    }

    public static void addToCart(int selectedCategory, int selectedItem, double quantity) {
        // Add to cart
        String product = itemList.get(selectedCategory).get(selectedItem).getProduct();
        double price = itemList.get(selectedCategory).get(selectedItem).getPrice();
        String unit = itemList.get(selectedCategory).get(selectedItem).getUnit();
        String category = itemList.get(selectedCategory).get(selectedItem).getCategory();
        double subTotal = price * quantity;

        itemCart.add(new Item(product, price, unit, category));
        transactCart.add(new Transaction(quantity, subTotal));

        // Show cart details
        String itemAdded = "\nItem added: %s | %s/%s x %s | %s"
                .formatted(product, String.format("%.2f", price), unit, quantity, subTotal);
        List<Object> dataStream = transactCart.stream().collect(
                Collectors.teeing(
                        Collectors.summingDouble(Transaction::getSubTotal),
                        Collectors.counting(),
                        List::of
                )
        );
        NumberFormat amountFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        amountFormat.setMinimumFractionDigits(0);
        String cartContents = """
                %s
                Current cart contents:
                Total amount: %s
                Total amount compact: %s
                Number of Items: %s""";

        System.out.println(cartContents.formatted(
                itemAdded,
                String.format("%.2f", (Double) dataStream.get(0)),
                amountFormat.format(dataStream.get(0)),
                dataStream.get(1)));

        // Show items in cart
        String itemFormat = "%s | %s/%s x %s | %s";

        for (int i = 0; i < itemCart.size(); i++) {
            Item item = itemCart.get(i);
            Transaction transaction = transactCart.get(i);

            System.out.println("\n\n".concat(
                    itemFormat.formatted(
                            item.getProduct(),
                            item.getPrice(),
                            item.getUnit(),
                            transaction.getQuantity(),
                            transaction.getSubTotal()
                    )
            ));
        }
    }
}
