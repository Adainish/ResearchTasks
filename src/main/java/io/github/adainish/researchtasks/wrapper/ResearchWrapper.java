package io.github.adainish.researchtasks.wrapper;

import io.github.adainish.researchtasks.obj.Player;
import io.github.adainish.researchtasks.obj.ResearchDex;

import java.util.HashMap;
import java.util.UUID;

public class ResearchWrapper {
    public HashMap<UUID, Player> playerHashMap = new HashMap <>();

    public ResearchDex preloadedDex;

    public ResearchWrapper()
    {
        preloadedDex = new ResearchDex();
        preloadedDex.loadFromConfig();
    }

    public void loadResearchDex()
    {
        this.preloadedDex = new ResearchDex();
        this.preloadedDex.loadFromConfig();
    }
}
