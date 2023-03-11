package io.github.adainish.researchtasks.cmd;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.obj.Player;
import io.github.adainish.researchtasks.storage.PlayerStorage;
import io.github.adainish.researchtasks.util.Util;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class Command
{
    public static LiteralArgumentBuilder <CommandSource> getCommand()
    {
        return Commands.literal("researchtasks")
                .executes(cc -> {
                    try {
                        Player p = PlayerStorage.getPlayer(cc.getSource().asPlayer().getUniqueID());
                        p.getOrCreateResearchDex().open(cc.getSource().asPlayer());
                    } catch (Exception e) {
                        cc.getSource().sendErrorMessage(new StringTextComponent("Something went wrong while executing the command, please contact a member of Staff if this issue persists"));
                        return 1;
                    }
                    return 1;
                })
                .then(Commands.literal("reload")
                        .executes(cc -> {
                            cc.getSource().sendFeedback(new StringTextComponent(Util.formattedString("&aReloaded the config data and files, please check your server console for any issues!")), true);
                            ResearchTasks.instance.reload();
                            return 1;
                        })
                )
                ;
    }
}
