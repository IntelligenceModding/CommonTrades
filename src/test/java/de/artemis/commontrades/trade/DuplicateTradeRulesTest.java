package de.artemis.commontrades.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        List<Trade> offers = new ArrayList<>(Arrays.asList(
                new Trade("apple", false),
                new Trade("apple", false)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(0, removed, "external offers should not be removed");
        assertEquals(Arrays.asList(
                new Trade("apple", false),
                new Trade("apple", false)), offers, "external offer order should be preserved");
    }

    private static void removesOwnedOfferWhenExternalOfferHasSameResult() {
        List<Trade> offers = new ArrayList<>(Arrays.asList(
                new Trade("apple", true),
                new Trade("apple", false),
                new Trade("carrot", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(1, removed, "owned duplicate should be removed when an external offer sells the same item");
        assertEquals(Arrays.asList(
                new Trade("apple", false),
                new Trade("carrot", true)), offers, "only the colliding owned offer should be removed");
    }

    private static void keepsOnlyFirstOwnedOfferForEachResult() {
        List<Trade> offers = new ArrayList<>(Arrays.asList(
                new Trade("apple", true),
                new Trade("apple", true),
                new Trade("apple", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(2, removed, "duplicate owned offers should be collapsed");
        assertEquals(Collections.singletonList(new Trade("apple", true)), offers, "first owned offer should be retained");
    }

    private static void keepsDistinctOwnedOffers() {
        List<Trade> offers = new ArrayList<>(Arrays.asList(
                new Trade("apple", true),
                new Trade("carrot", true),
                new Trade("potato", true)));

        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, Trade::result, Trade::owned);

        assertEquals(0, removed, "distinct owned offers should be kept");
        assertEquals(Arrays.asList(
                new Trade("apple", true),
                new Trade("carrot", true),
                new Trade("potato", true)), offers, "distinct owned offers should preserve order");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static final class Trade {
        private final String result;
        private final boolean owned;

        private Trade(String result, boolean owned) {
            this.result = result;
            this.owned = owned;
        }

        private String result() {
            return result;
        }

        private boolean owned() {
            return owned;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof Trade)) {
                return false;
            }
            Trade trade = (Trade) object;
            return owned == trade.owned && Objects.equals(result, trade.result);
        }

        @Override
        public int hashCode() {
            return Objects.hash(result, owned);
        }

        @Override
        public String toString() {
            return "Trade[result=" + result + ", owned=" + owned + "]";
        }
    }
}
