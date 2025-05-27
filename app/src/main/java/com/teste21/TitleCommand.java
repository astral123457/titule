package com.teste21;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
                case "Guerreiro Lendário":
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                    player.sendMessage(ChatColor.GOLD + "Você recebeu uma espada lendária!");
                    break;
                case "Mestre da Magia":
                    player.getInventory().addItem(new ItemStack(Material.MAGMA_CREAM, 3));
                    player.getInventory().addItem(new ItemStack(Material.BLAZE_POWDER, 3));
                    player.sendMessage(ChatColor.GOLD + "Você recebeu 3 Creme de Magma e 3 Pó de Blaze!");
                    break;
                case "Explorador Sombrio":
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1));
                    player.sendMessage(ChatColor.DARK_PURPLE + "Você recebeu invisibilidade por 30 segundos!");
                    break;
                case "Titã do PvP":
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    player.sendMessage(ChatColor.RED + "Você recebeu uma armadura poderosa!");
                    break;
                case "Caçador de Tesouros":
                    player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                    player.sendMessage(ChatColor.YELLOW + "Você recebeu 2 Maçãs Douradas místicas!");
                    break;
                case "Rei da Sobrevivência":
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                    player.sendMessage(ChatColor.GREEN + "Você recebeu comida especial e regeneração temporária!");
                    break;
                case "Arquimago Misterioso":
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTED_BOOK));
                    player.sendMessage(ChatColor.BLUE + "Você recebeu um livro de encantamentos poderosos!");
                    break;
            }
        }, () -> player.sendMessage(ChatColor.RED + "Erro ao definir título!"), 0);

        return true;
    }
}