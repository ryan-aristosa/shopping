import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Helper {
    public static void main(String[] args) {
        boolean categoryFlag = true;
        String[] categoryList = {"Pantry", "Meat/Poultry/Seafood", "Snacks"};
        AtomicInteger itemKey = new AtomicInteger(0);

        readStocks();

        while (categoryFlag) {
            // Show categories
            showCategories();
            int selectedCategory = validateSelectedCategory();

            switch (selectedCategory) {
                case -1 -> {
                    if (itemCart.isEmpty())
                        System.out.println("\nCart is empty, nothing to checkout.\n");
                    else
                        checkout();
                    continue;
                }
                case -2 -> {
                    categoryFlag = false;
                    continue;
                }
                case 0 -> { continue; }
                default ->  selectedCategory--;
            }
            List<Item> itemsInCategory = itemList.get(selectedCategory);

            System.out.println("\n%s Items:".formatted(categoryList[selectedCategory]));
            itemsInCategory.forEach(item -> showItemsInCategory(itemKey, item));
            itemKey.set(0);

            // Show items on category
            boolean itemFlag = true;

            while (itemFlag) {
                int selectedItem = validatedSelectedItem(itemsInCategory.size() - 1);
                if (selectedItem == -1) {
                    itemFlag = false;
                    continue;
                }

                String unit = itemList.get(selectedCategory).get(selectedItem).unit();
                double quantity = validateQuantity(unit);
                if (unit.equals("kg"))
                    numberOfItems++;
                else
                    numberOfItems += quantity;

                addToCart(selectedCategory, selectedItem, quantity);
            }
        }
    }
}