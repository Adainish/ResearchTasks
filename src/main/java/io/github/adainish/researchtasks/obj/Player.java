package io.github.adainish.researchtasks.obj;

import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.storage.PlayerStorage;

import java.util.UUID;

public class Player {
    public UUID uuid;
    public String username;

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
        if (ResearchTasks.researchWrapper.playerHashMap.containsKey(this.uuid))
            ResearchTasks.researchWrapper.playerHashMap.replace(this.uuid, this);
        else ResearchTasks.researchWrapper.playerHashMap.put(this.uuid, this);
    }

    public void savePlayer()
    {
        PlayerStorage.savePlayer(this);
    }
}
