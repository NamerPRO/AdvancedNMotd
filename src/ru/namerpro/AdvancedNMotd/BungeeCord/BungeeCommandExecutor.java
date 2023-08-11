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

package ru.namerpro.AdvancedNMotd.BungeeCord;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ru.namerpro.AdvancedNMotd.Templates.PluginCommandExecutorTemplate;

public class BungeeCommandExecutor extends Command {

    private static final PluginCommandExecutorTemplate template = new PluginCommandExecutorTemplate() {

        @Override
        protected boolean isPlayer() {
            return commandSenderInstance instanceof ProxiedPlayer;
        }

        @Override
        protected void sendPlayerMessage(String message) {
            ((ProxiedPlayer) commandSenderInstance).sendMessage(new TextComponent(message));
        }

    };

    public BungeeCommandExecutor() {
        super("advancedNMotd");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        template.setCommandSender(sender);
        template.onAdvancedNMotdCommand("advancedNMotd", args);
    }

}