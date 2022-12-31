package io.github.adainish.researchtasks.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import io.github.adainish.researchtasks.ResearchTasks;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static void runCommand(String cmd) {
        try {
            ResearchTasks.server.getCommandManager().getDispatcher().execute(cmd, ResearchTasks.server.getCommandSource());
        } catch (CommandSyntaxException e) {
            ResearchTasks.log.error(e);
        }
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
            System.out.println("Species: %species%, Form: %form%, Attack: %atk%"
                    .replace("%species%", species.getName())
                            .replace("%form%", form.getName())
                            .replace("%atk%", atk.getAttackName()
                    )
            );
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
            System.out.println("Species: %species%, Form: %form%, Attack: %atk%"
                    .replace("%species%", species.getName())
                            .replace("%form%", form.getName())
                            .replace("%atk%", atk.getAttackName()
                    )
            );
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
            System.out.println("Species: %species%, Form: %form%, Attack: %atk%"
                    .replace("%species%", species.getName())
                            .replace("%form%", form.getName())
                            .replace("%atk%", atk.getAttackName()
                    )
            );
            return atk.getAttackName();
        }
        return "";
    }

    public static String formattedString(String s) {
        if (s == null)
            return "";
        return s.replaceAll("&", "§");
    }

    public static List <String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList <>();
        for (String s:list) {
            formattedList.add(formattedString(s));
        }

        return formattedList;
    }
}
