package de.artemis.commontrades.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DuplicateTradeRulesTest {
    private DuplicateTradeRulesTest() {
    }

    public static void main(String[] args) {
        keepsExternalOffers();
        removesOwnedOfferWhenExternalOfferHasSameResult();
        keepsOnlyFirstOwnedOfferForEachResult();
        keepsDistinctOwnedOffers();
    }

    private static void keepsExternalOffers() {
        List<Trade> offers = new ArrayList<>(List.of(
                new Trade("apple", false),
                new Trade("apple", false)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(0, removed, "external offers should not be removed");
        assertEquals(List.of(
                new Trade("apple", false),
                new Trade("apple", false)), offers, "external offer order should be preserved");
    }

    private static void removesOwnedOfferWhenExternalOfferHasSameResult() {
        List<Trade> offers = new ArrayList<>(List.of(
                new Trade("apple", true),
                new Trade("apple", false),
                new Trade("carrot", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(1, removed, "owned duplicate should be removed when an external offer sells the same item");
        assertEquals(List.of(
                new Trade("apple", false),
                new Trade("carrot", true)), offers, "only the colliding owned offer should be removed");
    }

    private static void keepsOnlyFirstOwnedOfferForEachResult() {
        List<Trade> offers = new ArrayList<>(List.of(
                new Trade("apple", true),
                new Trade("apple", true),
                new Trade("apple", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(2, removed, "duplicate owned offers should be collapsed");
        assertEquals(List.of(new Trade("apple", true)), offers, "first owned offer should be retained");
    }

    private static void keepsDistinctOwnedOffers() {
        List<Trade> offers = new ArrayList<>(List.of(
                new Trade("apple", true),
                new Trade("carrot", true),
                new Trade("potato", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(0, removed, "distinct owned offers should be kept");
        assertEquals(List.of(
                new Trade("apple", true),
                new Trade("carrot", true),
                new Trade("potato", true)), offers, "distinct owned offers should preserve order");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private record Trade(String result, boolean owned) {
    }
}
