package com.example.marketplace.model;

public enum ProductCondition {

    NEW_WITH_TAGS("New with tags"),
    NEW_WITHOUT_TAGS("New without tags"),
    LIKE_NEW("Like new"),
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor");
    private final String label;

    ProductCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
