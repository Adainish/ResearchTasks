package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;

import java.util.HashMap;
import java.util.Map;

public class ResearchPokemon {
    private String configIdentifier;
    private Pokemon pokemon;

    private HashMap <String, ResearchForm> formsList = new HashMap<>();


    public ResearchPokemon(String identifier)
    {
        this.setConfigIdentifier(identifier);
        if (PixelmonSpecies.get(identifier).isPresent()) {
            this.setPokemon(PokemonBuilder.builder().species(PixelmonSpecies.get(identifier).get().getValueUnsafe()).build());
        } else this.setPokemon(PokemonBuilder.builder().species(PixelmonSpecies.MAGIKARP.getValueUnsafe()).build());
        loadForms();
    }

    public void loadForms()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", getConfigIdentifier());
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();

            ResearchForm researchForm = new ResearchForm(getPokemon().getSpecies(), nodestring);
            getFormsList().put(nodestring, researchForm);
        }
    }

    public void sync()
    {
        getFormsList().values().forEach(ResearchForm::sync);
    }

    public String getConfigIdentifier() {
        return configIdentifier;
    }

    public void setConfigIdentifier(String configIdentifier) {
        this.configIdentifier = configIdentifier;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public HashMap <String, ResearchForm> getFormsList() {
        return formsList;
    }

    public void setFormsList(HashMap <String, ResearchForm> formsList) {
        this.formsList = formsList;
    }
}
