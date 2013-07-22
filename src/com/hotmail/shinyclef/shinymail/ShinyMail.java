package com.hotmail.shinyclef.shinymail;

import com.hotmail.shinyclef.shinybase.ShinyBase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Author: ShinyClef
 * Date: 13/06/12
 * Time: 7:24 PM
 */

public class ShinyMail extends JavaPlugin
{
    private CmdExecutor commandExecutor;
    private MailHandler mailhandler;
    private ShinyBase shinyBase;


    @Override
    public void onEnable()
    {
        Plugin p = Bukkit.getPluginManager().getPlugin("ShinyBase");
        if (p != null)
        {
            shinyBase = (ShinyBase) p;
        }

        commandExecutor = new CmdExecutor(this);
        mailhandler = new MailHandler(this, shinyBase.getShinyBaseAPI());
        getCommand("mail").setExecutor(commandExecutor);
        getCommand("mailadmin").setExecutor(commandExecutor);

        new EventListener(this);
    }

    @Override
    public void onDisable()
    {

    }

    public MailHandler getMailHandler()
    {
        return mailhandler;
    }
}
