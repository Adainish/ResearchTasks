package io.github.adainish.researchtasks;


import io.github.adainish.researchtasks.conf.Config;
import io.github.adainish.researchtasks.conf.RewardConfig;
import io.github.adainish.researchtasks.listener.PlayerListener;
import io.github.adainish.researchtasks.registry.RewardRegistry;
import io.github.adainish.researchtasks.wrapper.PermissionWrapper;
import io.github.adainish.researchtasks.wrapper.ResearchWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


@Mod("researchtasks")
public class ResearchTasks {
    public static ResearchTasks instance;
    public static final String MOD_NAME = "Research Tasks";
    public static final String VERSION = "1.0.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2022";

    public static MinecraftServer server;

    public static File configDir;
    public static File playerStorageDir;
    public static File storageDir;

    public static final Logger log = LogManager.getLogger(MOD_NAME);
    public static PermissionWrapper permissionWrapper;
    public static ResearchWrapper researchWrapper;

    public static RewardRegistry rewardRegistry;

    public ResearchTasks() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        initDirs();
    }

    @SubscribeEvent
    public void onCommandRegistry(RegisterCommandsEvent event)
    {
        permissionWrapper = new PermissionWrapper();
    }


    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event)
    {
        log.info("Finalising set up");
        server = ServerLifecycleHooks.getCurrentServer();
        setupConfigs();
        loadConfigs();
        MinecraftForge.EVENT_BUS.register(new PlayerListener());
        researchWrapper = new ResearchWrapper();
        rewardRegistry = new RewardRegistry();
    }

    public void initDirs() {
        log.log(Level.WARN, "Setting up Storage Paths and Directories for ResearchTasks");
        configDir = ((new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toString())));
        configDir.mkdir();
        storageDir = new File(configDir + "/ResearchTasks/Storage/");
        storageDir.mkdirs();
        playerStorageDir = new File(storageDir + "/PlayerData/");
        playerStorageDir.mkdirs();
    }

    public void reload()
    {
        log.warn("Reload Requested, This might take a moment! Prepare for incoming lag!");
        setupConfigs();
        loadConfigs();
    }


    public void setupConfigs()
    {
        log.warn("Setting up configs");
        Config.getConfig().setup();
        RewardConfig.getConfig().setup();
    }

    public void loadConfigs()
    {
        log.warn("Loading Configs");
        Config.getConfig().load();
        RewardConfig.getConfig().load();
    }

}
