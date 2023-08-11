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

package ru.namerpro.AdvancedNMotd.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.namerpro.AdvancedNMotd.Templates.PluginCommandExecutorTemplate;

public class BukkitCommandExecutor implements CommandExecutor {

	private static final PluginCommandExecutorTemplate template = new PluginCommandExecutorTemplate() {

		@Override
		protected boolean isPlayer() {
			return commandSenderInstance instanceof Player;
		}

		@Override
		protected void sendPlayerMessage(String message) {
			((Player) commandSenderInstance).sendMessage(message);
		}

	};

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		template.setCommandSender(sender);
		return template.onAdvancedNMotdCommand(command.getName(), args);
	}

}
