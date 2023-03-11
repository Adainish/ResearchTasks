package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;

import java.util.HashMap;
import java.util.Map;

public class ResearchPokemon implements Cloneable {
    public String configIdentifier;
    public String pokemonSpeciesName;
    public int dexNumber = 0;

    public HashMap <String, ResearchForm> formsList = new HashMap<>();

    public ResearchPokemon(String identifier)
    {
        this.setConfigIdentifier(identifier);
        Species species = PixelmonSpecies.MAGIKARP.getValueUnsafe();
        if (PixelmonSpecies.get(identifier).isPresent()) {
            species = PixelmonSpecies.get(identifier).get().getValueUnsafe();
            this.setPokemonSpeciesName(species.getName());
        }
        this.setDexNumber(species.getDex());
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
            Species species = PixelmonSpecies.get(getPokemonSpeciesName()).get().getValueUnsafe();
            ResearchForm researchForm = new ResearchForm(species, nodestring);
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

    public String getPokemonSpeciesName() {
        return pokemonSpeciesName;
    }

    public void setPokemonSpeciesName(String pokemonSpeciesName) {
        this.pokemonSpeciesName = pokemonSpeciesName;
    }

    public HashMap <String, ResearchForm> getFormsList() {
        return formsList;
    }

    public void setFormsList(HashMap <String, ResearchForm> formsList) {
        this.formsList = formsList;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }
}
