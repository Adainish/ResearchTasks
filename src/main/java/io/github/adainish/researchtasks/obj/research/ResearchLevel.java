package io.github.adainish.researchtasks.obj.research;

import info.pixelmon.repack.org.spongepowered.serialize.SerializationException;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.obj.Reward;
import io.leangen.geantyref.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResearchLevel {

    private int level;
    private String species;
    private String form;
    private int requiredPoints = 1;
    private List<String> rewardIDs = new ArrayList <>();
    private boolean claimed = false;

    public ResearchLevel(int level, String species, String form)
    {
        this.setLevel(level);
        this.setSpecies(species);
        this.setForm(form);
        sync();
    }

    public void sync()
    {
        this.setRequiredPoints(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Levels", String.valueOf(getLevel()), "RequiredPoints").getInt());
        try {
            this.setRewardIDs(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Levels", String.valueOf(getLevel()), "RewardIds").getList(TypeToken.get(String.class)));
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public boolean completed(int progress)
    {
        return getRequiredPoints() <= progress;
    }

    public boolean handoutRewards(UUID uuid)
    {
        if (isClaimed())
            return false;

        for (Reward r:getRewards()) {
            r.handOutRewards(uuid);
        }
        return true;
    }

    public boolean claimed()
    {
        return isClaimed();
    }

    public List <Reward> getRewards()
    {
        List<Reward> rewards = new ArrayList <>();
        for (String s: getRewardIDs()) {
            if (ResearchTasks.rewardRegistry.rewardCache.containsKey(s))
                rewards.add(ResearchTasks.rewardRegistry.rewardCache.get(s));
        }
        return rewards;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public List <String> getRewardIDs() {
        return rewardIDs;
    }

    public void setRewardIDs(List <String> rewardIDs) {
        this.rewardIDs = rewardIDs;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
