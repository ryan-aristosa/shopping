import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Helper {
    private static final List<Item> PANTRY = new ArrayList<>();
    private static final List<Item> MEAT = new ArrayList<>();
    private static final List<Item> SNACKS = new ArrayList<>();
    protected static List<List<Item>> itemList = new ArrayList<>();
    protected static List<Item> itemCart = new ArrayList<>();
    protected static List<Transaction> transactCart = new ArrayList<>();
    protected static int numberOfItems = 0;
    protected static double total = 0;
    private static final NumberFormat amountFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);


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
                            case "Pantry" -> PANTRY.add(item);
                            case "Meat/Poultry/Seafood" -> MEAT.add(item);
                            case "Snacks" -> SNACKS.add(item);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemList.add(PANTRY);
        itemList.add(MEAT);
        itemList.add(SNACKS);
    }

    public static void showCategories() {
        System.out.println(
                """
                        Category:
                        \t1\s-\sPantry\ssupplies
                        \t2\s-\sMeat/Poultry/Seafood
                        \t3\s-\sSnacks"""
        );
    }

    public static int validateSelectedCategory() {
        int selectedCategory;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("\nChoose Category(-1 to Checkout, -2 to Exit):");
            selectedCategory = scanner.nextInt();

            if ((selectedCategory < 1 || selectedCategory > 3) && selectedCategory != -1 && selectedCategory != -2)
                throw new InvalidInputException("Invalid input");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            selectedCategory = 0;
        } catch (InvalidInputException e) {
            System.out.println("Invalid input. Try again.");
            selectedCategory = 0;
        }

        return selectedCategory;
    }

    public static void showItemsInCategory(AtomicInteger itemKey, Item item) {
        System.out.println(
                "[%d]%-55s price %s/%s".formatted(
                        itemKey.getAndIncrement(), item.product(), item.price(), item.unit()
                )
        );
    }

    public static int validatedSelectedItem(int count) {
        boolean repeatFlag = true;
        int selectedItem = 0;
        Scanner scanner = new Scanner(System.in);

        while (repeatFlag) {
            try {
                System.out.print("\nChoose item (-1 to go back to Categories):");
                selectedItem = scanner.nextInt();

                if (selectedItem < -1 || selectedItem > count)
                    throw new InvalidInputException("Invalid input");
                else
                    repeatFlag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner = new Scanner(System.in);
            } catch (InvalidInputException e) {
                System.out.println("Invalid input. Try again.");
                repeatFlag = false;
                selectedItem = -1;
            }
        }

        return selectedItem;
    }

    public static double validateQuantity(String unit) {
        boolean repeatFlag = true;
        double quantity = 0;
        Scanner scanner = new Scanner(System.in);

        while (repeatFlag) {
            try {
                System.out.print("Enter how many:");
                quantity = scanner.nextDouble();

                if (
                        ((unit.equals("pc") || unit.equals("pack")) && (quantity <= 0 || quantity != Math.floor(quantity)))
                                || (unit.equals("kg") && quantity <= 0)
                ) {
                    throw new InvalidInputException("Invalid input");
                } else
                    repeatFlag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner = new Scanner(System.in);
            } catch (InvalidInputException e) {
                System.out.println("Invalid input. Try again.");
                scanner = new Scanner(System.in);
            }
        }

        return quantity;
    }

    public static void addToCart(int selectedCategory, int selectedItem, double quantity) {
        // Add to cart
        String product = itemList.get(selectedCategory).get(selectedItem).product();
        double price = itemList.get(selectedCategory).get(selectedItem).price();
        String unit = itemList.get(selectedCategory).get(selectedItem).unit();
        String category = itemList.get(selectedCategory).get(selectedItem).category();
        double subTotal = price * quantity;
        boolean isExisting = false;

        for (int i = 0; i < itemCart.size(); i++) {
            if (itemCart.get(i).product().equals(product)) {
                transactCart.get(i).setQuantity(transactCart.get(i).getQuantity() + quantity);
                transactCart.get(i).setSubTotal(transactCart.get(i).getSubTotal() + subTotal);
                isExisting = true;
            }
        }
        if (!isExisting) {
            itemCart.add(new Item(product, price, unit, category));
            transactCart.add(new Transaction(quantity, subTotal));
        }

        // Show cart details
        String itemAdded = "\nItem added: %s | %s/%s x %s | %s"
                .formatted(product, String.format("%.2f", price), unit, quantity, subTotal);
        total = transactCart.stream().mapToDouble(Transaction::getSubTotal).sum();
        amountFormat.setMinimumFractionDigits(3);
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
        showCartItems();
    }

    public static void showCartItems() {
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

    public static void checkout() {
        // Payment methods
        Map<Integer, String> methodMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        boolean repeatFlag = true;
        int paymentMethod = 0;

        methodMap.put(1, "Savings");
        methodMap.put(2, "Checking");
        methodMap.put(3, "Credit Card");
        methodMap.put(4, "GCash");

        System.out.println("\nChoose Payment Option");
        for (Map.Entry<Integer, String> entry : methodMap.entrySet())
            System.out.println("[" + entry.getKey() + "] " + entry.getValue());
        System.out.print("Enter Payment Option: ");

        while (repeatFlag) {
            try {
                paymentMethod = scanner.nextInt();

                if (paymentMethod < 1 || paymentMethod > 4)
                    throw new InvalidInputException("Invalid input");
                else
                    repeatFlag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner = new Scanner(System.in);
            } catch (InvalidInputException e) {
                System.out.println("Invalid input. Try again.");
                scanner = new Scanner(System.in);
            }
        }

        String[] first = {"Account name: David Beckham", "Name on card: David Beckham"};
        String[] second = {"Account number: 005412345678", "Credit card name: 4028123456789012"};
        String[] third = {"Bank name: BDO", "Expiry date: 12/2022"};
        String payment123 = """
                %s
                %s
                %s""";
        String gCashPayment = """
                Subscriber name: David Beckham
                Mobile number: 09171234567""";
        String paymentTextBlock = """
                %s
                Total amount due:
                Total amount: %s
                Total amount compact: %s
                Number of items: %s""";
        String paymentFirstPart, paymentDetails;

        if (paymentMethod == 1 || paymentMethod == 2 || paymentMethod == 3) {
            if (paymentMethod == 3)
                paymentFirstPart = payment123.formatted(first[1], second[1], third[1]);
            else
                paymentFirstPart = payment123.formatted(first[0], second[0], third[0]);
            paymentDetails = paymentTextBlock.formatted(
                    paymentFirstPart, total, amountFormat.format(total), numberOfItems
            );
        } else
            paymentDetails = paymentTextBlock.formatted(
                    gCashPayment, total, amountFormat.format(total), numberOfItems
            );
        System.out.println("\n" + paymentDetails);
        showCartItems();
        System.out.println();

        // Save file
        Path filepath = Paths.get("src//receipt.txt");
        try {
            Files.writeString(filepath, paymentDetails, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reset
        itemCart.clear();
        transactCart.clear();
        numberOfItems = 0;
    }
}
