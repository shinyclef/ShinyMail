package com.hotmail.shinyclef.shinymail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Author: Shinyclef
 * Date: 13/06/12
 * Time: 7:37 PM
 */

public class CmdExecutor implements CommandExecutor
{
    private ShinyMail plugin;

    public CmdExecutor (ShinyMail plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        boolean returntype = false;

        if (command.getName().equalsIgnoreCase("mail"))
        {
            //make sure there's at least three arga
            if (args.length < 1)
                return false;

            if (args[0].equalsIgnoreCase("to"))
            {
                //make sure there's at least three arga
                if (args.length < 3)
                    return false;
                returntype = plugin.getMailHandler().SendMail((Player) sender, args);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("check"))
            {
                returntype = plugin.getMailHandler().CheckMail((Player) sender, false);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("read"))
            {
                returntype = plugin.getMailHandler().ReadMail((Player) sender, false);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("delete"))
            {
                //make sure there's at least two arga
                if (args.length < 2)
                    return false;
                returntype = plugin.getMailHandler().DeleteMail((Player) sender, args);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("help"))
            {
                returntype = plugin.getMailHandler().Help((Player) sender);
                return returntype;
            }
        }

         // ---------- Admin Commands ---------- //

        if (command.getName().equalsIgnoreCase("mailadmin"))
        {
            //make sure there's at least 1 arg
            if (args.length < 1)
                return false;

            if (args[0].equalsIgnoreCase("help"))
            {
                returntype = plugin.getMailHandler().AdminHelp((Player) sender);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("check"))
            {
                //check args length (check [playername])
                if (args.length < 2)
                    return false;
                returntype = plugin.getMailHandler().AdminCheckMail((Player) sender, args[1]);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("read"))
            {
                //check args length (read [playername])
                if (args.length < 2)
                    return false;

                returntype = plugin.getMailHandler().AdminReadMail((Player) sender, args[1], false);
                return returntype;
            }

            if (args[0].equalsIgnoreCase("delete"))
            {
                //check args length (delete all/# [playername])
                if (args.length < 3)
                    return false;

                returntype = plugin.getMailHandler().AdminDeleteMail((Player) sender, args);
                return returntype;
            }

         }
    return false;
    }
}
