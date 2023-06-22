package com.smashedpotato.prisonarchitecttips;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by beavi on 13/11/2016.
 */

public class PrisonContent {

    public static final List<PrisonContent.PrisonItem> ITEMS = new ArrayList<PrisonContent.PrisonItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, PrisonContent.PrisonItem> ITEM_MAP = new HashMap<String, PrisonContent.PrisonItem>();

    private Context context;

    String[] categories;
    public PrisonContent(Context current,String packageName){
        if(ITEMS.size() <= 0) {
            this.context = current;
            Resources res = context.getResources();
            categories = res.getStringArray(R.array.Categories);

            for (int i = 0; i < categories.length; i++) {
                addItem(new PrisonContent.PrisonItem(categories[i], categories[i].replace("_", " "), res.getString(res.getIdentifier(categories[i], "string", packageName))));
            }
        }
    }

    private static void addItem(PrisonContent.PrisonItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class PrisonItem {
        public final String name;
        public final String content;
        public final String details;

        public PrisonItem(String name, String content, String details) {
            this.name = name;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
