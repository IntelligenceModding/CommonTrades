package de.artemis.commontrades.client;

import java.util.BitSet;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class CommonTradeClientOfferMarkers {
    private static final BitSet MARKED_OFFERS = new BitSet();
    private static int containerId = -1;

    private CommonTradeClientOfferMarkers() {
    }

    public static void acceptSyncedIndexes(int syncedContainerId, int[] offerIndexes) {
        containerId = syncedContainerId;
        MARKED_OFFERS.clear();
        for (int offerIndex : offerIndexes) {
            if (offerIndex >= 0) {
                MARKED_OFFERS.set(offerIndex);
            }
        }
    }

    public static void clearSyncedIndexes(int syncedContainerId) {
        if (containerId == syncedContainerId) {
            containerId = -1;
            MARKED_OFFERS.clear();
        }
    }

    public static boolean isMarked(MerchantMenu menu, int offerIndex) {
        return menu.containerId == containerId && MARKED_OFFERS.get(offerIndex);
    }

    public static boolean isMarked(MerchantMenu menu, MerchantOffer offer) {
        if (menu.containerId != containerId || MARKED_OFFERS.isEmpty()) {
            return false;
        }

        MerchantOffers offers = menu.getOffers();
        for (int index = 0; index < offers.size(); index++) {
            if (offers.get(index) == offer) {
                return MARKED_OFFERS.get(index);
            }
        }
        return false;
    }
}
