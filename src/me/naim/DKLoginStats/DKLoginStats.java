package me.naim.DKLoginStats;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * @author Chef
 * @date 22.03.2012
 * @brief Brief class description
 *
 * Detailed class description
 */
public class DKLoginStats extends JavaPlugin {
    
    private static final Logger Log = Logger.getLogger("Minecraft");        
    private StatsPlayerListener playerListener = null;
    
    
    /**
     * JavaPlugin member called, when this plugin is started
     */
    @Override
    public void onEnable()
    {        
        // Register player chat event
        PluginManager pm = this.getServer().getPluginManager();
                
        // Check whether this plugin is enabled in the config
        if (getConfig().getBoolean("loginstats.enabled") == false)
        {
            // It is not, disable it
            pm.disablePlugin(this);
            return;
        }
        
        // Create the player listener that logs events
        playerListener =  new StatsPlayerListener(this);
        
        // register the player listener
        pm.registerEvents(playerListener, this);        
        
        // Tell console, we are ready
        Log.info("[DKLoginStats] enabled.");                
    }

    
    /**
     * JavaPlugin member called, when this plugin is started
     */    
    @Override
    public void onDisable(){
        Log.info("[DKLoginStats] disabled.");
    }                

    /**
     * Log a message as Info to the console
     * @param msg Message to write
     */       
    public void Log(String msg)
    {
        Log.info(msg);
    }        
    
    /**
     * Log a message with the specific level to the console
     * @param msg Message to write
     * @param level Log level
     */       
    public void Log(String msg, Level level)
    {
        Log.log(level, msg);
    }

    /**
     * Dummy main method to shut up NetBeans IDE when running the project
     */  
    public static void main(String[] args)
    {

    }    
}
