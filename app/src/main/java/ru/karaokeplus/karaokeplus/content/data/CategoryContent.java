package ru.karaokeplus.karaokeplus.content.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CategoryContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<CategoryItem> ITEMS = new ArrayList<CategoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, CategoryItem> ITEM_MAP = new HashMap<String, CategoryItem>();

    private static final int COUNT = 25;

    public static void addItem(CategoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class CategoryItem {
        public final String id;
        public final String categoryName;
        public final String details;

        public CategoryItem(String id, String categoryName, String details) {
            this.id = id;
            this.categoryName = categoryName;
            this.details = details;
        }

        @Override
        public String toString() {
            return categoryName;
        }
    }
}
