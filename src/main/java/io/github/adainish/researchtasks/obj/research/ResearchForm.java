package io.github.adainish.researchtasks.obj.research;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResearchForm {
    public String species;
    public Stats form;
    public List<ResearchLevel> researchLevels = new ArrayList <>();
    public int progressPoints = 0;


    public ResearchForm(Species species, String formID)
    {
        this.species = species.getName();
        if (species.getForm(formID) != null)
            this.form = species.getForm(formID);
        else this.form = species.getDefaultForm();
        loadLevels();
    }

    public void loadLevels()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon", species, "");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Form Data");
                continue;
            }
            String nodestring = obj.toString();
        }
    }

    public void sync()
    {
        researchLevels.forEach(ResearchLevel::sync);
    }


}
