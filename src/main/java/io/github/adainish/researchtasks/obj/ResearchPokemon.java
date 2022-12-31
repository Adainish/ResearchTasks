package io.github.adainish.researchtasks.obj;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;

import java.util.HashMap;
import java.util.Map;

public class ResearchPokemon {
    public String configIdentifier;
    public Pokemon pokemon;

    public HashMap <String, ResearchForm> formsList = new HashMap<>();


    public ResearchPokemon(String identifier)
    {
        this.configIdentifier = identifier;
        if (PixelmonSpecies.get(identifier).isPresent()) {
            this.pokemon = PokemonBuilder.builder().species(PixelmonSpecies.get(identifier).get().getValueUnsafe()).build();
        } else this.pokemon = PokemonBuilder.builder().species(PixelmonSpecies.MAGIKARP.getValueUnsafe()).build();
    }

    public void loadForms()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", configIdentifier);
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();

            ResearchForm researchForm = new ResearchForm(pokemon.getSpecies(), nodestring);
            formsList.put(nodestring, researchForm);
        }
    }

}
