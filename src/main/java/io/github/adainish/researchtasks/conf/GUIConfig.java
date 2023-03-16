package io.github.adainish.researchtasks.conf;

import info.pixelmon.repack.org.spongepowered.serialize.SerializationException;
import io.github.adainish.researchtasks.ResearchTasks;

import java.util.Arrays;

public class GUIConfig extends Configurable
{
    private static GUIConfig config;

    public static GUIConfig getConfig() {
        if (config == null) {
            config = new GUIConfig();
        }
        return config;
    }

    public void setup() {
        super.setup();
    }

    public void load() {
        super.load();
    }

    public void populate() {
        try {
            this.get().node("Buttons", "GenerationFilterButton").set("minecraft:purple_dye");
            this.get().node("Buttons", "BackButton").set("pixelmon:eject_button");
            this.get().node("Buttons", "ResearchLevelButton").set("minecraft:experience_bottle");
            this.get().node("Buttons", "ResearchTasksButton").set("pixelmon:rainbow_badge");
            this.get().node("Buttons", "CaughtButton").set("pixelmon:poke_ball");
            this.get().node("Buttons", "PhysicalMovesButton").set("pixelmon:ruby_sword");
            this.get().node("Buttons", "SpecialMovesButton").set("pixelmon:crystal_sword");
            this.get().node("Buttons", "StatusMovesButton").set("pixelmon:star_piece");
            this.get().node("Buttons", "BreedButton").set("pixelmon:destiny_knot");
            this.get().node("Buttons", "HatchedButton").set("pixelmon:lucky_egg");
            this.get().node("Buttons", "DefeatedButton").set("pixelmon:power_weight");
            this.get().node("Buttons", "EvolvedButton").set("pixelmon:ever_stone");
            this.get().node("Buttons", "PreviousPageButton").set("pixelmon:trade_holder_left");
            this.get().node("Buttons", "NextPageButton").set("pixelmon:trade_holder_right");

            this.get().node("MainMenu", "Title").set("&6&lResearch Tasks");
            this.get().node("MainMenu", "FilterButtonColourCode").set("e");
            this.get().node("MainMenu", "FilterButtonLore").set("&bClick to filter through generation options");
            this.get().node("MainMenu", "PokemonColourCode").set("b");
            this.get().node("MainMenu", "PokemonLore").set(Arrays.asList("&7Click to view the research options for this Pokemon"));
            this.get().node("MainMenu", "PreviousButtonPosition").set(3);
            this.get().node("MainMenu", "NextButtonPosition").set(5);
            this.get().node("MainMenu", "FilterButtonPosition").set(4);
            this.get().node("FormsMenu", "BackButtonPosition").set(0);
            this.get().node("FormsMenu", "Title").set("&6&lResearch Forms");
            this.get().node("OptionMenu", "BackButtonPosition").set(0);
            this.get().node("OptionMenu", "LevelsButtonPosition").set(3);
            this.get().node("OptionMenu", "FormsButtonPosition").set(5);
            this.get().node("OptionMenu", "Title").set("&6&lResearch Progress");
            this.get().node("LevelsMenu", "BackButtonPosition").set(0);
            this.get().node("LevelsMenu", "Title").set("&6&lResearch Level");
            this.get().node("ResearchFormTaskTypeMenu", "BackButtonPosition").set(0);
            this.get().node("ResearchFormTaskTypeMenu", "Title").set("&6&lResearch Types");
            this.get().node("TasksMenu", "BackButtonPosition").set(0);
            this.get().node("TasksMenu", "Title").set("&6&lResearch Tasks");
        } catch (SerializationException e) {
            ResearchTasks.log.error(e);
        }


    }

    public String getConfigName() {
        return "gui.yaml";
    }

    public GUIConfig() {
    }
}
