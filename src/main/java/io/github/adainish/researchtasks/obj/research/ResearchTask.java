package io.github.adainish.researchtasks.obj.research;

import io.github.adainish.researchtasks.enumerations.TaskTypes;

public class ResearchTask {

    public TaskTypes taskType;
    public String taskDisplay;
    public String taskActionType;
    public int taskCount = 0;
    public int taskProgress = 0;
    public boolean pointsRedeemed = false;

    public ResearchTask()
    {

    }

    public boolean completed()
    {
        return taskCount <= taskProgress;
    }

    public int left()
    {
        return taskCount - taskProgress;
    }

    public void addPoints()
    {}
}
