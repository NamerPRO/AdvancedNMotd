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

import java.util.Objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.utility.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitTask;
import ru.namerpro.AdvancedNMotd.Configuration.Adapters.BukkitConfigurationAdapter;
import ru.namerpro.AdvancedNMotd.Configuration.IConfigurationFactory;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.Templates.PluginStartUpTemplate;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;
import ru.namerpro.AdvancedNMotd.Updater.UpdateChecker;

public class Main extends JavaPlugin {

	private static final int pluginId = 19452;

	private static BukkitTask updateCheckingTask;

	private final PluginStartUpTemplate template = new PluginStartUpTemplate(this) {

		@Override
		protected IConfigurationFactory getConfigurationFactory() {
			return new BukkitConfigurationAdapter.BukkitConfigurationAdapterFactory();
		}

		@Override
		protected void setUpdater() {
			updateCheckingTask = Bukkit.getScheduler().runTaskTimerAsynchronously((Main) mainClassInstance, UpdateChecker::check, 1L, 1728000L);
		}

		@Override
		protected void registrar() {
			Objects.requireNonNull(getCommand("advancedNMotd")).setExecutor(new BukkitCommandExecutor());
			ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolLibMotd((Main) mainClassInstance, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO));
			ProtocolLibrary.getProtocolManager().addPacketListener(new BukkitClientVersionDetector((Main) mainClassInstance, ListenerPriority.NORMAL, PacketType.Handshake.Client.SET_PROTOCOL));
		}

		@Override
		protected void cancelTask(Object updateCheckingTask) {
			((BukkitTask) updateCheckingTask).cancel();
		}

	};

	@Override
	public void onEnable() {
		Metrics metrics = new Metrics(this, pluginId);

		UniversalActions.universalInstance = new BukkitUniversalActions();
		Information.serverType = Information.ServerType.BUKKIT;
		Information.areColorsSupportedByServer = ProtocolLibrary.getProtocolManager().getMinecraftVersion().compareTo(MinecraftVersion.NETHER_UPDATE) >= 0;

		template.onAdvancedNMotdEnable();
	}
	
	@Override
	public void onDisable() {
		template.onAdvancedNMotdDisable(updateCheckingTask);
	}
	
}
