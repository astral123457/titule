package com.teste21;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AgendadorTNT {

    private final JavaPlugin plugin;
    private final Map<String, ScheduledTask> tarefasAgendadas = new HashMap<>();

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
                 ResultSet rs = stmt.executeQuery("""
                     SELECT nome FROM fabricas WHERE ativa_agendamento = 1
                 """)) {
                while (rs.next()) {
                    String nome = rs.getString("nome").toLowerCase();
                    agendarFabricaPorNome(nome);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("❌ Erro ao agendar fábricas:");
                e.printStackTrace();
            }
        }
    }

    public boolean agendarFabricaPorNome(String nomeFabrica) {
        String nome = nomeFabrica.toLowerCase();
        cancelarAgendamento(nome); // Evita duplicação

        Connection conn = ((Main) plugin).getConnection();
        if (conn == null) return false;

        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT world, tnt_x, tnt_y, tnt_z
                    FROM fabricas
                    WHERE nome = ? AND ativa_agendamento = 1
                """)) {
                ps.setString(1, nome);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;

                    String worldName = rs.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().warning("🌍 Mundo '" + worldName + "' não carregado para fábrica '" + nome + "'.");
                        return false;
                    }

                    Location tntLoc = new Location(world, rs.getInt("tnt_x"), rs.getInt("tnt_y"), rs.getInt("tnt_z"));
                    Location observerLoc = tntLoc.clone().add(0, 0, -1); // Ajuste para OBSERVER atrás da TNT

                    ScheduledTask task = Bukkit.getRegionScheduler().runAtFixedRate(plugin, tntLoc, scheduled -> {
                        Block tnt = tntLoc.getBlock();
                        Block obs = observerLoc.getBlock();

                        if (tnt.getType() != Material.TNT) tnt.setType(Material.TNT);
                        if (obs.getType() != Material.OBSERVER) obs.setType(Material.OBSERVER);
                    }, 20L, 180L);

                    tarefasAgendadas.put(nome, task);
                    plugin.getLogger().info("✅ Fábrica '" + nome + "' agendada dinamicamente em " + formatLoc(tntLoc));
                    return true;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("❌ Erro ao agendar fábrica '" + nome + "':");
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean cancelarAgendamento(String nomeFabrica) {
        String nome = nomeFabrica.toLowerCase();
        ScheduledTask task = tarefasAgendadas.remove(nome);
        if (task != null && !task.isCancelled()) {
            task.cancel();
            plugin.getLogger().info("🛑 Agendamento cancelado para fábrica '" + nome + "'.");
            return true;
        }
        return false;
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}