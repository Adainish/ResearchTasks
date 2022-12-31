package io.github.adainish.researchtasks.obj;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;

import java.util.ArrayList;
import java.util.List;

public class ResearchForm {
    public Stats form;


    public ResearchForm(Species species, String formID)
    {
        if (species.getForm(formID) != null)
            this.form = species.getForm(formID);
        else  this.form = species.getDefaultForm();

    }

    public void loadLevels()
    {

    }



}
