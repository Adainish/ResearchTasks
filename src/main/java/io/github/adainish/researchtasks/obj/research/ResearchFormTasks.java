package io.github.adainish.researchtasks.obj.research;

import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.enumerations.TaskTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResearchFormTasks
{
    public String species;
    public String form;
    public String taskType;

    public List<ResearchTask> researchTasks = new ArrayList <>();

    public ResearchFormTasks(String species, String form, String taskType)
    {
        this.species = species;
        this.form = form;
        this.taskType = taskType;
        loadResearchTasks();
    }

    public void loadResearchTasks()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", species, form,  "Tasks", taskType);
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();
            ResearchTask researchTask = new ResearchTask(Integer.parseInt(nodestring), species, form, taskType);
            researchTasks.add(researchTask);
        }
        researchTasks.sort(Comparator.comparing(ResearchTask::getLevel));
    }


    public void sync()
    {
        researchTasks.forEach(ResearchTask::sync);
    }

}
