package io.github.adainish.researchtasks.listener;

import com.pixelmonmod.pixelmon.api.daycare.event.DayCareEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import io.github.adainish.researchtasks.enumerations.TaskTypes;
import io.github.adainish.researchtasks.obj.Player;
import io.github.adainish.researchtasks.obj.ResearchDex;
import io.github.adainish.researchtasks.storage.PlayerStorage;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ResearchListener
{

    //catch listener
    @SubscribeEvent
    public void onCatchEvent(CaptureEvent.SuccessfulCapture event)
    {
        if (event.isCanceled())
            return;

        Player player = PlayerStorage.getPlayer(event.getPlayer().getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            researchDex.update(player, event.getPokemon().getPokemon(), TaskTypes.Caught.name(), "");
        }
    }
    //move listener
    @SubscribeEvent
    public void attackListener(AttackEvent.Use event)
    {
        if (event.user.isWildPokemon())
            return;
        if (event.user.getTrainerOwner() != null)
            return;
        Player player = PlayerStorage.getPlayer(event.user.getPlayerOwner().getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            String parsed = "";
            switch (event.getAttack().getAttackCategory())
            {
                case STATUS:
                    parsed = TaskTypes.Status_Move_Used.name();
                    break;
                case SPECIAL:
                    parsed = TaskTypes.Special_Move_Used.name();
                    break;
                case PHYSICAL:
                    parsed = TaskTypes.Physical_Move_Used.name();
                    break;
            }
            if (parsed.isEmpty())
                return;

            researchDex.update(player, event.user.pokemon, parsed, event.attack.getAttackName());
        }
    }
    //breed listener
    @SubscribeEvent
    public void onBreedCollectEvent(DayCareEvent.PostCollect event)
    {
        Player player = PlayerStorage.getPlayer(event.getPlayer().getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            researchDex.update(player, event.getParentOne(), TaskTypes.Breed.name(), "");
            researchDex.update(player, event.getChildGiven(), TaskTypes.Breed.name(), "");
        }
    }
    //hatched
    @SubscribeEvent
    public void onHatchEvent(EggHatchEvent.Post event)
    {
        Player player = PlayerStorage.getPlayer(event.getPlayer().getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            researchDex.update(player, event.getPokemon(), TaskTypes.Hatched.name(), "");
        }
    }
    //defeated
    @SubscribeEvent
    public void onBeatWildPixelmonEvent(BeatWildPixelmonEvent event)
    {
        Player player = PlayerStorage.getPlayer(event.player.getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            for (PixelmonWrapper wrapper :event.wpp.allPokemon) {
                if (wrapper == null)
                    continue;
                if (wrapper.pokemon == null)
                    continue;
                researchDex.update(player, wrapper.pokemon, TaskTypes.Defeated.name(), "");
            }

        }
    }
    //evolved
    @SubscribeEvent
    public void onEvolutionEvent(EvolveEvent.Pre event)
    {
        if (event.isCanceled())
            return;
        Player player = PlayerStorage.getPlayer(event.getPlayer().getUniqueID());
        if (player != null) {
            ResearchDex researchDex = player.researchDex;
            researchDex.update(player, event.getPokemon(), TaskTypes.Evolved.name(), "");
        }
    }
}
