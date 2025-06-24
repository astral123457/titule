package com.teste21;

import org.bukkit.command.*;
import java.sql.*;

public class ListarFabricasCommand implements CommandExecutor {

    private final Main plugin;

    public ListarFabricasCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Connection conn = plugin.getConnection();

        if (conn == null) {
            sender.sendMessage("❌ Banco de dados não conectado.");
            return true;
        }

        synchronized (conn) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM fabricas")) {

                boolean encontrou = false;

                sender.sendMessage("📦 Fábricas registradas:");
                while (rs.next()) {
                    encontrou = true;

                    String nome = rs.getString("nome");
                    String world = rs.getString("world");
                    int x = rs.getInt("tnt_x");
                    int y = rs.getInt("tnt_y");
                    int z = rs.getInt("tnt_z");

                    sender.sendMessage("🔹 " + nome + " » TNT em " + x + " " + y + " " + z + " (mundo: " + world + ")");
                }

                if (!encontrou) {
                    sender.sendMessage("⚠️ Nenhuma fábrica encontrada.");
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Erro ao listar fábricas:");
                e.printStackTrace();
                sender.sendMessage("❌ Erro ao acessar o banco de dados.");
            }
        }

        return true;
    }
}