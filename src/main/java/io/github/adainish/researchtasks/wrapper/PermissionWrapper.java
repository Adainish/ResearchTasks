package io.github.adainish.researchtasks.wrapper;

import io.github.adainish.researchtasks.ResearchTasks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;

public class PermissionWrapper {
    public String adminPermission = "researchtasks.admin";
    public PermissionWrapper()
    {
        registerCommandPermission(adminPermission);
    }

    public void registerCommandPermission(String s) {
        if (s == null || s.isEmpty()) {
            ResearchTasks.log.log(Level.FATAL, "Trying to register a permission node failed, please check any configs for null/empty Configs");
            return;
        }
        PermissionAPI.registerNode(s, DefaultPermissionLevel.NONE, s);
    }

    public void registerCommandPermission(String s, String description) {
        if (s == null || s.isEmpty()) {
            ResearchTasks.log.log(Level.FATAL, "Trying to register a permission node failed, please check any configs for null/empty Configs");
            return;
        }
        PermissionAPI.registerNode(s, DefaultPermissionLevel.NONE, description);
    }

}
