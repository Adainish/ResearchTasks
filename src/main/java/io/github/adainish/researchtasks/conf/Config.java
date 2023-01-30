package io.github.adainish.researchtasks.conf;

import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import info.pixelmon.repack.org.spongepowered.serialize.SerializationException;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.enumerations.TaskTypes;
import io.github.adainish.researchtasks.util.Util;

import java.util.Arrays;

public class Config extends Configurable
{
    private static Config config;

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void setup() {
        super.setup();
    }

    public void load() {
        super.load();
    }

    @Override
    public void populate() {
        try {

            this.get().node("Rewards", "Example", "Commands").set(Arrays.asList("broadcast %p% obtained a reward!"));
            this.get().node("Rewards", "Example", "GUIItem").set("minecraft:paper");
            this.get().node("Rewards", "Example", "GUITitle").set("&cAn Example Reward");
            this.get().node("Rewards", "Example", "GUILore").set(Arrays.asList("&cThis is a reward you can get!"));

            for (Species sp: PixelmonSpecies.getAll()) {
                if (sp.is(PixelmonSpecies.MISSINGNO.getValueUnsafe()))
                    continue;
                for (Stats form:sp.getForms()) {
                    for (TaskTypes taskType: TaskTypes.values()) {
                        for (int i = 0; i < 5; i++) {
                            switch (taskType)
                            {
                                case Status_Move_Used:
                                {
                                    String actionKey = Util.getRandomStatusAttack(sp, form);
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Enabled").set(!actionKey.isEmpty());
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "ActionKey").set(Util.getRandomStatusAttack(sp, form));
                                    break;
                                }
                                case Special_Move_Used:
                                {
                                    String actionKey = Util.getRandomSpecialAttack(sp, form);
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Enabled").set(!actionKey.isEmpty());
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "ActionKey").set(actionKey);
                                    break;
                                }
                                case Physical_Move_Used:
                                {
                                    String actionKey = Util.getRandomPhysicalAttack(sp, form);
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Enabled").set(!actionKey.isEmpty());
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "ActionKey").set(actionKey);
                                    break;
                                }
                                case Evolved:
                                {
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Enabled").set(Util.hasEvolution(form));
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "ActionKey").set("");
                                    break;
                                }
                                default:
                                {
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Enabled").set(true);
                                    this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "ActionKey").set("");
                                    break;
                                }
                            }
                            this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Requirement").set(1 + i * 2);
                            this.get().node("Pokemon", sp.getName(), form.getName(), "Tasks", taskType.name(), i, "Points").set((i + 10) / 3);

                            this.get().node("Pokemon", sp.getName(), form.getName(), "Levels", i, "RequiredPoints").set(i * 100);
                            this.get().node("Pokemon", sp.getName(), form.getName(), "Levels", i, "RewardIds").set(Arrays.asList(""));
                        }
                    }

                }
            }

        } catch (SerializationException e) {
            ResearchTasks.log.error(e);
        }
    }


    public String getConfigName() {
        return "config.yaml";
    }

    public Config()
    {
    }
}
