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
import ru.namerpro.AdvancedNMotd.Configuration.IConfigurationFactory;
import ru.namerpro.AdvancedNMotd.Extensions.Loader.Loader;
import ru.namerpro.AdvancedNMotd.Extensions.Loader.LoaderException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.FormatRule.FontData;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public abstract class PluginStartUpTemplate {

    protected final Object mainClassInstance;

    public PluginStartUpTemplate(Object mainClassInstance) {
        this.mainClassInstance = mainClassInstance;
    }

    public void onAdvancedNMotdEnable() {
        ConfigurationManager.setConfigurationFactory(getConfigurationFactory());

        try {
            ConfigurationManager.createConfigs();
        } catch (IOException error) {
            UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.couldNotCreateConfigurationFiles);
            error.printStackTrace();
        }

        if (ConfigurationManager.config.getBoolean("AdvancedNMotd.CheckForUpdates")) {
            setUpdater();
        }

        try {
            FontData.loadLanguages();
        } catch (IOException error) {
            UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.couldNotLoadLanguagesData);
            error.printStackTrace();
        }

        try {
            Loader.getLoaderInstance().load(true);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | LoaderException | URISyntaxException | IOException error) {
            UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.couldNotLoadExtensions);
            error.printStackTrace();
        }

        registrar();

        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.empty);
        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.onPluginEnableTitle);
        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.onPluginEnableNotification);
        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.yellowLineSeparator);
        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.empty);
    }

    public void onAdvancedNMotdDisable(Object updateCheckingTask) {
        if (updateCheckingTask != null) {
            cancelTask(updateCheckingTask);
        }
        try {
            Loader.getLoaderInstance().load(false);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | LoaderException | URISyntaxException | IOException error) {
            UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.couldNotDisableExtensions);
            error.printStackTrace();
        }
        UniversalActions.universalInstance.sendConsoleMessage(PluginMessagesTemplate.pluginIsDisabled);
    }

    protected abstract IConfigurationFactory getConfigurationFactory();
    protected abstract void setUpdater();
    protected abstract void registrar();
    protected abstract void cancelTask(Object updateCheckingTask);

}
