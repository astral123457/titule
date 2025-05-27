package com.teste21;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {
    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String title = plugin.getPlayerTitle(player.getUniqueId());

        if (title != null) {
            // ✅ Definir o título salvo ao entrar no servidor (compatível com Folia)
            player.getScheduler().execute(plugin, () -> {
                player.setDisplayName(title + player.getName());
                player.sendMessage(ChatColor.YELLOW + "Título restaurado: " + title);
            }, () -> player.sendMessage(ChatColor.RED + "Erro ao recarregar título!"), 0);
        }
    }
}