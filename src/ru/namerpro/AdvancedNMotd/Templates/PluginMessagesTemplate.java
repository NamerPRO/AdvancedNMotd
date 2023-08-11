/*
 * AdvancedNMotd - Bukkit / Bungeecord plugin that provides advanced ways to manage minecraft server motd
 * Copyright (C) 2023  NamerPRO
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package ru.namerpro.AdvancedNMotd.Templates;

import net.md_5.bungee.api.ChatColor;

public class PluginMessagesTemplate {

    // Main class messages (On plugin startup)
    public static final String couldNotCreateConfigurationFiles = ChatColor.RED + "Could not create configuration files! Contact plugin developer for more information. Send him the error below:";
    public static final String couldNotLoadExtensions = ChatColor.RED + "AdvancedNMotd: Failed to load extensions with reason:";
    public static final String empty = "";
    public static final String onPluginEnableTitle = ChatColor.YELLOW + "---------------- " + ChatColor.WHITE + "Advanced" + ChatColor.GRAY + "NMotd" + ChatColor.YELLOW + " --------------";
    public static final String onPluginEnableNotification = ChatColor.GREEN + "           AdvancedNMotd is enabled!";
    public static final String yellowLineSeparator = ChatColor.YELLOW + "---------------------------------------------";
    public static final String couldNotDisableExtensions = ChatColor.RED + "AdvancedNMotd: Failed to disable extensions with reason:";
    public static final String pluginIsDisabled = ChatColor.RED + "AdvancedNMotd is disabled!";
    public static final String couldNotLoadLanguagesData = ChatColor.RED + "AdvancedNMotd: Failed to load languages data for format rule 'format'.";
    // =====

    // On command execute messages
    public static final String couldNotReloadConfiguration = ChatColor.RED + "An error occurred while attempting to reload configuration files! Error:";
    public static final String successfullyReloadedPluginConfiguration = ChatColor.GREEN + "AdvancedNMotd: Successfully reloaded plugin configuration!";
    public static final String commandWasRanByPlayer = ChatColor.RED + "AdvancedNMotd: Access denied! Run command from console.";
    public static final String helpPageLineOne = ChatColor.GREEN + "AdvancedNMotd: advancedNMotd reload - reloads plugin configuration.";
    public static final String helpPageLineTwo = ChatColor.GREEN + "AdvancedNMotd: advancedNMotd help - shows this page.";
    public static final String unknownCommandWasEntered = ChatColor.RED + "AdvancedNMotd: Unknown command! Use /advancedNMotd help for more information.";
    // =====

    // On motd set messaged
    public static final String couldNotSetMotd = ChatColor.RED + "Could not set motd, because something went wrong! Here is an error:";
    // =====

}
