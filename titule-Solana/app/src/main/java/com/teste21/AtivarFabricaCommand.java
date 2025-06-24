package com.teste21;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class AtivarFabricaCommand implements CommandExecutor {

    private final Main plugin;

    public AtivarFabricaCommand(Main plugin) {
        this.plugin = plugin;
    }


private void comprarAtivacao(Player player, String nome) {
    plugin.getLogger().info("🟡 [Ativador] Iniciando processo de ativação para '" + nome + "' por " + player.getName());

    plugin.getSolana().buyGameCurrency(player, 0.001).thenAccept(success -> {
        plugin.getLogger().info("⚙️ [Ativador] Resultado da compra de moeda: " + success);

        if (!success) {
            plugin.getLogger().warning("🔴 [Ativador] Compra negada para " + player.getName());
            player.sendMessage(ChatColor.RED + "❌ Você não tem SOL suficiente para ativar esta fábrica.");
            return;
        }
    });
}

    
 @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("⚠ Somente jogadores podem ativar uma fábrica.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "❓ Use: /ativarfabrica <nome>");
            return true;
        }


        
  

        if (args.length != 1) {
            sender.sendMessage("❓ Use: /ativarfabrica <nome>");
            return true;
        }

        String nome = args[0].trim().toLowerCase();
        Connection conn = plugin.getConnection();

        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fabricas SET ativa_agendamento = 1 WHERE nome = ?")) {

                ps.setString(1, nome);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    sender.sendMessage(ChatColor.GREEN + "✅ Fábrica '" + ChatColor.YELLOW + nome + ChatColor.GREEN + "' ativada no banco de dados.");

                    boolean reAgendado = plugin.getAgendadorTNT().agendarFabricaPorNome(nome);
                    if (reAgendado) {
                        sender.sendMessage("🔁 Fábrica '" + nome + "' agendada com sucesso.");
                        plugin.getLogger().info("🟢 Fábrica '" + nome + "' ativada e agendada por " + player.getName());

                        try {
                            plugin.getSolana().solicitarAirdrop(player);
                            } catch (Exception e) {
                                plugin.getLogger().warning("⚠️ Erro ao solicitar airdrop para " + player.getName() + ": " + e.getMessage());
                                e.printStackTrace();
                                player.sendMessage(ChatColor.RED + "⚠️ Ocorreu um erro ao solicitar o airdrop.");
                            }



                        
                    } else {
                        sender.sendMessage("⚠️ Fábrica ativada, mas não foi possível agendar. Verifique o mundo ou posições.");
                    }

                } else {
                    sender.sendMessage("⚠️ Fábrica '" + nome + "' não encontrada.");
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("❌ Erro ao ativar fábrica: " + nome);
                e.printStackTrace();
                sender.sendMessage("❌ Ocorreu um erro ao ativar o agendamento.");
            }
        }
        comprarAtivacao(player, nome);

        return true;
    }
}




