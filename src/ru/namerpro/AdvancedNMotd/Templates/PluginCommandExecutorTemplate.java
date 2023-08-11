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

import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.IOException;

public abstract class PluginCommandExecutorTemplate {

    protected Object commandSenderInstance;

    public void setCommandSender(Object commandSenderInstance) {
        this.commandSenderInstance = commandSenderInstance;
    }

    public boolean onAdvancedNMotdCommand(String commandName, String[] args) {
        if (commandName.equalsIgnoreCase("advancedNMotd")) {
            if (isPlayer()) {
                sendPlayerMessage(PluginMessagesTemplate.commandWasRanByPlayer);
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    try {
                        ConfigurationManager.createConfigs();
                    } catch (IOException error) {
                        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.couldNotReloadConfiguration);
                        error.printStackTrace();
                    }
                    UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.successfullyReloadedPluginConfiguration);
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.helpPageLineOne);
                    UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.helpPageLineTwo);
                    return true;
                }
            }
            UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.unknownCommandWasEntered);
        }
        return true;
    }

    protected abstract boolean isPlayer();
    protected abstract void sendPlayerMessage(String message);

}
