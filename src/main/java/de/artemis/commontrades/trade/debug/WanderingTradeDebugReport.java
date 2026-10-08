package de.artemis.commontrades.trade.debug;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.config.CommonTradesConfig;
import de.artemis.commontrades.trade.RegisteredWanderingTradeInspector;
import de.artemis.commontrades.trade.TradeCategory;
import de.artemis.commontrades.trade.TradeEntry;
import de.artemis.commontrades.trade.TradePoolCache;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;

public final class WanderingTradeDebugReport {
    private static final int ENTRIES_PER_PAGE = 18;
    private static final String COMMON_TRADES_FILTER = CommonTrades.MOD_ID;
    private static final String VANILLA_FILTER = "vanilla";
    private static final String UNKNOWN_MOD_ID = "unknown";
    private static final String OTHER_MODDED_MINECRAFT_ID = "minecraft";

    private final List<TradeGroup> groups;
    private final Map<String, Integer> resultCounts;

    private WanderingTradeDebugReport(List<TradeGroup> groups, Map<String, Integer> resultCounts) {
        this.groups = groups;
        this.resultCounts = resultCounts;
    }

    public static WanderingTradeDebugReport create(RegistryAccess registries) {
        TradePoolCache.ensureBuilt();

        Map<String, TradeGroupBuilder> otherModGroups = new HashMap<>();
        TradeGroupBuilder vanilla = new TradeGroupBuilder(GroupKind.VANILLA, VANILLA_FILTER, "Vanilla");
        TradeGroupBuilder commonTrades = new TradeGroupBuilder(GroupKind.COMMON_TRADES, COMMON_TRADES_FILTER, "Common Trades");

        collectRegisteredTrades(registries, vanilla, otherModGroups);
        collectCommonTrades(commonTrades);

        List<TradeGroup> groups = new ArrayList<>();
        groups.add(vanilla.build());
        otherModGroups.values().stream()
                .map(TradeGroupBuilder::build)
                .sorted(Comparator.comparing(TradeGroup::title))
                .forEach(groups::add);
        groups.add(commonTrades.build());

        Map<String, Integer> resultCounts = new HashMap<>();
        for (TradeGroup group : groups) {
            for (DebugTrade trade : group.trades()) {
                trade.resultId().ifPresent(id -> resultCounts.merge(id.toString(), 1, Integer::sum));
            }
        }
        return new WanderingTradeDebugReport(groups, resultCounts);
    }

    public static Iterable<String> suggestedFilters(RegistryAccess registries) {
        WanderingTradeDebugReport report = create(registries);
        Set<String> filters = new TreeSet<>();
        filters.add(VANILLA_FILTER);
        filters.add(COMMON_TRADES_FILTER);
        for (TradeGroup group : report.groups) {
            if (group.kind() == GroupKind.OTHER_MOD) {
                filters.add(group.filterId());
            }
        }
        return filters;
    }

    public List<String> format(String rawFilter, int requestedPage) {
        String filter = normalizeFilter(rawFilter);
        List<TradeGroup> visibleGroups = filteredGroups(filter);
        List<IndexedTrade> entries = flatten(visibleGroups);
        int totalPages = Math.max(1, (entries.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE);
        int page = Math.min(Math.max(1, requestedPage), totalPages);
        int from = Math.min((page - 1) * ENTRIES_PER_PAGE, entries.size());
        int to = Math.min(from + ENTRIES_PER_PAGE, entries.size());

        List<String> lines = new ArrayList<>();
        lines.add("=== Wandering Trader Trades ===");
        lines.add("Page " + page + "/" + totalPages + " - " + entries.size() + " matching trade(s)");
        lines.add(summaryLine());
        if (filter != null) {
            lines.add("Filter: " + filter);
        }
        lines.add("");

        if (entries.isEmpty()) {
            lines.add("No matching Wandering Trader trades found.");
            return lines;
        }

        TradeGroup currentGroup = null;
        for (int index = from; index < to; index++) {
            IndexedTrade indexedTrade = entries.get(index);
            if (indexedTrade.group() != currentGroup) {
                if (currentGroup != null) {
                    lines.add("");
                }
                currentGroup = indexedTrade.group();
                lines.add(currentGroup.title() + " (" + currentGroup.trades().size() + ")");
            }
            lines.add(formatTrade(indexedTrade.trade()));
        }

        if (page < totalPages) {
            lines.add("");
            lines.add("Use /commontrades trades" + pageCommandSuffix(filter, page + 1) + " for the next page.");
        }
        return lines;
    }

    public int filteredEntryCount(String rawFilter) {
        return flatten(filteredGroups(normalizeFilter(rawFilter))).size();
    }

    private static void collectRegisteredTrades(
            RegistryAccess registries,
            TradeGroupBuilder vanilla,
            Map<String, TradeGroupBuilder> otherModGroups) {
        for (RegisteredWanderingTradeInspector.InspectedTrade inspectedTrade : RegisteredWanderingTradeInspector.wanderingTrades(registries)) {
            DebugTrade trade = inspectedTrade(inspectedTrade);
            String modId = inspectedTrade.tradeId()
                    .map(Identifier::getNamespace)
                    .orElseGet(() -> inspectedTrade.resultId().getNamespace());
            if (OTHER_MODDED_MINECRAFT_ID.equals(modId)) {
                vanilla.add(trade);
            } else {
                if (CommonTrades.MOD_ID.equals(modId)) {
                    modId = UNKNOWN_MOD_ID;
                }
                String title = modDisplayName(modId);
                otherModGroups.computeIfAbsent(modId, id -> new TradeGroupBuilder(GroupKind.OTHER_MOD, id, title)).add(trade);
            }
        }
    }

    private static void collectCommonTrades(TradeGroupBuilder commonTrades) {
        Map<TradeCategory, List<TradeEntry>> entriesByCategory = TradePoolCache.snapshotEntriesByCategory();
        for (TradeCategory category : TradeCategory.values()) {
            for (TradeEntry entry : entriesByCategory.getOrDefault(category, List.of())) {
                commonTrades.add(new DebugTrade(
                        categoryLabel(category),
                        Optional.of(entry.id()),
                        amountDisplay(category),
                        Integer.toString(CommonTradesConfig.emeraldCost(category)),
                        Integer.toString(category.maxUses()),
                        true,
                        ""));
            }
        }
    }

    private static DebugTrade inspectedTrade(RegisteredWanderingTradeInspector.InspectedTrade inspectedTrade) {
        return new DebugTrade(
                inspectedTrade.group(),
                Optional.of(inspectedTrade.resultId()),
                inspectedTrade.amount(),
                inspectedTrade.emeraldCost(),
                inspectedTrade.maxUses(),
                false,
                inspectedTrade.note());
    }

    private List<TradeGroup> filteredGroups(String filter) {
        if (filter == null) {
            return groups;
        }
        return groups.stream()
                .filter(group -> group.matches(filter))
                .toList();
    }

    private static List<IndexedTrade> flatten(List<TradeGroup> groups) {
        List<IndexedTrade> indexedTrades = new ArrayList<>();
        for (TradeGroup group : groups) {
            for (DebugTrade trade : group.trades()) {
                indexedTrades.add(new IndexedTrade(group, trade));
            }
        }
        return indexedTrades;
    }

    private String formatTrade(DebugTrade trade) {
        String label = trade.commonTradesGenerated() ? "Common Trades: " + trade.label() : trade.label();
        String line = "[" + label + "] ";
        line += trade.resultId()
                .map(id -> id + " x" + trade.amount())
                .orElse("dynamic/unknown result x" + trade.amount());
        line += " - " + emeraldText(trade.emeraldCost());
        line += " - max uses " + trade.maxUses();
        if (trade.resultId().map(id -> resultCounts.getOrDefault(id.toString(), 0) > 1).orElse(false)) {
            line += " [possible duplicate]";
        }
        if (!trade.note().isBlank()) {
            line += " (" + trade.note() + ")";
        }
        return line;
    }

    private String summaryLine() {
        int vanillaTotal = count(GroupKind.VANILLA);
        int otherTotal = count(GroupKind.OTHER_MOD);
        int commonTradesTotal = count(GroupKind.COMMON_TRADES);
        return "Vanilla (" + vanillaTotal + ") | Other mods (" + otherTotal + ") | Common Trades (" + commonTradesTotal + ")";
    }

    private int count(GroupKind kind) {
        return groups.stream()
                .filter(group -> group.kind() == kind)
                .mapToInt(group -> group.trades().size())
                .sum();
    }

    private static String pageCommandSuffix(String filter, int page) {
        if (filter == null) {
            return " " + page;
        }
        return " " + filter + " " + page;
    }

    private static String emeraldText(String emeraldCost) {
        if (!"1".equals(emeraldCost)) {
            return emeraldCost + " Emeralds";
        }
        return "1 Emerald";
    }

    private static String normalizeFilter(String rawFilter) {
        if (rawFilter == null || rawFilter.isBlank()) {
            return null;
        }
        return rawFilter.toLowerCase(Locale.ROOT);
    }

    private static String amountDisplay(TradeCategory category) {
        if (category.minOutputCount() == category.maxOutputCount()) {
            return Integer.toString(category.minOutputCount());
        }
        return category.minOutputCount() + "-" + category.maxOutputCount();
    }

    private static String categoryLabel(TradeCategory category) {
        String label = category.logName();
        StringBuilder builder = new StringBuilder(label.length());
        boolean capitalize = true;
        for (int i = 0; i < label.length(); i++) {
            char character = label.charAt(i);
            if (Character.isWhitespace(character)) {
                capitalize = true;
                builder.append(character);
            } else if (capitalize) {
                builder.append(Character.toUpperCase(character));
                capitalize = false;
            } else {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    private static String modDisplayName(String modId) {
        if (UNKNOWN_MOD_ID.equals(modId)) {
            return "Unknown Mod";
        }
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .filter(name -> !name.isBlank())
                .orElse(modId);
    }

    private enum GroupKind {
        VANILLA,
        OTHER_MOD,
        COMMON_TRADES
    }

    private record DebugTrade(
            String label,
            Optional<Identifier> resultId,
            String amount,
            String emeraldCost,
            String maxUses,
            boolean commonTradesGenerated,
            String note) {
    }

    private record IndexedTrade(TradeGroup group, DebugTrade trade) {
    }

    private record TradeGroup(GroupKind kind, String filterId, String title, List<DebugTrade> trades) {
        private boolean matches(String filter) {
            if (kind == GroupKind.VANILLA) {
                return VANILLA_FILTER.equals(filter);
            }
            if (kind == GroupKind.COMMON_TRADES) {
                return COMMON_TRADES_FILTER.equals(filter);
            }
            return filterId.equals(filter);
        }
    }

    private static final class TradeGroupBuilder {
        private final GroupKind kind;
        private final String filterId;
        private final String title;
        private final List<DebugTrade> trades = new ArrayList<>();

        private TradeGroupBuilder(GroupKind kind, String filterId, String title) {
            this.kind = kind;
            this.filterId = filterId;
            this.title = title;
        }

        private void add(DebugTrade trade) {
            trades.add(trade);
        }

        private TradeGroup build() {
            return new TradeGroup(
                    kind,
                    filterId,
                    title,
                    trades.stream()
                            .sorted(Comparator.comparing(trade -> trade.resultId().map(Identifier::toString).orElse("~" + trade.note())))
                            .toList());
        }
    }
}
