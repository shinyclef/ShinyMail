package com.hotmail.shinyclef.shinymail;

import com.hotmail.shinyclef.shinybase.ShinyBaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: ShinyClef
 * Date: 15/06/12
 * Time: 12:26 AM
 */

public class MailHandler
{
    private ShinyMail plugin;
    private List<String> maillist;
    private ShinyBaseAPI shinyBaseAPI;

    public MailHandler(ShinyMail plugin, ShinyBaseAPI shinyBaseAPI) //constructor
    {
       this.plugin = plugin;
       this.shinyBaseAPI = shinyBaseAPI;
    }


    // ---------- Public Commands ---------- //

    public boolean SendMail(Player playersender, String[] args)
    {
        //set recipient vars
        String recipient = args[1].toLowerCase();

        //if player is online, send a message to sender and end.
        if (Bukkit.getServer().getOfflinePlayer(recipient).isOnline())
        {
            playersender.sendMessage(ChatColor.DARK_GREEN +
                    "Little Timmy the Mailman gives you a disappointed look.");
            playersender.sendMessage(ChatColor.AQUA + "<Little Timmy>" + ChatColor.WHITE +
                    "Please don't make me deliver this! " + Bukkit.getServer().getPlayer(recipient).getName() +
                    " is online! Just use /msg!");
            return true;
        }

        //if player has not played before on server
        //if (recipient.getLastPlayed() == 0)

        //if recipient does not exist
        if (!shinyBaseAPI.isExistingPlayer(recipient))
        {
            playersender.sendMessage(ChatColor.DARK_GREEN +
                    "Little Timmy checks his little notebook with a confused look.");
            playersender.sendMessage(ChatColor.AQUA +
                    "<Little Timmy>" + ChatColor.WHITE + " Sorry, I don't know who " + args[1] + " is!");
            return true;
        }

        //else - if player exists (player is offline check already done),
        //vars
        int i = 2;
        String sentence = "";

        //put all the args starting from the 2nd into string 'sentence'
        do
        {
            sentence = sentence + args[i] + " ";
            i++;
        }
        while (i < args.length);

        //remove the last white space from sentence
        sentence = sentence.substring(0, sentence.length() - 1);

        //message to player
        playersender.sendMessage(ChatColor.AQUA + "To " +
                args[1] + ": " + ChatColor.WHITE + sentence);
        playersender.sendMessage(ChatColor.DARK_GREEN +
                "Little Timmy excitedly takes your letter and hurries to the post office!");

        //add timestamp and sender name to sentence
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy|HH:mm");
        Date date = new Date();
        sentence = dateFormat.format(date) + "|" + playersender.getName() + "|" + sentence;

        //copy the list from config
        List<String> maillist = plugin.getConfig().getStringList("Inbox." + args[1]);
        if (maillist == null)
        {
            maillist = new ArrayList<String>(); //if the copy is null, initialize a new arraylist
        }

        //add the new sentence to the copy of the list
        maillist.add(sentence);

        //overwrite config list with updated copy of list
        plugin.getConfig().set("Inbox." + recipient, maillist);
        plugin.saveConfig();
        plugin.reloadConfig();

        return true;
    }

    public boolean CheckMail(Player playersender, boolean isLoggingIn)
    {
        String senderlcasename = playersender.getName().toLowerCase();

        //if player is logging in, reload config
        if (isLoggingIn)
        {
            plugin.reloadConfig();
        }

        // get a copy of config list
        maillist = plugin.getConfig().getStringList("Inbox." + senderlcasename);

        //if the list is null or empty, notify player. Don't do this if player is logging in.
        if ((maillist == null || maillist.isEmpty()) && !isLoggingIn)
        {
            playersender.sendMessage(ChatColor.DARK_GREEN +
                    "Little Timmy the Mailman ruffles through his mail bag with a concerned look.");
            playersender.sendMessage(ChatColor.AQUA + "<Little Timmy>" + ChatColor.WHITE +
                    " Sorry " + playersender.getName() + ", but you don't seem to have any mail!");
            return true;
        }

        if (maillist.size() == 1)
        {
            playersender.sendMessage(ChatColor.DARK_GREEN +
                    "Little Timmy the Mailman runs up to you looking very pleased with himself.");
            playersender.sendMessage(ChatColor.AQUA +
                    "<Little Timmy>" + ChatColor.WHITE + " Hey " + playersender.getName() +
                    "! I have a letter for you! Read it with \"/mail read\"!");
            return true;
        }

        if (maillist.size() > 1)
        {
            playersender.sendMessage(ChatColor.DARK_GREEN + "Little Timmy the Mailman runs up to you excitedly.");
            playersender.sendMessage(ChatColor.AQUA + "<Little Timmy>" + ChatColor.WHITE +
                    " Hey " + playersender.getName() + "! I have " + maillist.size() +
                    " letters for you! Read them with \"/mail read\"!");
            return true;
        }

        return false;
    }

    public boolean ReadMail(Player playersender, boolean isDeleting)
    {
        String senderlcasename = playersender.getName().toLowerCase();

        //make a copy of all the strings for player name
        List<String> maillist = plugin.getConfig().getStringList("Inbox." + senderlcasename);

        //if the list is null or empty, notify player
        if ((maillist == null || maillist.isEmpty()) && !isDeleting)
        {
            //if issuing from /read
            playersender.sendMessage(ChatColor.DARK_GREEN +
                    "Little Timmy the Mailman looks through his mail bag with a concerned look.");
            playersender.sendMessage(ChatColor.AQUA +
                    "<Little Timmy>" + ChatColor.WHITE + " Sorry " + playersender.getName() +
                    ", but it looks like you don't have any mail!");
            return true;
        }

        //if (args[1].equalsIgnoreCase("all"))
        //{
            int mailnumber = 1;
            //for each message in the player's inbox
            for (String nextmail : maillist)
            {
                //divide the string into array 'item' on char "|" (date/time, sender, and msg)
                String[] item = nextmail.split("\\|");
                //throw away the /yyyy from date
                item[0] = item[0].substring(0,5);
                //send mail to player with mailnumber
                playersender.sendMessage(ChatColor.RED + "" + mailnumber + ". " + ChatColor.AQUA + item[0] +
                        " " + item[1] + " <" + item[2] + "> " + ChatColor.WHITE + item[3]);
                mailnumber++;
            }
            maillist.clear();
            return true;
        //}

        /*if (args[1].equalsIgnoreCase("next"))
        {
            //get the items from the next String in list 'maillist' (date/time, sender and msg)
            String[] item = maillist.get(0).split("\\|");
            //send mail to player
            playersender.sendMessage(ChatColor.AQUA + item[0] + " <" + item[1] + "> " + ChatColor.WHITE + item[2]);
            return true;
        }*/
        //return false;
    }

    public boolean DeleteMail(Player playersender, String[] args)
    {
        String senderlcasename = playersender.getName().toLowerCase();

        // get a copy of config list
        maillist = plugin.getConfig().getStringList("Inbox." + senderlcasename);

        if(args[1].equalsIgnoreCase("all"))
        {
            if (!maillist.isEmpty())
            {
                //remove the list entirely
                maillist.clear();
                //save it in memory
                plugin.getConfig().set("Inbox." + senderlcasename, null);
                //message to player
                playersender.sendMessage(ChatColor.DARK_GREEN +
                        "Little Timmy the Mailman notices you throwing your mail away.");
                playersender.sendMessage(ChatColor.AQUA +
                        "<Little Timmy>" + ChatColor.WHITE + " There sure is nothing like a nice clean empty mailbox!");
                //save config file
                plugin.saveConfig();
                return true;
            }

            if (maillist.isEmpty() || maillist == null)
            {
                //message to player
                playersender.sendMessage(ChatColor.DARK_GREEN + "Little Timmy the Mailman is worried about you.");
                playersender.sendMessage(ChatColor.AQUA +
                        "<Little Timmy>" + ChatColor.WHITE + " Come along now " +
                        playersender.getName() + ", it's already empty!");
                return true;
            }
        }


        //This section is equivalent to "If arg[1] is not a number within range of maillist, return false"
        try
        {
            int intarg1 = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        int intarg1 = Integer.parseInt(args[1]);
        if (intarg1 > maillist.size())
        {
            return false;
        }

        //remove mail with the index (args[1] - 1)
        maillist.remove(intarg1 - 1);

        //save it in memory
        if (!maillist.isEmpty())
            plugin.getConfig().set("Inbox." + senderlcasename, maillist);
        if (maillist.isEmpty())
            plugin.getConfig().set("Inbox." + senderlcasename, null);

        //message to player
        if (maillist.size() > 0)
            playersender.sendMessage(ChatColor.AQUA + "<Little Timmy>" + ChatColor.WHITE +
                    " Ok, it's gone! Here's what's left!");
        if (maillist.size() == 0)
            playersender.sendMessage(ChatColor.AQUA + "<Little Timmy>" + ChatColor.WHITE + " That's it! No mail left!");

        //save config file
        plugin.saveConfig();

        //reprint the list by calling read
        ReadMail(playersender, true);

        return true;
    }

    public boolean Help(Player playersender)
    {
        //send help details to player
        playersender.sendMessage(ChatColor.RED + "/mail to [playername] [message]" + ChatColor.WHITE +
                " - Sends a message to a player who is offline for them to receive when they next log in.");
        playersender.sendMessage(ChatColor.RED + "/mail check" + ChatColor.WHITE +
                " - Shows you how many messages are in your inbox.");
        playersender.sendMessage(ChatColor.RED + "/mail read" + ChatColor.WHITE +
                " - Shows you the mail in your inbox.");
        playersender.sendMessage(ChatColor.RED + "/mail delete [all/#]" + ChatColor.WHITE +
                " - Deletes all messages (all) or a specific message (#) specified by the number you enter.");
        return true;
    }


    // ------------ On Logout ------------- //
    public void SweepMail(Player playersender)
    {
        //vars
        String senderlcasename = playersender.getName().toLowerCase();

        // get a copy of config list
        maillist = plugin.getConfig().getStringList("Inbox." + senderlcasename);

        //set date format and get current date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy|HH:mm");
        Date currentdate = new Date();
        Date datMsgDate = new Date();
        String strMsgDate = "";

        //for each string in list
        Iterator it = maillist.iterator();
        while (it.hasNext())
        {
            //get the date from first 16 chars
            strMsgDate = it.next().toString().substring(0,16);
            try
            {
                datMsgDate = dateFormat.parse(strMsgDate);
            }
            catch (ParseException ignore){} //errors will only occur if config is messed with

            if (currentdate.getTime() - datMsgDate.getTime() > 604800000) //if it's older than 1 week (in ms)
            {
                it.remove();
            }
        }

        if (!maillist.isEmpty())
            plugin.getConfig().set("Inbox." + senderlcasename, maillist);
        if (maillist.isEmpty())
            plugin.getConfig().set("Inbox." + senderlcasename, null);
        plugin.saveConfig();
    }


    // ---------- Admin Commands ---------- //

    public boolean AdminHelp(Player playersender)
    {
        //send help details to player
        playersender.sendMessage(ChatColor.RED + "/mailadmin check [playername]" + ChatColor.WHITE +
                " - Shows you how many messages are in [playername]'s inbox.");
        playersender.sendMessage(ChatColor.RED + "/mailadmin read [playername]" + ChatColor.WHITE +
                " - Shows you the the mail in [playername]'s inbox.");
        playersender.sendMessage(ChatColor.RED + "/mailadmin delete [all/#/old] [playername]" + ChatColor.WHITE +
                " - Deletes all messages (all), a specific message (#) specified by the number you enter, " +
                "or messages older than a week (old) in [playername]'s inbox.");
        return true;
    }

    public boolean AdminCheckMail(Player playersender, String targetplayerarg)
    {
        String targetplayer = targetplayerarg.toLowerCase();

        //if player doesn't exist
        if (!shinyBaseAPI.isExistingPlayer(targetplayer))
        {
            playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED + " does not exist.");
            return true;
        }

        //get a copy of config list
        maillist = plugin.getConfig().getStringList("Inbox." + targetplayer);

        //if the list is null or empty, notify player.
        if ((maillist == null || maillist.isEmpty()))
        {
            playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED  + " has no mail.");
            return true;
        }


        if (maillist.size() == 1)
            playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED + " has 1 message.");
        if (maillist.size() > 1)
            playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED + " has " +
                    maillist.size() + " messages.");

        return true;
    }

    public boolean AdminReadMail(Player playersender, String targetplayerarg, boolean isDeleting)
    {
        String targetplayer = targetplayerarg.toLowerCase();

        //if the target player does no exist
        if (!shinyBaseAPI.isExistingPlayer(targetplayer.toLowerCase()))
        {
            playersender.sendMessage(ChatColor.RED + "Player " + targetplayerarg + " does not exist.");
            return true;
        }

        //make a copy of all the strings for target player
        List<String> maillist = plugin.getConfig().getStringList("Inbox." + targetplayer);

        //if the list is null or empty, notify player
        if ((maillist == null || maillist.isEmpty()) && !isDeleting)
        {
            //if issuing from /read
            playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED  + " has no mail.");
            return true;
        }

        //send message with target's name
        playersender.sendMessage(ChatColor.DARK_GREEN + targetplayerarg + ChatColor.RED + "'s mail:");

        //mail sending loop
        int mailnumber = 1;
        //for each message in the target's inbox
        for (String nextmail : maillist)
        {
            //divide the string into array 'item' on char "|" (date/time, sender, and msg)
            String[] item = nextmail.split("\\|");
            //throw away the /yyyy from date
            item[0] = item[0].substring(0,5);
            //send mail to player with mailnumber
            playersender.sendMessage(ChatColor.RED + "" + mailnumber + ". " + item[0] + " " + item[1] +
                    " <" + item[2] + "> " + ChatColor.GREEN + item[3]);
            mailnumber++;
        }
        maillist.clear();
        return true;
        //}

        /*if (args[1].equalsIgnoreCase("next"))
        {
            //get the items from the next String in list 'maillist' (date/time, sender and msg)
            String[] item = maillist.get(0).split("\\|");
            //send mail to player
            playersender.sendMessage(ChatColor.AQUA + item[0] + " <" + item[1] + "> " + ChatColor.WHITE + item[2]);
            return true;
        }*/
        //return false;
    }

    public boolean AdminDeleteMail(Player playersender, String[] args)
    {
        String targetplayer = args[2];

        //get a copy of config list
        maillist = plugin.getConfig().getStringList("Inbox." + targetplayer);

        if(args[1].equalsIgnoreCase("all"))
        {
            if (!maillist.isEmpty())
            {
                //remove the list entirely
                maillist.clear();
                //save it in memory
                plugin.getConfig().set("Inbox." + targetplayer, null);
                //message to player
                playersender.sendMessage(ChatColor.DARK_GREEN + targetplayer + ChatColor.RED +
                        "'s inbox has been emptied.");
                //save config file
                plugin.saveConfig();
                return true;
            }

            //set date format and get current date
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy|HH:mm");
            Date currentdate = new Date();
            Date datMsgDate = new Date();
            String strMsgDate = "";

            //for each string in list
            Iterator it = maillist.iterator();
            int oldcount = 0;
            while (it.hasNext())
            {
                //get the date from first 16 chars
                strMsgDate = it.next().toString().substring(0,16);
                try
                {
                    datMsgDate = dateFormat.parse(strMsgDate);
                }
                catch (ParseException ignore){} //errors will only occur if config is messed with

                if (currentdate.getTime() - datMsgDate.getTime() > 604800000) //if it's older than 1 week (in ms)
                {
                    oldcount++;
                }
            }

            if (maillist.isEmpty() || maillist == null)
            {
                //message to player
                playersender.sendMessage(ChatColor.DARK_GREEN + targetplayer + ChatColor.RED +
                        " has no mail to delete.");
                return true;
            }
        }

        if(args[1].equalsIgnoreCase("old"))
        {
            //Sweep target's mail and pwn them (remove old messages)

            //set date format and get current date
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy|HH:mm");
            Date currentdate = new Date();
            Date datMsgDate = new Date();
            String strMsgDate = "";

            //for each string in list
            Iterator it = maillist.iterator();
            int deleted = 0;
            while (it.hasNext())
            {
                //get the date from first 16 chars
                strMsgDate = it.next().toString().substring(0,16);
                try
                {
                    datMsgDate = dateFormat.parse(strMsgDate);
                }
                catch (ParseException ignore){} //errors will only occur if config is messed with

                if (currentdate.getTime() - datMsgDate.getTime() > 604800000) //if it's older than 1 week (in ms)
                {
                    it.remove();
                    deleted++;
                }
            }

            if (!maillist.isEmpty())
                plugin.getConfig().set("Inbox." + targetplayer, maillist);
            if (maillist.isEmpty())
                plugin.getConfig().set("Inbox." + targetplayer, null);

            plugin.saveConfig();

            if (deleted != 1)
            {
                playersender.sendMessage(ChatColor.RED + "(" + ChatColor.DARK_GREEN + deleted + ChatColor.RED +
                        " messages were deleted.)");
                return true;
            }
            //else
            playersender.sendMessage(ChatColor.RED + "(" + ChatColor.DARK_GREEN + deleted + ChatColor.RED +
                    " message was deleted.)");

            return true;
        }



        //This section is equivalent to "If arg[1] is not a number within range of maillist, return false"
        try
        {
            int intarg1 = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        int intarg1 = Integer.parseInt(args[1]);
        if (intarg1 > maillist.size())
        {
            return false;
        }

        //remove mail with the index (args[1] - 1)
        maillist.remove(intarg1 - 1);

        //save it in memory
        if (!maillist.isEmpty())
            plugin.getConfig().set("Inbox." + targetplayer, maillist);
        if (maillist.isEmpty())
            plugin.getConfig().set("Inbox." + targetplayer, null);

        //message to player
        if (maillist.size() > 0)
            playersender.sendMessage(ChatColor.RED + "Message removed.");
        if (maillist.size() == 0)
            playersender.sendMessage(ChatColor.RED + "Message removed. No messages remaining.");

        //save config file
        plugin.saveConfig();

        //reprint the list by calling read
        AdminReadMail(playersender, targetplayer, true);

        return true;
    }
}