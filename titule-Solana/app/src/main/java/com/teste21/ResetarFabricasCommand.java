package com.teste21;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ResetarFabricasCommand implements CommandExecutor {

    private final Main plugin;

    public ResetarFabricasCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("❗ Apenas jogadores podem executar este comando.");
            return true;
        }

        Connection conn = plugin.getConnection();

        if (conn == null) {
            player.sendMessage("❌ Banco de dados não está conectado.");
            return true;
        }

        synchronized (conn) {
            try (Statement statement = conn.createStatement()) {
                statement.execute("DELETE FROM fabricas"); // SQLite não aceita TRUNCATE
                player.sendMessage("🧹 Todas as fábricas foram apagadas com sucesso!");
            } catch (SQLException e) {
                plugin.getLogger().severe("Erro ao resetar a tabela fabricas:");
                e.printStackTrace();
                player.sendMessage("❌ Erro ao limpar o banco de dados.");
            }
        }

        return true;
    }
    
}