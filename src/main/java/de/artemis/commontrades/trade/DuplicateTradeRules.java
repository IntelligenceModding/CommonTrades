package de.artemis.commontrades.trade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class DuplicateTradeRules {
    private DuplicateTradeRules() {
    }

    public static <T, K> int removeOwnedCollisions(List<T> offers, Function<T, K> resultKey, Predicate<T> isOwned) {
        Set<K> externallySold = new HashSet<>();
        for (T offer : offers) {
            if (!isOwned.test(offer)) {
                externallySold.add(resultKey.apply(offer));
            }
        }

        Set<K> keptOwned = new HashSet<>();
        int originalSize = offers.size();
        offers.removeIf(offer -> {
            if (!isOwned.test(offer)) {
                return false;
            }

            K key = resultKey.apply(offer);
            return externallySold.contains(key) || !keptOwned.add(key);
        });
        return originalSize - offers.size();
    }
}
