package io.github.adainish.researchtasks.util;

import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import io.github.adainish.researchtasks.ResearchTasks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Util {
    public static void runCommand(String cmd) {
        try {
            ResearchTasks.server.getCommandManager().getDispatcher().execute(cmd, ResearchTasks.server.getCommandSource());
        } catch (CommandSyntaxException e) {
            ResearchTasks.log.error(e);
        }
    }

    public static GooeyButton filler = GooeyButton.builder()
            .display(new ItemStack(Blocks.GRAY_STAINED_GLASS_PANE, 1))
            .build();


    public static String getResourceLocationStringFromItemStack(ItemStack stack)
    {
        return stack.getItem().getRegistryName().toString();
    }

    public static String underScoreReformattedString(String s)
    {
        if (s == null)
            return "";
        s = s.replaceAll("_", " ");
        return s;
    }

    public static boolean hasEvolution(Stats form)
    {
        return !form.getEvolutions().isEmpty();
    }

    public static String getRandomStatusAttack(Species species, Stats form)
    {
        Pokemon pokemon = PokemonBuilder.builder().species(species).form(form).build();
        ImmutableAttack atk;
        List<ImmutableAttack> immutableAttacks = new ArrayList <>();
        for (ImmutableAttack immutableAttack:pokemon.getForm().getMoves().getAllMoves()) {
            if (immutableAttack.getAttackCategory().equals(AttackCategory.STATUS))
                immutableAttacks.add(immutableAttack);
        }
        atk = RandomHelper.getRandomElementFromCollection(immutableAttacks);

        if (atk != null) {
            return atk.getAttackName();
        }
        return "";
    }

    public static String getRandomSpecialAttack(Species species, Stats form)
    {
        Pokemon pokemon = PokemonBuilder.builder().species(species).form(form).build();
        ImmutableAttack atk;
        List<ImmutableAttack> immutableAttacks = new ArrayList <>();
        for (ImmutableAttack immutableAttack:pokemon.getForm().getMoves().getAllMoves()) {
            if (immutableAttack.getAttackCategory().equals(AttackCategory.SPECIAL))
                immutableAttacks.add(immutableAttack);
        }
        atk = RandomHelper.getRandomElementFromCollection(immutableAttacks);

        if (atk != null) {
            return atk.getAttackName();
        }
        return "";
    }

    public static String getRandomPhysicalAttack(Species species, Stats form)
    {
        Pokemon pokemon = PokemonBuilder.builder().species(species).form(form).build();
        ImmutableAttack atk;
        List<ImmutableAttack> immutableAttacks = new ArrayList <>();
        for (ImmutableAttack immutableAttack:pokemon.getForm().getMoves().getAllMoves()) {
            if (immutableAttack.getAttackCategory().equals(AttackCategory.PHYSICAL))
                immutableAttacks.add(immutableAttack);
        }
        atk = RandomHelper.getRandomElementFromCollection(immutableAttacks);
        if (atk != null) {
            return atk.getAttackName();
        }
        return "";
    }

    public static String formattedString(String s) {
        if (s == null)
            return "";
        return s.replaceAll("&", "§");
    }

    public static void send(UUID uuid, String message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;
        if (server.getPlayerList().getPlayerByUUID(uuid) == null)
            return;
        if (message == null)
            return;
        if (message.isEmpty())
            return;
        server.getPlayerList().getPlayerByUUID(uuid).sendMessage(new StringTextComponent((message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")), uuid);
    }


    public static List <String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList <>();
        for (String s:list) {
            formattedList.add(formattedString(s));
        }

        return formattedList;
    }
}
