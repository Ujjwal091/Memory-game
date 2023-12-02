package com.example.memorygame;

public class Card {
    private final int id;        // Unique identifier for the card
    private final int imageId;   // Resource ID of the card image
    private boolean isFlipped;   // Flag indicating whether the card is flipped

    public Card(int id, int imageId) {
        this.id = id;
        this.imageId = imageId;
        this.isFlipped = false;   // Initialize the card as not flipped
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }
}
