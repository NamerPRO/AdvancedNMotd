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
import ru.namerpro.AdvancedNMotd.Configuration.IConfiguration;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PluginMotdTemplate {

    private static final Random random = new Random();

    protected Object motdPingInstance;

    public void setMotdPingInstance(Object motdPingInstance) {
        this.motdPingInstance = motdPingInstance;
    }

    public void setAdvnacedNMotdMotd() throws Exception {
        IConfiguration concreteMotd = ConfigurationManager.motds.get(random.nextInt(ConfigurationManager.motds.size()));

        if(ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Motds.Motd")) {
            String concreteLineOneOfConcreteMotd= concreteMotd.getString("Motd.LineOne");
            String concreteLineTwoOfConcreteMotd = concreteMotd.getString("Motd.LineTwo");

            String parsedConcreteLineOneOfConcreteMotd = new RuleParser(concreteLineOneOfConcreteMotd).apply();
            String parsedConcreteLineTwoOfConcreteMotd = new RuleParser(concreteLineTwoOfConcreteMotd).apply();

            setMainMotd(parsedConcreteLineOneOfConcreteMotd, parsedConcreteLineTwoOfConcreteMotd);
        }

        if(ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Data")) {
            int maxPlayers = ConfigurationManager.data.getInt("Data.FakeMaxPlayers");
            setPlayersMaximum(maxPlayers);

            int online = UniversalActions.universalInstance.getOnlinePlayers() * ConfigurationManager.data.getInt("Data.PlayerSpace") + ConfigurationManager.data.getInt("Data.ExtraOnlinePlayers");
            if((ConfigurationManager.data.getBoolean("Data.OnlineIsBiggerThenSlotsProtection")) && (online > maxPlayers)) {
                online = maxPlayers;
            }
            setPlayersOnline(online);
        }

        if(ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Motds.Version")) {
            setVersionMotd(new RuleParser(concreteMotd.getString("Version.Text")).apply());
        }

        if(ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Motds.HoverBox")) {
            List<String> hoverBoxLines = concreteMotd.getStringList("HoverBox.Text");
            int hoverBoxLinesAmount = hoverBoxLines.size();
            for (int i = 0; i < hoverBoxLinesAmount; ++i) {
                hoverBoxLines.set(i, new RuleParser(hoverBoxLines.get(i)).apply());
            }
            setHoverMotd(hoverBoxLines);
        }

        if (ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Icons")) {
            setFavicon(getRandomFavicon(concreteMotd));
        }
    }

    private String getRandomFavicon(IConfiguration concreteMotd) throws NoSuchElementException, IOException {
        String iconsListAsString = Objects.requireNonNull(concreteMotd.getString("Motd.Icons")).trim();

        List<String> iconsArray = Arrays.stream(iconsListAsString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        File[] icons = new File(Information.resourceFolderPath + "/icons").listFiles((dir, name) -> name.toLowerCase().endsWith(".png") && (iconsListAsString.equals("") || iconsArray.contains(name)));
        if (icons == null || icons.length == 0) {
            throw new NoSuchElementException("Cannot get random favicon, because no \"*.png\" favicon files found in \"AdvancedNMotd/Icons\" folder! If you don't want to use this functional set \"Icons: false\" in \"config.yml\" file.");
        }
        File concreteIcon = icons[random.nextInt(icons.length)];
        return Base64.getEncoder().encodeToString(Files.readAllBytes(concreteIcon.toPath()));
    }

    protected abstract void setMainMotd(String motdLineOne, String motdLineTwo);
    protected abstract void setHoverMotd(List<String> hoverMotdLines);
    protected abstract void setVersionMotd(String versionMotdLine);
    protected abstract void setFavicon(String base64Favicon) throws IOException;
    protected abstract void setPlayersMaximum(int playersMaximum);
    protected abstract void setPlayersOnline(int playersOnline);

}
