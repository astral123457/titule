package com.teste21;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;

public class TitleCommand implements CommandExecutor {
    private final Main plugin;
    private final String[] titles = {
            "Guerreiro Lendário", "Mestre da Magia", "Explorador Sombrio",
            "Titã do PvP", "Caçador de Tesouros", "Rei da Sobrevivência",
            "Arquimago Misterioso"
    };
    private final ChatColor[] rainbowColors = {
            ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.BLUE, ChatColor.DARK_PURPLE,
            ChatColor.LIGHT_PURPLE
    };

    public TitleCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        Random random = new Random();

        // Escolher um título e uma cor aleatórios
        String title = titles[random.nextInt(titles.length)];
        ChatColor color = rainbowColors[random.nextInt(rainbowColors.length)];
        String fullTitle = color + "[" + title + "] " + ChatColor.RESET;

        // 🚀 Salvar no banco de dados
        plugin.savePlayerTitle(uuid, fullTitle);

        // 🚀 Definir título no jogador (compatível com Folia)
        player.getScheduler().execute(plugin, () -> {
            player.setDisplayName(fullTitle + player.getName());
            player.sendMessage(ChatColor.GREEN + "Seu novo título é: " + fullTitle);
        }, () -> player.sendMessage(ChatColor.RED + "Erro ao definir título!"), 0);

        return true;
    }
}