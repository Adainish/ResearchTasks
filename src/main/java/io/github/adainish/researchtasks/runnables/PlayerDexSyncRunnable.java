package io.github.adainish.researchtasks.runnables;

import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.obj.Player;

public class PlayerDexSyncRunnable implements Runnable
{

    @Override
    public void run() {
        if (ResearchTasks.researchWrapper.playerHashMap.values().isEmpty())
            return;

        for (Player p:ResearchTasks.researchWrapper.playerHashMap.values()) {
            p.getAndUpdateDex();
        }
    }
}
