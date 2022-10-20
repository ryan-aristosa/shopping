import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Path filePath = Paths.get(System.getProperty("user.dir") + "\\src\\stocks.csv");
        ArrayList<Item> pantry = new ArrayList<>();
        ArrayList<Item> meat = new ArrayList<>();
        ArrayList<Item> snacks = new ArrayList<>();
        ArrayList<ArrayList<Item>> itemList = new ArrayList<>();

        try {
            Files.readString(filePath)
                    .lines()
                    .filter(Predicate.not(String::isBlank))
                    .forEach(line -> {
                        String[] arr = line.split(",");
                        switch (arr[3]) {
                            case "Pantry" -> pantry.add(
                                    new Item(arr[0], Double.parseDouble(arr[1]), arr[2], arr[3])
                            );
                            case "Meat/Poultry/Seafood" -> meat.add(
                                    new Item(arr[0], Double.parseDouble(arr[1]), arr[2], arr[3])
                            );
                            case "Snacks" -> snacks.add(
                                    new Item(arr[0], Double.parseDouble(arr[1]), arr[2], arr[3])
                            );
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemList.add(pantry);
        itemList.add(meat);
        itemList.add(snacks);

        // -->
        Scanner scanner = new Scanner(System.in);
//        String[] categoryList = {"Pantry", "Meat/Poultry/Seafood", "Snacks"};
        String itemsInCategory = "[%d]%-55s price %s/%s";
        int[] itemKey = {0};
        ArrayList<Item> itemCart = new ArrayList<>();
        ArrayList<Transaction> transactCart = new ArrayList<>();
        String itemAdded = "Item added: %s | %s/%s x %s | %s";

        System.out.print("Choose Category(-1 to Checkout, -2 to Exit):");
        int selectedCategory = scanner.nextInt() - 1;

        itemList.get(selectedCategory).forEach(item ->
                System.out.println(
                        itemsInCategory.formatted(itemKey[0]++, item.getProduct(), item.getPrice(), item.getUnit())
                )
        );

        System.out.print("Choose item (-1 to go back to Categories):");
        int selectedItem = scanner.nextInt();
        System.out.print("Enter how many:");
        double quantity = scanner.nextDouble();

        String item = itemList.get(selectedCategory).get(selectedItem).getProduct();
        double price = itemList.get(selectedCategory).get(selectedItem).getPrice();
        String unit = itemList.get(selectedCategory).get(selectedItem).getUnit();
        String category = itemList.get(selectedCategory).get(selectedItem).getCategory();
        double subTotal = price * quantity;

        itemCart.add(new Item(item, price, unit, category));
        transactCart.add(new Transaction(quantity, subTotal));

        System.out.println(itemAdded.formatted(item, price, unit, quantity, subTotal));
    }
}