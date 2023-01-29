package io.github.adainish.researchtasks.obj;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.obj.research.ResearchPokemon;

import java.util.HashMap;
import java.util.Map;

public class ResearchDex {

    public HashMap <Species, ResearchPokemon> researchPokemonList = new HashMap <>();

    public ResearchDex()
    {
        loadFromConfig();
    }

    public void loadFromConfig()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Task");
                continue;
            }
            String nodestring = obj.toString();

            ResearchPokemon dropPokemon = new ResearchPokemon(nodestring);
            researchPokemonList.put(dropPokemon.pokemon.getSpecies(), dropPokemon);
        }
    }

    public void syncTasks()
    {
        researchPokemonList.forEach((key, value) -> {
            value.sync();
            researchPokemonList.put(key, value);
        });

    }

}
