package com.sample.moneymanager;

public class SpentMoneyItem {

    private String text;
    private boolean header; // 0 = default item; 1 = header
    private int value;
    private String category;

    public SpentMoneyItem(String text, boolean header, int value) {
        this.text = text;
        this.header = header;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public boolean isHeader() {
        return header;
    }

    @Override
    public String toString() {
        String str = "";
        str += getText() + ",";
        str += getValue()+ ",";
        str += getCategory();
        return str;
    }
}
