package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.config.CommonTradesConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;

public final class CommonTradeOfferManager {
    private static final Set<MerchantOffer> OWNED_OFFERS = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private CommonTradeOfferManager() {
    }

    public static void finalizeOffers(WanderingTraderEntity trader) {
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
                    trader.getCommandSenderWorld().getRandom(),
                    blockedItems,
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

    public static int[] ownedOfferIndexes(MerchantOffers offers) {
        int[] indexes = new int[offers.size()];
        int count = 0;
        for (int index = 0; index < offers.size(); index++) {
            if (owns(offers.get(index))) {
                indexes[count] = index;
                count++;
            }
        }
        return Arrays.copyOf(indexes, count);
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
