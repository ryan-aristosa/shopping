import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;

public class Helper {
    protected static List<List<Item>> itemList = new ArrayList<>();
    protected static List<Item> itemCart = new ArrayList<>();
    protected static List<Transaction> transactCart = new ArrayList<>();
    protected static double total = 0;
    protected static int numberOfItems = 0;
    protected static final Texts TEXTS = new Texts();

    public static void readStocks() {
        Path stocksPath = Paths.get("src//stocks.csv");
        List<Item> pantry = new ArrayList<>();
        List<Item> meat = new ArrayList<>();
        List<Item> snacks = new ArrayList<>();

        try {
            Files.readString(stocksPath)
                    .lines()
                    .filter(Predicate.not(String::isBlank))
                    .forEach(line -> {
                        String[] tempArray = line.split(",");
                        Item item = new Item(
                                tempArray[0], Double.parseDouble(tempArray[1]), tempArray[2], tempArray[3]
                        );
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

    public static int validateSelectedCategory() {
        int selectedCategory;
        Scanner scanner = new Scanner(System.in);

        try {
            selectedCategory = scanner.nextInt();

            if ((selectedCategory < 1 || selectedCategory > 3) && selectedCategory != -1 && selectedCategory != -2)
                throw new InvalidInputException("Invalid input. Try again.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            selectedCategory = 0;
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            selectedCategory = 0;
        }

        return selectedCategory;
    }

    public static int validateInput(String usedFor, int upperLimit) { // item and payment
        boolean repeatFlag = true;
        int input = 0;
        Scanner scanner = new Scanner(System.in);

        while (repeatFlag) {
            try {
                input = scanner.nextInt();
                boolean isItemNotValid = usedFor.equals("item") && input < -1 || input > upperLimit;
                boolean isPaymentNotValid = usedFor.equals("payment") && (input < 1 || input > upperLimit);

                if (isItemNotValid || isPaymentNotValid)
                    throw new InvalidInputException("Invalid input. Try again.");
                else
                    repeatFlag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner = new Scanner(System.in);
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
                scanner = new Scanner(System.in);
            }
        }

        return input;
    }

    public static double validateQuantity(String unit) {
        boolean repeatFlag = true;
        double quantity = 0;
        Scanner scanner = new Scanner(System.in);

        while (repeatFlag) {
            try {
                quantity = scanner.nextDouble();

                if (
                        ((unit.equals("pc") || unit.equals("pack"))
                                && (quantity <= 0 || quantity != Math.floor(quantity))
                        ) || (unit.equals("kg") && quantity <= 0)
                )
                    throw new InvalidInputException("Invalid input. Try again.");
                else
                    repeatFlag = false;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner = new Scanner(System.in);
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
                scanner = new Scanner(System.in);
            }
        }

        return quantity;
    }

    public static void addToCart(int selectedCategory, int selectedItem, double quantity) {
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

        total = transactCart.stream().mapToDouble(Transaction::getSubTotal).sum();

        // Display the added item, total amount and the current content of the cart
        TEXTS.displayCartDetails(product, price, unit, quantity, subTotal, total, numberOfItems);
        TEXTS.displayCartItems(itemCart, transactCart);
    }

    public static void checkout() {
        TEXTS.displayPaymentMethods();
        System.out.print("Enter Payment Option: ");
        int paymentMethod = validateInput("payment", 4);

        // Display payment details
        String paymentDetails = TEXTS.showPaymentDetails(paymentMethod, total, numberOfItems);
        System.out.println("\n".concat(paymentDetails));

        TEXTS.displayCartItems(itemCart, transactCart);
        System.out.println();

        // Save payment details to a file named receipt.txt
        Path filepath = Paths.get("src//receipt.txt");
        try {
            Files.newBufferedWriter(filepath, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(filepath, paymentDetails, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemCart.clear();
        transactCart.clear();
        total = 0;
        numberOfItems = 0;
    }
}