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

    public static final List<CategoryItem> ITEMS = new ArrayList<CategoryItem>();
    public static final Map<Integer, CategoryItem> ITEM_MAP = new HashMap<Integer, CategoryItem>();

    public static void addItem(CategoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class CategoryItem {
        public final int id;
        public final String categoryName;
        public final String details;

        public CategoryItem(int id, String categoryName, String details) {
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
