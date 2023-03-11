package io.github.adainish.researchtasks.obj;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.LineType;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBuilder;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import info.pixelmon.repack.org.spongepowered.CommentedConfigurationNode;
import info.pixelmon.repack.org.spongepowered.serialize.SerializationException;
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.conf.GUIConfig;
import io.github.adainish.researchtasks.conf.LanguageConfig;
import io.github.adainish.researchtasks.enumerations.TaskTypes;
import io.github.adainish.researchtasks.obj.research.*;
import io.github.adainish.researchtasks.storage.PlayerStorage;
import io.github.adainish.researchtasks.util.Util;
import io.leangen.geantyref.TypeToken;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

import static io.github.adainish.researchtasks.util.Util.underScoreReformattedString;

public class ResearchDex {

    public HashMap <String, ResearchPokemon> researchPokemonList = new HashMap <>();

    public ResearchDex()
    {

    }

    public void loadFromConfig()
    {
        CommentedConfigurationNode node = Config.getConfig().get().node("Pokemon");
        Map <Object, CommentedConfigurationNode> nodeMap = node.childrenMap();
        for (Object obj: nodeMap.keySet()) {
            if (obj == null) {
                ResearchTasks.log.error("OBJ Null while generating Pokemon Research Info");
                continue;
            }
            String nodestring = obj.toString();

            ResearchPokemon dropPokemon = new ResearchPokemon(nodestring);
            researchPokemonList.put(dropPokemon.getPokemonSpeciesName(), dropPokemon);
        }
    }

    public void syncTasks()
    {
        researchPokemonList.forEach((key, value) -> {
            value.sync();
            researchPokemonList.put(key, value);
        });

    }

    public void update(Player player, Pokemon pokemon, String tasktype, String actiontype)
    {
        try {
            if (this.researchPokemonList.containsKey(pokemon.getSpecies().getName()))
            {
                if (this.researchPokemonList.get(pokemon.getSpecies().getName()).formsList.containsKey(pokemon.getForm().getName()))
                {
                    this.researchPokemonList.get(pokemon.getSpecies().getName()).formsList.get(pokemon.getForm().getName()).researchTasks.forEach(t -> {
                        for (ResearchTask r:t.researchTasks) {
                            if (!r.getTaskActionType().equalsIgnoreCase(actiontype))
                                continue;
                            if (!r.getTaskType().equalsIgnoreCase(tasktype))
                                continue;
                            if (!r.completed())
                            {
                                r.increaseCounter();
                            }
                            if (!r.isPointsRedeemed()) {
                                if (r.complete()) {
                                    this.researchPokemonList.get(pokemon.getSpecies().getName()).formsList.get(pokemon.getForm().getName()).increasePoints(r.getPoints());
                                    player.sendMessage(LanguageConfig.getConfig().get().node("Messages", "Research", "ResearchPoints").getString()
                                            .replace("%points%", String.valueOf(r.getPoints()))
                                            .replace("%pokemon%", pokemon.getSpecies().getName())
                                            .replace("%form%", pokemon.getForm().getName())
                                    );
                                }
                            }
                        }
                    });
                    cache(player);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ItemStack getStackFromTask(TaskTypes taskType)
    {
        ItemStack itemStack;
        switch (taskType)
        {
            case Evolved:
            {
                itemStack = new ItemStack(PixelmonItems.ever_stone);
                break;
            }
            case Physical_Move_Used:
            {
                itemStack = new ItemStack(PixelmonItems.ruby_sword);
                break;
            }
            case Special_Move_Used:
            {
                itemStack = new ItemStack(PixelmonItems.crystal_sword);
                break;
            }
            case Status_Move_Used:
            {
                itemStack = new ItemStack(PixelmonItems.star_piece);
                break;
            }
            case Breed:
            {
                itemStack = new ItemStack(PixelmonItems.destiny_knot);
                break;
            }
            case Caught:
            {
                itemStack = new ItemStack(PixelmonItems.poke_ball);
                break;
            }
            case Hatched:
            {
                itemStack = new ItemStack(PixelmonItems.lucky_egg);
                break;
            }
            case Defeated:
            {
                itemStack = new ItemStack(PixelmonItems.power_weight);
                break;
            }
            default:
            {
                itemStack = new ItemStack(Items.PAPER);
                break;
            }
        }
        return itemStack;
    }

    public List<ResearchPokemon> sortedList()
    {
        List <ResearchPokemon> list = new ArrayList <>(researchPokemonList.values());
        list.sort(Comparator.comparing(ResearchPokemon::getDexNumber));
        return list;
    }

    public Pokemon getPokemonFromDex(String name)
    {
        return PokemonBuilder.builder().species(name).build();
    }

    public void open(ServerPlayerEntity playerEntity)
    {
        UIManager.openUIForcefully(playerEntity, MainMenu());
    }

    //dynamic species
    public List <Button> speciesButtonList()
    {
        String pokemonColourCode = "&";
        if (LanguageConfig.getConfig().get().node("MainMenu", "PokemonColourCode").getString() != null)
            pokemonColourCode = pokemonColourCode + LanguageConfig.getConfig().get().node("MainMenu", "PokemonColourCode").getString();
        else pokemonColourCode = "&b";
        List<Button> buttons = new ArrayList <>();
        List<String> pokemonLore = new ArrayList <>();
        try {
            pokemonLore = GUIConfig.getConfig().get().node("MainMenu", "PokemonLore").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            pokemonLore = new ArrayList <>();
        }
        for (ResearchPokemon researchPokemon:sortedList()) {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(pokemonColourCode + researchPokemon.getPokemonSpeciesName()))
                    .lore(Util.formattedArrayList(pokemonLore))
                    .display(SpriteItemHelper.getPhoto(getPokemonFromDex(researchPokemon.getPokemonSpeciesName())))
                    .onClick(b ->
                    {
                        UIManager.openUIForcefully(b.getPlayer(), FormsMenu(researchPokemon));
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }
    //dynamic form
    public List<Button> formButtonList(ResearchPokemon researchPokemon)
    {
        List<Button> buttons = new ArrayList <>();
        for (ResearchForm researchForm:researchPokemon.getFormsList().values()) {
            String title = LanguageConfig.getConfig().get().node("GUI", "FormTitle").getString().replace("%species%", researchForm.species).replace("%form%", researchForm.getForm());
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(title))
                    .display(SpriteItemHelper.getPhoto(researchForm.getPokemon()))
                    .onClick(b -> {
                        UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm));
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }


    //dynamic form tasks
    public List<Button> formTasksButtonList(ResearchForm form)
    {
        List<Button> buttons = new ArrayList <>();
        for (ResearchFormTasks formTask:form.getResearchTasks()) {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "FormTaskTitle").getString()
                            .replace("%species%", formTask.species)
                            .replace("%form%", formTask.form)
                            .replace("%task%", underScoreReformattedString(formTask.taskType))))
                    .display(getStackFromTask(TaskTypes.valueOf(formTask.taskType)))
                    .onClick(b -> {
                        UIManager.openUIForcefully(b.getPlayer(), TasksMenu(form, formTask));
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }

    public GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Blocks.GRAY_STAINED_GLASS_PANE, 1))
                .build();
    }


    //dynamic specific tasks
    public List<Button> taskButtonList(ResearchFormTasks formTasks)
    {
        List<Button> buttons = new ArrayList <>();
        for (ResearchTask researchTask:formTasks.researchTasks) {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "TaskTitle").getString()
                            .replace("%progress%", String.valueOf(researchTask.getTaskProgress()))
                            .replace("%count%", String.valueOf(researchTask.getTaskCount()))
                            .replace("%totalleft%", String.valueOf(researchTask.getTaskCount()))
                            .replace("%species%", researchTask.getSpecies())
                            .replace("%form%", researchTask.getForm())
                            .replace("%actiontype%", Util.underScoreReformattedString(researchTask.getTaskActionType()))
                            .replace("%tasktype%", Util.underScoreReformattedString(researchTask.getTaskType()))
                    ))
                    .display(getStackFromTask(TaskTypes.valueOf(researchTask.getTaskType())))
                    .build();
            buttons.add(button);
        }
        return buttons;
    }

    //Main Menu using species buttons

    public LinkedPage MainMenu()
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(PixelmonItems.trade_holder_left))
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "PreviousButtonTitle").getString()))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(PixelmonItems.trade_holder_right))
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "NextButtonTitle").getString()))
                .linkType(LinkType.Next)
                .build();


        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .rectangle(1, 1, 3, 7, placeHolderButton)
                .set(GUIConfig.getConfig().get().node("MainMenu", "PreviousButtonPosition").getInt(), previous)
                .set(GUIConfig.getConfig().get().node("MainMenu", "NextButtonPosition").getInt(), next)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, speciesButtonList(), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("MainMenu", "Title").getString())).template(template));
    }

    public LinkedPage FormsMenu(ResearchPokemon researchPokemon)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(GUIConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu());
                })
                .build();
        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(GUIConfig.getConfig().get().node("FormsMenu", "BackButtonPosition").getInt(), back)
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formButtonList(researchPokemon), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("FormsMenu", "Title").getString())).template(template));
    }

    public LinkedPage ResearchFormsTasksMenu(ResearchForm researchForm)
    {
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm));
                })
                .build();

        PlaceholderButton placeHolderButton = new PlaceholderButton();

        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(GUIConfig.getConfig().get().node("ResearchFormTaskTypeMenu", "BackButtonPosition").getInt(), back)
                .rectangle(1, 1, 2, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formTasksButtonList(researchForm), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("ResearchFormTaskTypeMenuMenu", "Title").getString())).template(template));
    }

    public LinkedPage TasksMenu(ResearchForm researchForm, ResearchFormTasks researchFormTasks)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), ResearchFormsTasksMenu(researchForm));
                })
                .build();
        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(GUIConfig.getConfig().get().node("TasksMenu", "BackButtonPosition").getInt(), back)
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, taskButtonList(researchFormTasks), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("TasksMenu", "Title").getString())).template(template));
    }

    //level buttons
    public List<Button> formLevelButtonList(ResearchForm form)
    {
        List<Button> buttons = new ArrayList <>();
        for (ResearchLevel rl:form.researchLevels) {

            List<String> lore = new ArrayList <>();
            if (rl.completed(form.progressPoints))
            {
                if (rl.claimed())
                {
                    lore.add(LanguageConfig.getConfig().get().node("GUI", "RewardClaimedLore").getString());
                } else {
                    lore.add(LanguageConfig.getConfig().get().node("GUI", "RewardAvailableLore").getString());
                }
            } else {
                lore.add(LanguageConfig.getConfig().get().node("GUI", "RewardNotReadyLore").getString());
            }

            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "LevelTitleLore").getString()
                            .replace("%level%", String.valueOf(rl.getLevel()))
                            .replace("%currentpoints%", String.valueOf(form.progressPoints)))
                            .replace("%pointsneeded%", String.valueOf(rl.getRequiredPoints()))
                    )
                    .display(new ItemStack(Items.EXPERIENCE_BOTTLE))
                    .lore(Util.formattedArrayList(lore))
                    .onClick(b -> {
                        if (!rl.claimed()) {
                            if (rl.completed(form.progressPoints)) {
                                Player player = PlayerStorage.getPlayer(b.getPlayer().getUniqueID());
                                if (player != null) {
                                    rl.handoutRewards(b.getPlayer().getUniqueID());
                                    if (rl.handoutRewards(b.getPlayer().getUniqueID()))
                                    {
                                        rl.setClaimed(true);
                                        save(player);
                                    } else {
                                        player.sendMessage(LanguageConfig.getConfig().get().node("GUI", "FailedRewards").getString());
                                    }

                                } else {
                                    ResearchTasks.log.error("Could not hand out rewards for %p% with uuid: %uuid%, player data could not be found"
                                            .replace("%p%", b.getPlayer().getName().getUnformattedComponentText())
                                            .replace("%uuid%", String.valueOf(b.getPlayer().getUniqueID()))
                                    );
                                }
                            }
                        }
                        UIManager.closeUI(b.getPlayer());
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }

    public void cache(Player player)
    {
        player.researchDex = this;
        player.updateCache();
    }

    public void save(Player player)
    {
        player.researchDex = this;
        player.savePlayer();
    }

    //select page
    public GooeyPage selectOptionMenu(ResearchForm researchForm)
    {
        Template template = null;
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu());
                })
                .build();
        GooeyButton levels = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "ViewResearchLevelTitle").getString()))
                .display(new ItemStack(Items.EXPERIENCE_BOTTLE))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), ResearchFormsLevelMenu(researchForm));
                })
                .build();
        GooeyButton forms = GooeyButton.builder().display(new ItemStack(PixelmonItems.rainbow_badge))
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "ViewResearchTaskTitle").getString()))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), ResearchFormsTasksMenu(researchForm));
                })
                .build();
        template = ChestTemplate.builder(1)
                .set(GUIConfig.getConfig().get().node("OptionMenu", "LevelsButtonPosition").getInt(), levels)
                .set(GUIConfig.getConfig().get().node("OptionMenu", "BackButtonPosition").getInt(), back)
                .set(GUIConfig.getConfig().get().node("OptionMenu", "FormsButtonPosition").getInt(), forms)
                .fill(filler())
                .build();
        return GooeyPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("OptionMenu", "Title").getString())).template(template).build();
    }
    //level gui
    public LinkedPage ResearchFormsLevelMenu(ResearchForm researchForm)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm));
                })
                .build();

        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .set(GUIConfig.getConfig().get().node("LevelsMenu", "BackButtonPosition").getInt(), back)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formLevelButtonList(researchForm), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("LevelsMenu", "Title").getString())).template(template));
    }

}
