public class Main extends Helper {
    public static void main(String[] args) {
        boolean categoryFlag = true;
        Texts texts = new Texts();

        // Read the available items from stocks.csv file
        readStocks();

        while (categoryFlag) {
            // Ask the user the grocery category, or to checkout or exit
            texts.displayCategories();
            System.out.print("\nChoose Category(-1 to Checkout, -2 to Exit):");
            int selectedCategory = validateSelectedCategory();

            switch (selectedCategory) {
                case -1 -> {
                    // Upon checkout, ask user the payment method
                    if (itemCart.isEmpty())
                        System.out.println("\nCart is empty, nothing to checkout.\n");
                    else
                        checkout();
                    continue;
                }
                case -2 -> {
                    System.out.println("Program Exiting now...");
                    categoryFlag = false;
                    continue;
                }
                case 0 -> { continue; }
                default -> selectedCategory--;
            }

            // Display the items under the chosen category
            texts.displayItems(itemList, selectedCategory);
            boolean itemFlag = true;

            while (itemFlag) {
                // Ask the user the item to add to cart
                System.out.print("\nChoose item (-1 to go back to Categories):");
                int selectedItem = validateInput("item", itemList.get(selectedCategory).size() - 1);
                if (selectedItem == -1) {
                    itemFlag = false;
                    continue;
                }

                String unit = itemList.get(selectedCategory).get(selectedItem).unit();
                System.out.print("Enter how many:");
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