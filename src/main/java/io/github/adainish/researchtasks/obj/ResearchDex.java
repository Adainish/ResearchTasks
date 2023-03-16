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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
                Item item = PixelmonItems.ever_stone;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "EvolvedButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Physical_Move_Used:
            {
                Item item = PixelmonItems.ruby_sword;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "PhysicalMovesButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Special_Move_Used:
            {
                Item item = PixelmonItems.crystal_sword;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "SpecialMovesButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Status_Move_Used:
            {
                Item item = PixelmonItems.star_piece;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "StatusMovesButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Breed:
            {
                Item item = PixelmonItems.destiny_knot;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "BreedButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Caught:
            {
                Item item = PixelmonItems.poke_ball;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "CaughtButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Hatched:
            {
                Item item = PixelmonItems.lucky_egg;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "HatchedButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
                break;
            }
            case Defeated:
            {
                Item item = PixelmonItems.power_weight;
                String parsed = GUIConfig.getConfig().get().node("Buttons", "DefeatedButton").getString();
                if (parsed != null && Util.getItemFromString(parsed) != null)
                    item = Util.getItemFromString(parsed);
                itemStack = new ItemStack(item);
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
        UIManager.openUIForcefully(playerEntity, MainMenu(defaultRef()));
    }

    public boolean isInGen(int dex, AtomicReference <String> argRef)
    {
        boolean bool = false;
        switch (argRef.get()) {
            case "Gen1": {
                if (dex > 0 && dex <= 151)
                    bool = true;
                break;
            }
            case "Gen2": {
                if (dex > 151 && dex <= 251)
                    bool = true;
                break;
            }
            case "Gen3": {
                if (dex > 251 && dex <= 386)
                    bool = true;
                break;
            }
            case "Gen4": {
                if (dex > 386 && dex <= 493)
                    bool = true;
                break;
            }
            case "Gen5": {
                if (dex > 493 && dex <= 649)
                    bool = true;
                break;
            }
            case "Gen6": {
                if (dex > 649 && dex <= 721)
                    bool = true;
                break;
            }
            case "Gen7": {
                if (dex > 721 && dex <= 809)
                    bool = true;
                break;
            }
            case "Gen8": {
                if (dex > 809 && dex <= 905)
                    bool = true;
                break;
            }
            case "Gen9": {
                if (dex > 905 && dex <= 1010)
                    bool = true;
                break;
            }
            case "Default": {
                bool = true;
                break;
            }
            default: {
                bool = false;
            }
        }
        return bool;
    }

    public List <Button> speciesSortableButtonList(AtomicReference <String> argRef)
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
            if (isInGen(researchPokemon.dexNumber, argRef)) {
                GooeyButton button = GooeyButton.builder()
                        .title(Util.formattedString(pokemonColourCode + researchPokemon.getPokemonSpeciesName()))
                        .lore(Util.formattedArrayList(pokemonLore))
                        .display(SpriteItemHelper.getPhoto(getPokemonFromDex(researchPokemon.getPokemonSpeciesName())))
                        .onClick(b ->
                                UIManager.openUIForcefully(b.getPlayer(), FormsMenu(researchPokemon)))
                        .build();
                buttons.add(button);
            }
        }

        return buttons;
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
                    .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm)))
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
                    .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), TasksMenu(form, formTask)))
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

    public ItemStack getTasksStack()
    {
        Item i = PixelmonItems.rainbow_badge;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "ResearchTasksButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
    }

    public ItemStack getLevelStack()
    {
        Item i = Items.EXPERIENCE_BOTTLE;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "ResearchLevelButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
    }

    public ItemStack getPreviousButtonStack()
    {
        Item i = PixelmonItems.trade_holder_left;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "PreviousPageButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
    }

    public ItemStack getNextButtonStack()
    {
        Item i = PixelmonItems.trade_holder_right;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "NextPageButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
    }

    public ItemStack getBackButtonStack()
    {
        Item i = PixelmonItems.eject_button;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "BackButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
    }

    public ItemStack getFilterButtonStack()
    {
        Item i = Items.PURPLE_DYE;

        String parsed = GUIConfig.getConfig().get().node("Buttons", "GenerationFilterButton").getString();
        if (parsed != null && Util.getItemFromString(parsed) != null)
            i = Util.getItemFromString(parsed);

        return new ItemStack(i);
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

    public LinkedPage MainMenu(AtomicReference <String> type)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(getPreviousButtonStack())
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "PreviousButtonTitle").getString()))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(getNextButtonStack())
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "NextButtonTitle").getString()))
                .linkType(LinkType.Next)
                .build();

        String typeArgColourCode = "&";
        if (LanguageConfig.getConfig().get().node("MainMenu", "FilterButtonColourCode").getString() != null)
            typeArgColourCode = typeArgColourCode + LanguageConfig.getConfig().get().node("MainMenu", "FilterButtonColourCode").getString();
        else typeArgColourCode = "&7";

        GooeyButton typeArg = GooeyButton.builder()
                .display(getFilterButtonStack())
                .title(Util.formattedString(typeArgColourCode + "%type%".replaceAll("%type%", type.get())))
                .lore(Util.formattedArrayList(Arrays.asList(LanguageConfig.getConfig().get().node("MainMenu", "FilterButtonLore").getString())))
                .onClick(buttonAction -> {
                    switch (type.get())
                    {
                        case "Default":
                        {
                            type.set("Gen1");
                            break;
                        }
                        case "Gen1":
                        {
                            type.set("Gen2");
                            break;
                        }
                        case "Gen2":
                        {
                            type.set("Gen3");
                            break;
                        }
                        case "Gen3":
                        {
                            type.set("Gen4");
                            break;
                        }
                        case "Gen4":
                        {
                            type.set("Gen5");
                            break;
                        }
                        case "Gen5":
                        {
                            type.set("Gen6");
                            break;
                        }
                        case "Gen6":
                        {
                            type.set("Gen7");
                            break;
                        }
                        case "Gen7":
                        {
                            type.set("Gen8");
                            break;
                        }
                        case "Gen8":
                        {
                            type.set("Gen9");
                            break;
                        }
                        case "Gen9":
                        {
                            type.set("Default");
                            break;
                        }
                    }
                    UIManager.openUIForcefully(buttonAction.getPlayer(),  MainMenu(type));
                })
                .build();

        Template template = null;
        template = ChestTemplate.builder(5)
                .border(0, 0, 5, 9, filler())
                .rectangle(1, 1, 3, 7, placeHolderButton)
                .set(GUIConfig.getConfig().get().node("MainMenu", "FilterButtonPosition").getInt(), typeArg)
                .set(GUIConfig.getConfig().get().node("MainMenu", "PreviousButtonPosition").getInt(), previous)
                .set(GUIConfig.getConfig().get().node("MainMenu", "NextButtonPosition").getInt(), next)
                .build();

        return PaginationHelper.createPagesFromPlaceholders(template, speciesSortableButtonList(type), LinkedPage.builder().title(Util.formattedString(GUIConfig.getConfig().get().node("MainMenu", "Title").getString())).template(template));
    }

    public AtomicReference<String> defaultRef()
    {
        return new AtomicReference<>("Default");
    }

    public LinkedPage FormsMenu(ResearchPokemon researchPokemon)
    {
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "BackButtonTitle").getString()))
                .display(getBackButtonStack())
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), MainMenu(defaultRef()));
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
                .display(getBackButtonStack())
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm)))
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
                .display(getBackButtonStack())
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), ResearchFormsTasksMenu(researchForm)))
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
                    .display(getLevelStack())
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
                .display(getBackButtonStack())
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), MainMenu(defaultRef())))
                .build();
        GooeyButton levels = GooeyButton.builder()
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "ViewResearchLevelTitle").getString()))
                .display(getLevelStack())
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), ResearchFormsLevelMenu(researchForm)))
                .build();
        GooeyButton forms = GooeyButton.builder()
                .display(getTasksStack())
                .title(Util.formattedString(LanguageConfig.getConfig().get().node("GUI", "ViewResearchTaskTitle").getString()))
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), ResearchFormsTasksMenu(researchForm)))
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
                .display(getBackButtonStack())
                .onClick(b -> UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(researchForm)))
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
