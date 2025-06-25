package com.teste21;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

            // Aplicar efeitos ou conceder itens especiais com base no título
            switch (title) {
                case "Guerreiro Lendário":// Escanor Orgulho do Leão
                // Um dos mais poderosos, confiando totalmente na própria força.
                    break;
                case "Mestre da Magia":// Merlin Gula do Javali
                // Um dos magos mais poderosos, obcecado por conhecimento e poder.
                    break;
                case "Explorador Sombrio":// Meliodas Ira do Dragão
                // O líder dos Sete Pecados Capitais, com uma força imensa e um
                    break;
                case "Titã do PvP":// Ban Avareza da Cabra
                // Um dos mais poderosos, obcecado por sua própria força e habilidades.
                    break;
                case "Caçador de Tesouros":// King Preguiça do Urso
                // O rei dos Fairy Kings, com um poder imenso e habilidades de controle da natureza.
                    break;
                case "Rei da Sobrevivência":// Diane Inveja da Terra
                // A gigante mais poderosa, com habilidades de controle da terra e força imensa.
                    break;
                case "Arquimago Misterioso":// Gowther Luxúria do Carneiro
                // Um dos magos mais poderosos, com habilidades de manipulação mental e mágica.
                    break;
            }
        }, () -> player.sendMessage(ChatColor.RED + "Erro ao definir título!"), 0);

        return true;
    }
}
