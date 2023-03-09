package io.github.adainish.researchtasks.obj;

import ca.landonjw.gooeylibs2.implementation.tasks.Task;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.storage.PlayerStorage;
import io.github.adainish.researchtasks.util.Util;

import java.util.UUID;

public class Player {
    public UUID uuid;
    public String username;
    public ResearchDex researchDex;

    public Player(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return username;
    }

    public void setUserName(String name) {
        this.username = name;
    }

    public void updateCache() {
        ResearchTasks.researchWrapper.playerHashMap.put(this.uuid, this);
    }

    public void sendMessage(String s)
    {
        if (s == null)
            return;
        if (s.isEmpty())
            return;
        Util.send(uuid, s);
    }

    public void shutdownSave()
    {
        PlayerStorage.savePlayerShutDown(this);
    }

    public void savePlayer()
    {
        PlayerStorage.savePlayer(this);
    }

    public ResearchDex getOrCreateResearchDex()
    {
        if (this.researchDex != null)
            return this.researchDex;
        this.researchDex = ResearchTasks.researchWrapper.preloadedDex;
        return this.researchDex;
    }

    public void getAndUpdateDex()
    {
        if (researchDex == null) {
            getOrCreateResearchDex();
            return;
        }
        Task.builder().execute(t ->
        {
            this.researchDex.syncTasks();
        }).iterations(1).interval(0).build();

    }
}
