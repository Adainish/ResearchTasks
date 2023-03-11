package io.github.adainish.researchtasks.conf;

import info.pixelmon.repack.org.spongepowered.serialize.SerializationException;
import io.github.adainish.researchtasks.ResearchTasks;

import java.util.Arrays;

public class LanguageConfig extends Configurable
{
    private static LanguageConfig config;

    public static LanguageConfig getConfig() {
        if (config == null) {
            config = new LanguageConfig();
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
            this.get().node("Messages", "Research", "ResearchPoints").set("&7You received &a%points% level points &7for &e%pokemon% %form%'s &7research level!");
            this.get().node("GUI", "ViewResearchTaskTitle").set("&aView Research Tasks");
            this.get().node("GUI", "ViewResearchLevelTitle").set("&bView Research Level");
            this.get().node("GUI", "FailedRewards").set("&cFailed to hand you your rewards, please contact staff!");
            this.get().node("GUI", "LevelTitleLore").set("&bLevel %level% &e%currentpoints% &7/ &e%pointsneeded%");
            this.get().node("GUI", "RewardNotReadyLore").set("&7You need to obtain more points to claim the rewards for this level!");
            this.get().node("GUI", "RewardAvailableLore").set("&bYou can claim the rewards for this level!");
            this.get().node("GUI", "RewardClaimedLore").set("&4You've already claimed these rewards.");
            this.get().node("GUI", "FormTitle").set("&5%species% %form%");
            this.get().node("GUI", "FormTaskTitle").set("%species% %form% %task%");
            this.get().node("GUI", "TaskTitle").set("&b%species% &5%form% &e%actiontype% &a%tasktype% &f%progress% &7/ &f%count%");
            this.get().node("GUI", "PreviousButtonTitle").set("Previous Page");
            this.get().node("GUI", "NextButtonTitle").set("Next Page");
            this.get().node("GUI", "BackButtonTitle").set("&eClick to go back");
        } catch (SerializationException e) {
            ResearchTasks.log.error(e);
        }


    }

    public String getConfigName() {
        return "language.yaml";
    }

    public LanguageConfig() {
    }
}
