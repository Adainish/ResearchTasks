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

    public int level;
    public String species;
    public String form;
    public int requiredPoints = 1;
    public List<String> rewardIDs = new ArrayList <>();
    public boolean claimed = false;

    public ResearchLevel(int level, String species, String form)
    {
        this.level = level;
        this.species = species;
        this.form = form;
        sync();
    }

    public void sync()
    {
        this.requiredPoints = Config.getConfig().get().node("Pokemon", species, form, "Levels", String.valueOf(level), "").getInt();
        try {
            this.rewardIDs = Config.getConfig().get().node("Pokemon", species, form, "Levels", String.valueOf(level), "").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public boolean completed(int progress)
    {
        return requiredPoints <= progress;
    }

    public boolean handoutRewards(UUID uuid)
    {
        if (claimed)
            return false;

        for (Reward r:getRewards()) {
            r.handOutRewards(uuid);
        }
        return true;
    }

    public boolean claimed()
    {
        return claimed;
    }

    public List <Reward> getRewards()
    {
        List<Reward> rewards = new ArrayList <>();
        for (String s:rewardIDs) {
            if (ResearchTasks.rewardRegistry.rewardCache.containsKey(s))
                rewards.add(ResearchTasks.rewardRegistry.rewardCache.get(s));
        }
        return  rewards;
    }
}
