package de.artemis.commontrades.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.artemis.commontrades.trade.debug.WanderingTradeDebugReport;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class CommonTradesCommands {
    private static final int REQUIRED_PERMISSION_LEVEL = 2;

    private CommonTradesCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("commontrades")
                .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .then(Commands.literal("trades")
                        .executes(context -> showTrades(context, null, 1))
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(context -> showTrades(context, null, IntegerArgumentType.getInteger(context, "page"))))
                        .then(Commands.literal("commontrades")
                                .executes(context -> showTrades(context, "commontrades", 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> showTrades(context, "commontrades", IntegerArgumentType.getInteger(context, "page")))))
                        .then(Commands.literal("vanilla")
                                .executes(context -> showTrades(context, "vanilla", 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> showTrades(context, "vanilla", IntegerArgumentType.getInteger(context, "page")))))
                        .then(Commands.argument("modid", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        WanderingTradeDebugReport.suggestedFilters(context.getSource().getServer().registryAccess()),
                                        builder))
                                .executes(context -> showTrades(context, StringArgumentType.getString(context, "modid"), 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> showTrades(
                                                context,
                                                StringArgumentType.getString(context, "modid"),
                                                IntegerArgumentType.getInteger(context, "page")))))));
    }

    private static int showTrades(CommandContext<CommandSourceStack> context, String filter, int page) {
        WanderingTradeDebugReport report = WanderingTradeDebugReport.create(context.getSource().getServer().registryAccess());
        for (String line : report.format(filter, page)) {
            context.getSource().sendSuccess(() -> Component.literal(line), false);
        }
        return report.filteredEntryCount(filter);
    }
}
