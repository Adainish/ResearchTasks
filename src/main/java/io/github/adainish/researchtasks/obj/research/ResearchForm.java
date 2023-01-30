package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.enumerations.TaskTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResearchForm {
    private String species;
    private Stats form;

    private List<ResearchFormTasks> researchTasks = new ArrayList <>();
    private List<ResearchLevel> researchLevels = new ArrayList <>();
    private int progressPoints = 0;


    public ResearchForm(Species species, String formID)
    {
        this.setSpecies(species.getName());
        if (species.getForm(formID) != null)
            this.setForm(species.getForm(formID));
        else this.setForm(species.getDefaultForm());
        loadTasks();
        loadLevels();
    }

    public void loadTasks()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", getSpecies(), getForm().getName(), "Tasks");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();

            TaskTypes taskType = TaskTypes.valueOf(nodestring);
            ResearchFormTasks researchFormTask = new ResearchFormTasks(species, form.getName(), taskType.name());
            getResearchTasks().add(researchFormTask);
        }
    }

    public void loadLevels()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", getSpecies(), getForm().getName(), "Levels");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();

            ResearchLevel researchLevel = new ResearchLevel(Integer.parseInt(nodestring), getSpecies(), getForm().getName());
            getResearchLevels().add(researchLevel);
        }
        getResearchLevels().sort(Comparator.comparing(ResearchLevel::getLevel));
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

    public Stats getForm() {
        return form;
    }

    public void setForm(Stats form) {
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
