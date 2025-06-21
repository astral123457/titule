package com.teste21;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class AgendadorTNT {

    private final JavaPlugin plugin;

    public AgendadorTNT(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void agendarTodasFabricas() {
        Connection conn = ((Main) plugin).getConnection();

        if (conn == null) {
            plugin.getLogger().warning("❌ Banco de dados não está conectado.");
            return;
        }

        synchronized (conn) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT nome, world, tnt_x, tnt_y, tnt_z FROM fabricas WHERE ativa_agendamento = 1")) {

                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String worldName = rs.getString("world");

                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().warning("🌍 Mundo '" + worldName + "' não carregado para fábrica '" + nome + "'.");
                        continue;
                    }

                    Location tntLoc = new Location(world, rs.getInt("tnt_x"), rs.getInt("tnt_y"), rs.getInt("tnt_z"));

                    Bukkit.getRegionScheduler().runAtFixedRate(plugin, tntLoc, task -> {
                        Block block = tntLoc.getBlock();
                        if (block.getType() != Material.TNT) {
                            block.setType(Material.TNT);
                        }
                    }, 0L, 180L);

                    plugin.getLogger().info("⏳ Agendamento iniciado para '" + nome + "' em " + formatLoc(tntLoc));
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("❌ Erro ao agendar fábricas:");
                e.printStackTrace();
            }
        }
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}