package com.hotmail.shinyclef.shinymail;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Author: ShinyClef
 * Date: 14/06/12
 * Time: 3:45 AM
 */

public class EventListener implements Listener
{
    private ShinyMail plugin;

    public EventListener(ShinyMail plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void eventPlayerLogin(PlayerJoinEvent event)
    {
        plugin.getMailHandler().CheckMail(event.getPlayer(), true);
    }

    @EventHandler
    public void eventPlayerQuit(PlayerQuitEvent event)
    {
        plugin.getMailHandler().SweepMail(event.getPlayer());
    }
}
