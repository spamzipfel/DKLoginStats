package me.naim.DKLoginStats;
import java.io.IOException;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Chef
 * @date 22.03.2012
 * @brief Player Listener Implementation for the logger
 *
 * Logs player Kick and Login events;
 * Create a log file for each day
 */
public class StatsPlayerListener implements Listener 
{
    /// The logger we log our stuff to
    private static final Logger Stats = Logger.getLogger("DKLoginPlayerListener");
    
    /// The formatter we use to format a line
    private static final StatsLogFormatter Formatter = new StatsLogFormatter();
    
    /// The date/time format to use
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /// The path read from the config, where log files are stored
    private String Path = null;
    
    /// Last logging time to keep track of when to create a new file
    private Date LastLog = null;
    
    /// The file handle of the current file we log to 
    private FileHandler File = null;
    
    // Base Plugin class to get access to some server variables/methods
    protected DKLoginStats Base = null;
    
    /**
     * Ctor
     * @param base Base Plugin class
     */
    public StatsPlayerListener(DKLoginStats base)
    {
        Base = base;         
        LastLog = null;
        Stats.setLevel(Level.ALL); 
        
        UpdateLogger();
    }             
        
    /**
     * Overriden finalize to close the last file handle
     * @throws Throwable 
     */
    @Override
    protected void finalize() throws Throwable
    {                
        super.finalize(); 
        File.close();
    }
    
    /**
     * Updates the file handler, if the day has changed and we need a new file
     * @param logger The logger to hook
     * @throws IOException
     * @throws SecurityException 
     */
    private void SetFileHandler(Logger logger) 
            throws IOException, SecurityException
    {
        FileHandler fh = null;
                       
        /*
         * If the day has passed or we don't even have a file handler yet,
         * create a new one
         */
        if (DayHasPassed() || File == null)
        {
            // Get the path from the config
            Path = Base.getConfig().getString("loginstats.path");
            
            // Append '/' to the path
            if (!Path.endsWith("/"))
                Path += "/";
            
            // Add the file name to the path
            Path += StatsLogFormatter.GetDateTime(DATE_FORMAT) + ".log";
            
            // create the file handler
            fh = new FileHandler(Path, true);
            
            // Set the logger for the file handler
            fh.setFormatter(Formatter);
            
            // Append the file handler to the logger
            logger.addHandler(fh);
            
            // Did we already have a file handler?
            if (File != null)
            {
                // Remove it from the logger
                logger.removeHandler(File);
                // Close the old file
                File.close();
            }
            
            // Update the file handler
            File = fh;            
        }
        
        // Update the Date, we last logged something
        LastLog = new Date();
                
    }
    
    /**
     * Determine, whether a day has passed since our last log
     * @return true, if day has passed; false else
     */
    private boolean DayHasPassed()
    {
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar last = new GregorianCalendar();
        
        // We did not even log yet
        if (LastLog == null)
        {            
            return true;
        }
        
        now.setTime(new Date());
        last.setTime(LastLog);
        
        /*
         * If the day of month differs a day has passed
         * May be buggy if the server runs for 1 month without ANY logins...
         */
        return now.get(Calendar.DAY_OF_MONTH) != last.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * @brief Update the file handler on the logger
     * 
     * Updates the FileHandler on the logger;
     * If an exception is caught, it will be logged to the console and the 
     * plugin will be disabled.
     */        
    private void UpdateLogger()             
    {
        
        try
        {
            // Update the logger
            SetFileHandler(Stats);
        }
        catch ( IOException ex)
        {
            Base.Log("[DKLoginStats] Can not update logger!", Level.SEVERE);
            Base.Log(ex.getMessage(), Level.SEVERE);
            Base.getServer().getPluginManager().disablePlugin(Base);
        }
        catch ( SecurityException ex)
        {
            Base.Log("[DKLoginStats] Can not update logger!", Level.SEVERE);
            Base.Log(ex.getMessage(), Level.SEVERE);
            Base.getServer().getPluginManager().disablePlugin(Base);
        }
        
    }
    
    /**
     * Registered player kick event
     * @param event Event when a player gets kicked
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKick(final PlayerKickEvent event) 
    {    
        
        StringBuilder message = null;
        
        // Update the logger
        UpdateLogger();
        
        // If we do not want to log these events, return
        if (!Base.getConfig().getBoolean("loginstats.log.KICK_USER"))
        {
            return;
        }
        
        // Build the message
        message = new StringBuilder();        
        message.append(event.getPlayer().getName());
        message.append("(");
        message.append(event.getPlayer().getAddress().getHostString());
        message.append(")");
        message.append(": ");
        message.append(event.getEventName());
        message.append(" - ");
        message.append(event.getReason());    
        
        // Log the message as severe
        Stats.severe(message.toString());
    }  
    
    /**
     * Registered player Login event
     * @param event Event when player logs in
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event) 
    {    
        /// Log level
        Level logLevel = Level.INFO;
        
        /// the message
        StringBuilder message = new StringBuilder();
        
        /// do we log this?
        boolean log = true;
        
        /// do we override the original message with a new one?
        boolean override_message = false;
        
        /// The message a user gets
        String userMessage = "";
        
        // Update the logger
        UpdateLogger();
                
        // Fetch config settings
        log = Base.getConfig().getBoolean("loginstats.log." + event.getResult());
        override_message = Base.getConfig().getBoolean("loginstats.override_messages");
        userMessage = Base.getConfig().getString("loginstats.messages." + event.getResult());
        
        // if we override the message
        if (override_message == true)
        {
            // and the message out of the config is not empty or null
            if ( userMessage!= null && !userMessage.isEmpty())
            {
                // set the new message for this event
                event.setKickMessage(userMessage);
            }
        }        
        
        message.append(event.getPlayer().getName());
        message.append("(");
        message.append(event.getHostname());
        message.append(")");
        message.append(": ");
        message.append(event.getResult());
        message.append(" - ");
        message.append(event.getKickMessage());
                
        // Switch the log level depending on the event result
        switch (event.getResult())
        {
            case KICK_WHITELIST:
                // Player is not whitelisted
                logLevel = Level.WARNING;
                break;
            case ALLOWED:
                // Player is allowed, all ok
                logLevel = Level.INFO;
                break;
            case KICK_BANNED:
                // Player is banned on this server
                logLevel = Level.SEVERE;
                break;
            case KICK_FULL:
                // Server is full
                logLevel = Level.WARNING;
                break;
            case KICK_OTHER:
                // Some other reason why he got kicked                
                logLevel = Level.SEVERE;
                break;
            default:
                // Theoretical
                logLevel = Level.SEVERE;
                break;
        }
        
        // If we want to log this
        if (log)
        {
            // Log it
            Stats.log(logLevel, message.toString());
        }
        

    }                   
    

}
