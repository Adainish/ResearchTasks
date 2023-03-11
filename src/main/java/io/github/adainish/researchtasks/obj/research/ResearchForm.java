package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.enumerations.TaskTypes;

import java.util.*;

public class ResearchForm implements Cloneable{
    public String species;
    public String form;

    public List<ResearchFormTasks> researchTasks = new ArrayList <>();
    public List<ResearchLevel> researchLevels = new ArrayList <>();
    public int progressPoints = 0;

    public ResearchForm(String species, String form, List<ResearchFormTasks> copiedResearchTasks, List<ResearchLevel> copiedResearchLevels, int progressPoints)
    {
        this.species = species;
        this.form = form;
        List<ResearchFormTasks> tasks = new ArrayList <>(copiedResearchTasks);
        List<ResearchLevel> levels = new ArrayList <>(copiedResearchLevels);
        Collections.copy(tasks, copiedResearchTasks);
        Collections.copy(levels, copiedResearchLevels);
        this.researchTasks = tasks;
        this.researchLevels = levels;
        this.progressPoints = 0;
    }

    public ResearchForm(Species species, String formID)
    {
        this.setSpecies(species.getName());
        this.setForm(formID);
        loadTasks();
        loadLevels();
    }

    public void loadTasks()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Tasks");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();
                ResearchFormTasks researchFormTask = new ResearchFormTasks(species, form, nodestring);
                getResearchTasks().add(researchFormTask);
        }
    }

    public void loadLevels()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Levels");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();

            ResearchLevel researchLevel = new ResearchLevel(Integer.parseInt(nodestring), getSpecies(), getForm());
            getResearchLevels().add(researchLevel);
        }
        getResearchLevels().sort(Comparator.comparing(ResearchLevel::getLevel));
    }

    public Pokemon getPokemon()
    {
        PokemonBuilder builder = PokemonBuilder.builder();
        builder.species(species);
        builder.form(form);
        return builder.build();
    }

    public void increasePoints(int val)
    {
        this.progressPoints += val;
    }

    public void sync()
    {
        getResearchLevels().forEach(ResearchLevel::sync);
        getResearchTasks().forEach(ResearchFormTasks::sync);
    }


    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public List <ResearchLevel> getResearchLevels() {
        return researchLevels;
    }

    public void setResearchLevels(List <ResearchLevel> researchLevels) {
        this.researchLevels = researchLevels;
    }

    public int getProgressPoints() {
        return progressPoints;
    }

    public void setProgressPoints(int progressPoints) {
        this.progressPoints = progressPoints;
    }

    public List <ResearchFormTasks> getResearchTasks() {
        return researchTasks;
    }

    public void setResearchTasks(List <ResearchFormTasks> researchTasks) {
        this.researchTasks = researchTasks;
    }
}
