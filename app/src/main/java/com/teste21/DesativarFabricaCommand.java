package com.teste21;

import org.bukkit.command.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DesativarFabricaCommand implements CommandExecutor {

    private final Main plugin;

    public DesativarFabricaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("❓ Use: /desativarfabrica <nome>");
            return true;
        }

        String nome = args[0];
        Connection conn = plugin.getConnection();

        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE fabricas SET ativa_agendamento = 0 WHERE nome = ?")) {
                ps.setString(1, nome);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    sender.sendMessage("⛔ Fábrica '" + nome + "' desativada.");
                } else {
                    sender.sendMessage("⚠️ Fábrica '" + nome + "' não encontrada.");
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Erro ao desativar fábrica: " + nome);
                e.printStackTrace();
                sender.sendMessage("❌ Erro ao desativar agendamento.");
            }
        }

        return true;
    }
}