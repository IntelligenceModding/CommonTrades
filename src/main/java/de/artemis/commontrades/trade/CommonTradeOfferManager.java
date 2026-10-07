package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.config.CommonTradesConfig;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class CommonTradeOfferManager {
    private static final Set<MerchantOffer> OWNED_OFFERS = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private CommonTradeOfferManager() {
    }

    public static void finalizeOffers(WanderingTrader trader) {
        if (!CommonTradesConfig.enabled()) {
            removeOwnedCollisions(trader.getOffers());
            return;
        }

        MerchantOffers offers = trader.getOffers();
        removeOwnedCollisions(offers);

        int targetCount = CommonTradesConfig.extraTradesPerTrader();
        int ownedCount = countOwnedOffers(offers);
        if (ownedCount < targetCount) {
            Set<Item> blockedItems = collectSoldItems(offers);
            List<MerchantOffer> additions = TradePoolCache.createOffers(
                    trader.getRandom(),
                    blockedItems,
                    trader.level().enabledFeatures(),
                    targetCount - ownedCount);

            for (MerchantOffer addition : additions) {
                OWNED_OFFERS.add(addition);
                offers.add(addition);
            }
        }

        removeOwnedCollisions(offers);
    }

    public static boolean owns(MerchantOffer offer) {
        return OWNED_OFFERS.contains(offer);
    }

    static int removeOwnedCollisions(MerchantOffers offers) {
        int removed = DuplicateTradeRules.removeOwnedCollisions(offers, CommonTradeOfferManager::resultItem, CommonTradeOfferManager::owns);
        if (removed > 0) {
            CommonTrades.LOGGER.debug("Removed {} duplicate Common Trades Wandering Trader offer(s).", removed);
        }
        return removed;
    }

    private static Item resultItem(MerchantOffer offer) {
        return offer.getResult().getItem();
    }

    private static int countOwnedOffers(MerchantOffers offers) {
        int count = 0;
        for (MerchantOffer offer : offers) {
            if (owns(offer)) {
                count++;
            }
        }
        return count;
    }

    private static Set<Item> collectSoldItems(MerchantOffers offers) {
        Set<Item> soldItems = new HashSet<>();
        for (MerchantOffer offer : offers) {
            soldItems.add(resultItem(offer));
        }
        return soldItems;
    }
}
