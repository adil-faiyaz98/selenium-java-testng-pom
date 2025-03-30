package com.example.enums;

public enum LocatorStrategy {
    ID("id"),
    NAME("name"),
    CLASS_NAME("className"),
    CSS_SELECTOR("cssSelector"),
    XPATH("xpath"),
    LINK_TEXT("linkText"),
    PARTIAL_LINK_TEXT("partialLinkText"),
    TAG_NAME("tagName");

    private final String value;

    LocatorStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}