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
import io.github.adainish.researchtasks.ResearchTasks;
import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.enumerations.TaskTypes;
import io.github.adainish.researchtasks.obj.research.*;
import io.github.adainish.researchtasks.storage.PlayerStorage;
import io.github.adainish.researchtasks.util.Util;
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
        loadFromConfig();
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
        if (researchPokemonList.containsKey(pokemon.getSpecies().getName()))
        {
            if (researchPokemonList.get(pokemon.getSpecies().getName()).formsList.containsKey(pokemon.getForm().getName()))
            {
                researchPokemonList.get(pokemon.getSpecies().getName()).formsList.get(pokemon.getForm().getName()).researchTasks.forEach(t -> {
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
                                researchPokemonList.get(pokemon.getSpecies().getName()).formsList.get(pokemon.getForm().getName()).increasePoints(r.getPoints());
                                player.sendMessage("&7You received &a%points% level points &7for &e%pokemon% %form%'s &7research level!"
                                        .replace("%points%", String.valueOf(r.getPoints()))
                                        .replace("%pokemon%", pokemon.getSpecies().getName())
                                        .replace("%form%", pokemon.getForm().getName())
                                );
                            }
                        }
                    }
                });
                save(player);
            }
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
        List<Button> buttons = new ArrayList <>();
        for (ResearchPokemon researchPokemon:sortedList()) {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(researchPokemon.getPokemonSpeciesName()))
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
            String title = "&5%species% %form%".replace("%species%", researchForm.species).replace("%form%", researchForm.getForm());
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
                    .title(Util.formattedString("%species% %form% %task%"
                            .replace("%species%", formTask.species)
                            .replace("%form%", formTask.form)
                            .replace("%task%", underScoreReformattedString(formTask.taskType))))
                    .display(getStackFromTask(TaskTypes.valueOf(formTask.taskType)))
                    .onClick(b -> {
                        UIManager.openUIForcefully(b.getPlayer(), TasksMenu(formTask));
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
                    .title(Util.formattedString("&b%species% &5%form% &e%actiontype% &a%tasktype% &f%progress% &7/ &f%count%"
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
                .title("Previous Page")
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(PixelmonItems.trade_holder_right))
                .title("Next Page")
                .linkType(LinkType.Next)
                .build();


        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .rectangle(1, 1, 3, 7, placeHolderButton)
                .set(0, 3, previous)
                .set(0, 5, next)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, speciesButtonList(), LinkedPage.builder().template(template));
    }

    public LinkedPage FormsMenu(ResearchPokemon researchPokemon)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&eClick to go back"))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu());
                })
                .build();
        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(0, 0, back)
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formButtonList(researchPokemon), LinkedPage.builder().template(template));
    }

    public LinkedPage ResearchFormsTasksMenu(ResearchForm researchForm)
    {
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&eClick to go back"))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm));
                })
                .build();

        PlaceholderButton placeHolderButton = new PlaceholderButton();

        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(0, 0, back)
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formTasksButtonList(researchForm), LinkedPage.builder().template(template));
    }

    public LinkedPage TasksMenu(ResearchFormTasks researchFormTasks)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&eClick to go back"))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu());
                })
                .build();
        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .set(0, 0, back)
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, taskButtonList(researchFormTasks), LinkedPage.builder().template(template));
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
                    lore.add("&4You've already claimed these rewards.");
                } else {
                    lore.add("&bYou can claim the rewards for this level!");
                }
            } else {
                lore.add("&7You need to obtain more points to claim the rewards for this level!");
            }

            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString("&bLevel %level% &e%currentpoints% &7/ &e%pointsneeded%"
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
                                    rl.setClaimed(true);
                                    save(player);
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
                .title(Util.formattedString("&eClick to go back"))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu());
                })
                .build();
        GooeyButton levels = GooeyButton.builder()
                .title(Util.formattedString("&bView Research Level"))
                .display(new ItemStack(Items.EXPERIENCE_BOTTLE))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), ResearchFormsLevelMenu(researchForm));
                })
                .build();
        GooeyButton forms = GooeyButton.builder().display(new ItemStack(PixelmonItems.rainbow_badge))
                .title(Util.formattedString("&aView Research Tasks"))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), ResearchFormsTasksMenu(researchForm));
                })
                .build();
        template = ChestTemplate.builder(1)
                .set(0, 3, levels)
                .set(0, 0, back)
                .set(0, 5, forms)
                .fill(filler())
                .build();
        return GooeyPage.builder().template(template).build();
    }
    //level gui
    public LinkedPage ResearchFormsLevelMenu(ResearchForm researchForm)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&eClick to go back"))
                .display(new ItemStack(PixelmonItems.eject_button))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm));
                })
                .build();

        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .line(LineType.HORIZONTAL, 1, 1, 7, placeHolderButton)
                .set(0, 0, back)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, formLevelButtonList(researchForm), LinkedPage.builder().template(template));
    }

}
