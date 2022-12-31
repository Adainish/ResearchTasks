package io.github.adainish.researchtasks.wrapper;

import io.github.adainish.researchtasks.obj.Player;
import io.github.adainish.researchtasks.obj.ResearchDex;

import java.util.HashMap;
import java.util.UUID;

public class ResearchWrapper {
    public HashMap<UUID, Player> playerHashMap = new HashMap <>();

    public ResearchDex cachedResearchDex;

    public ResearchWrapper()
    {
        cachedResearchDex = new ResearchDex();
        cachedResearchDex.loadFromConfig();
    }
}
