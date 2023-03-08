package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.obj.Player;
import io.github.adainish.researchtasks.obj.ResearchDex;

import java.util.UUID;

public class ResearchTask {
    private int level;
    private String species;
    private String form;

    private String taskType;
    private String taskDisplay;
    private String taskActionType;
    private int taskCount = 0;
    private int taskProgress = 0;
    private int points = 1;
    private boolean pointsRedeemed = false;
    private boolean enabled = false;

    public ResearchTask(int level, String species, String form, String taskType) {
        this.setLevel(level);
        this.setSpecies(species);
        this.setForm(form);
        this.setTaskType(taskType);
    }

    public boolean completed() {
        return getTaskCount() <= getTaskProgress();
    }

    public int left() {
        return getTaskCount() - getTaskProgress();
    }

    public void increaseCounter()
    {
        this.taskProgress += 1;
    }

    public boolean complete() {
        if (!completed())
            return false;
        if (isPointsRedeemed())
            return false;
        this.setPointsRedeemed(true);
        return true;
    }


    public void addPoints(UUID uuid)
    {
        //add points to form
        if (PixelmonSpecies.get(species).isPresent() && PixelmonSpecies.get(species).get().getValue().isPresent())
        ResearchTasks.researchWrapper.playerHashMap.get(uuid).researchDex.researchPokemonList.get(PixelmonSpecies.get(species).get().getValue().get().getName()).getFormsList().get(form).increasePoints(points);
    }

    public void sync()
    {
        this.setEnabled(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Tasks", getTaskType(), String.valueOf(getLevel()), "Enabled").getBoolean());
        this.setTaskActionType(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Tasks", getTaskType(), String.valueOf(getLevel()), "ActionKey").getString());
        this.setTaskCount(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Tasks", getTaskType(), String.valueOf(getLevel()), "Requirement").getInt());
        this.setPoints(Config.getConfig().get().node("Pokemon", getSpecies(), getForm(), "Tasks", getTaskType(), String.valueOf(getLevel()), "Points").getInt());
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskDisplay() {
        return taskDisplay;
    }

    public void setTaskDisplay(String taskDisplay) {
        this.taskDisplay = taskDisplay;
    }

    public String getTaskActionType() {
        return taskActionType;
    }

    public void setTaskActionType(String taskActionType) {
        this.taskActionType = taskActionType;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskProgress() {
        return taskProgress;
    }

    public void setTaskProgress(int taskProgress) {
        this.taskProgress = taskProgress;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isPointsRedeemed() {
        return pointsRedeemed;
    }

    public void setPointsRedeemed(boolean pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
