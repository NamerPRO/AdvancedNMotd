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

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;

import ru.namerpro.AdvancedNMotd.Templates.PluginMessagesTemplate;
import ru.namerpro.AdvancedNMotd.Templates.PluginMotdTemplate;

public class ProtocolLibMotd extends PacketAdapter {

    private final PluginMotdTemplate template = new PluginMotdTemplate() {

        @Override
        protected void setMainMotd(String motdLineOne, String motdLineTwo) {
            ((WrappedServerPing) motdPingInstance).setMotD(motdLineOne + "\n" + ChatColor.RESET + motdLineTwo);
        }

        @Override
        protected void setHoverMotd(List<String> hoverMotdLines) {
            int hoverMotdLinesAmount = hoverMotdLines.size();
            WrappedGameProfile[] profiles = new WrappedGameProfile[hoverMotdLinesAmount];
            for (int i = 0; i < hoverMotdLinesAmount; ++i) {
                profiles[i] = new WrappedGameProfile(new UUID(0,0), hoverMotdLines.get(i));
            }
            ((WrappedServerPing) motdPingInstance).setPlayers(Arrays.asList(profiles));
        }

        @Override
        protected void setVersionMotd(String versionMotdLine) {
            ((WrappedServerPing) motdPingInstance).setVersionProtocol(-1);
            ((WrappedServerPing) motdPingInstance).setVersionName(ChatColor.WHITE + versionMotdLine);
        }

        @Override
        protected void setFavicon(String base64Favicon) {
            ((WrappedServerPing) motdPingInstance).setFavicon(WrappedServerPing.CompressedImage.fromBase64Png(base64Favicon));
        }

        @Override
        protected void setPlayersMaximum(int playersMaximum) {
            ((WrappedServerPing) motdPingInstance).setPlayersMaximum(playersMaximum);
        }

        @Override
        protected void setPlayersOnline(int playersOnline) {
            ((WrappedServerPing) motdPingInstance).setPlayersOnline(playersOnline);
        }

    };

    public ProtocolLibMotd(Plugin plugin, ListenerPriority normal, PacketType outServerInfo) {
        super(plugin, normal, outServerInfo);
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        try {
            WrappedServerPing ping = event.getPacket().getServerPings().read(0);
            template.setMotdPingInstance(ping);
            template.setAdvnacedNMotdMotd();
            event.getPacket().getServerPings().write(0, ping);
        } catch (Exception err) {
            Bukkit.getConsoleSender().sendMessage(PluginMessagesTemplate.couldNotSetMotd);
            err.printStackTrace();
        }
    }

}
