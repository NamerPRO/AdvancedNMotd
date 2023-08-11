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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.namerpro.AdvancedNMotd.Templates.PluginMotdTemplate;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class BungeeMotd implements Listener {

    public static ProxyPingEvent event;

    private final PluginMotdTemplate template = new PluginMotdTemplate() {

        @Override
        protected void setMainMotd(String motdLineOne, String motdLineTwo) {
            ((ServerPing) motdPingInstance).setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(motdLineOne + '\n' + ChatColor.RESET + motdLineTwo)));
        }

        @Override
        protected void setHoverMotd(List<String> hoverMotdLines) {
            int hoverMotdLinesAmount = hoverMotdLines.size();
            ServerPing.PlayerInfo[] infos = new ServerPing.PlayerInfo[hoverMotdLinesAmount];
            for (int i = 0; i < hoverMotdLinesAmount; ++i) {
                infos[i] = new ServerPing.PlayerInfo(hoverMotdLines.get(i), new UUID(0, 0));
            }
            ((ServerPing) motdPingInstance).getPlayers().setSample(infos);
        }

        @Override
        protected void setVersionMotd(String versionMotdLine) {
            ((ServerPing) motdPingInstance).getVersion().setProtocol(-1);
            ((ServerPing) motdPingInstance).getVersion().setName(versionMotdLine);
        }

        @Override
        protected void setFavicon(String base64Favicon) throws IOException {
            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(base64Favicon));
            ((ServerPing) motdPingInstance).setFavicon(Favicon.create(ImageIO.read(stream)));
            stream.close();
        }

        @Override
        protected void setPlayersMaximum(int playersMaximum) {
            ((ServerPing) motdPingInstance).getPlayers().setMax(playersMaximum);
        }

        @Override
        protected void setPlayersOnline(int playersOnline) {
            ((ServerPing) motdPingInstance).getPlayers().setOnline(playersOnline);
        }

    };

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) throws Exception {
        BungeeMotd.event = event;

        ServerPing ping = event.getResponse();
        template.setMotdPingInstance(ping);
        template.setAdvnacedNMotdMotd();
        event.setResponse(ping);
    }

}
