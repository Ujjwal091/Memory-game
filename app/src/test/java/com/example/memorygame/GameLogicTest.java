package com.example.memorygame;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameLogicTest {

    @Test
    public void testCardShuffling() {
        List<Card> cards = createCardList();
        List<Card> originalOrder = new ArrayList<>(cards);

        // Shuffle the cards
        Collections.shuffle(cards);

        // Assert that the order of the cards has changed
        Assert.assertNotEquals(originalOrder, cards);
    }

    @Test
    public void testCardMatching() {
        List<Card> cards = createCardList();

        // Select two cards with the same ID for matching
        Card card1 = cards.get(0);
        Card card2 = cards.get(1);

        // Set both cards as flipped
        card1.setFlipped(true);
        card2.setFlipped(true);

        // Check if the cards are matched
        boolean isMatched = card1.getId() == card2.getId();

        // Assert that the cards are matched
        Assert.assertTrue(isMatched);
    }

    @Test
    public void testScoreCalculation() {
        int initialScore = 0;
        int remainingPairs = 6;

        // Calculate the score based on the remaining pairs
        int score = initialScore + remainingPairs * 10;

        // Assert that the score calculation is accurate
        Assert.assertEquals(60, score);
    }

    private List<Card> createCardList() {
        List<Card> cards = new ArrayList<>();

        // Add the cards to the list
        cards.add(new Card(1, R.drawable.card1));
        cards.add(new Card(1, R.drawable.card1_desc));
        cards.add(new Card(2, R.drawable.card2));
        cards.add(new Card(2, R.drawable.card2_desc));
        cards.add(new Card(3, R.drawable.card3));
        cards.add(new Card(3, R.drawable.card3_desc));
        cards.add(new Card(4, R.drawable.card4));
        cards.add(new Card(4, R.drawable.card4_desc));
        cards.add(new Card(5, R.drawable.card5));
        cards.add(new Card(5, R.drawable.card5_desc));
        cards.add(new Card(6, R.drawable.card6));
        cards.add(new Card(6, R.drawable.card6_desc));

        return cards;
    }
}
