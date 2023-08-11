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

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import ru.namerpro.AdvancedNMotd.Configuration.Adapters.BungeeConfigurationAdapter;
import ru.namerpro.AdvancedNMotd.Configuration.IConfigurationFactory;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.Templates.PluginStartUpTemplate;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;
import ru.namerpro.AdvancedNMotd.Updater.UpdateChecker;

import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    private static final int pluginId = 19453;

    private static ScheduledTask updateCheckingTask;

    private final PluginStartUpTemplate template = new PluginStartUpTemplate(this) {

        @Override
        protected IConfigurationFactory getConfigurationFactory() {
            return new BungeeConfigurationAdapter.BungeeConfigurationAdapterFactory();
        }

        @Override
        protected void setUpdater() {
            updateCheckingTask = BungeeCord.getInstance().getScheduler().schedule((Main) mainClassInstance, UpdateChecker::check, 0L, 1L, TimeUnit.DAYS);
        }

        @Override
        protected void registrar() {
            PluginManager manager = getProxy().getPluginManager();
            manager.registerCommand((Main) mainClassInstance, new BungeeCommandExecutor());
            manager.registerListener((Main) mainClassInstance, new BungeeMotd());
        }

        @Override
        protected void cancelTask(Object updateCheckingTask) {
            ((ScheduledTask) updateCheckingTask).cancel();
        }

    };

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, pluginId);

        UniversalActions.universalInstance = new BungeeUniversalActions();
        Information.serverType = Information.ServerType.BUNGEE;
        Information.areColorsSupportedByServer = true;

        template.onAdvancedNMotdEnable();
    }

    @Override
    public void onDisable() {
        template.onAdvancedNMotdDisable(updateCheckingTask);
    }

}
