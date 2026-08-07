package com.witchcraft.book;

import java.util.List;

/**
 * Holds the content for a single spell book (written book item).
 */
public class SpellBookData {

    private final String spellId;
    private final String title;
    private final String category;
    private final List<String> pages;
    private final int price; // emerald price for villager trades

    public SpellBookData(String spellId, String title, String category, List<String> pages, int price) {
        this.spellId = spellId;
        this.title = title;
        this.category = category;
        this.pages = pages;
        this.price = price;
    }

    public String getSpellId() { return spellId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public List<String> getPages() { return pages; }
    public int getPrice() { return price; }
}
