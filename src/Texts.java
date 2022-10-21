import java.util.concurrent.atomic.AtomicInteger;

public class Texts {
    public String showCategories() {
        return """
                Category:
                \t1\s-\sPantry\ssupplies
                \t2\s-\sMeat/Poultry/Seafood
                \t3\s-\sSnacks""";
    }

    public String showItemsOnCategory(AtomicInteger itemKey, Item item) {
        return "[%d]%-55s price %s/%s".formatted(
                itemKey.getAndIncrement(), item.getProduct(), item.getPrice(), item.getUnit()
        );
    }
}
