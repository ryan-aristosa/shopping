import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Helper {
    public static void main(String[] args) {
        Texts texts = new Texts();
        Scanner scanner = new Scanner(System.in);
        String[] categoryList = {"Pantry", "Meat/Poultry/Seafood", "Snacks"};
        AtomicInteger itemKey = new AtomicInteger(0);

        readStocks();

        System.out.println(texts.showCategories());
        System.out.print("Choose Category(-1 to Checkout, -2 to Exit):");
        int selectedCategory = scanner.nextInt() - 1;

        System.out.println("\n%s Items:".formatted(categoryList[selectedCategory]));
        itemList.get(selectedCategory).forEach(item -> System.out.println(texts.showItemsOnCategory(itemKey, item)));

        // -->

        System.out.print("\nChoose item (-1 to go back to Categories):");
        int selectedItem = scanner.nextInt();
        System.out.print("Enter how many:");
        double quantity = scanner.nextDouble();

        addToCart(selectedCategory, selectedItem, quantity);
    }
}