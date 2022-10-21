import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Helper {
    public static void main(String[] args) {
        String[] categoryList = {"Pantry", "Meat/Poultry/Seafood", "Snacks"};
        AtomicInteger itemKey = new AtomicInteger(0);
        boolean categoryFlag = true;

        readStocks();

        while (categoryFlag) {
            // Show categories
            showCategories();
            int selectedCategory = validateSelectedCategory();

            switch (selectedCategory) {
                case -1 -> {
                    categoryFlag = false;
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

                String unit = itemList.get(selectedCategory).get(selectedItem).getUnit();
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