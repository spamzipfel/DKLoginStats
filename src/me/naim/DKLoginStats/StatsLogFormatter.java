package me.naim.DKLoginStats;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;

/**
 * @author Chef
 * @date 22.03.2012
 * @brief Log Formatting Class
 *
 * Used as custom formatter for a LogRecord
 */
class StatsLogFormatter extends Formatter 
{
    private static final String TIME_FORMAT = "HH:mm:ss";
    
    /**
     * Overridden format method, to format a LogRecord
     * @param rec LogRecord to format
     * @return Formatted String of LogRecord log
     */
    @Override
    public String format(LogRecord rec) 
    {                    
        StringBuilder buf = new StringBuilder();
        
        buf.append(GetDateTime(TIME_FORMAT));
        buf.append(" [");
        buf.append(rec.getLevel());
        buf.append("] ");                   
        buf.append(rec.getMessage());
        buf.append("\r\n");
        
        return buf.toString();
    }

    /**
     * Returns the current date and time
     * @param format Date and Time format
     * @return Current Date and Time as String
     */
    public static String GetDateTime(String format)
    {
        java.text.SimpleDateFormat df 
                = new java.text.SimpleDateFormat(format);

        Date date = new Date();
        
        return df.format(date);
    }     

}