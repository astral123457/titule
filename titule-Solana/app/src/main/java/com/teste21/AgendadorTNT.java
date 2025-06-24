package com.teste21;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import java.util.concurrent.TimeUnit;

public class AgendadorTNT {

    private final JavaPlugin plugin;
    private final Solana solana;

    // Mapa para armazenar tarefas agendadas por nome de fábrica
    private final Map<String, ScheduledTask> tarefasAgendadas = new HashMap<>();

    public AgendadorTNT(JavaPlugin plugin, Solana solana) {
    this.plugin = plugin;
    this.solana = solana;
}

    /**
     * Inicia o agendador de TNT para todas as fábricas ativas.
     */

    public void agendarTodasFabricas() {
    Connection conn = ((Main) plugin).getConnection();

    if (conn == null) {
        plugin.getLogger().warning("❌ [Agendador] Banco de dados não está conectado.");
        return;
    }

    plugin.getLogger().info("🔁 [Agendador] Iniciando agendamento de todas as fábricas ativas...");

    synchronized (conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome FROM fabricas WHERE ativa_agendamento = 1")) {

            int contador = 0;
            while (rs.next()) {
                String nome = rs.getString("nome").toLowerCase();
                boolean ok = agendarFabricaPorNome(nome);
                plugin.getLogger().info("➡️ [Agendador] Fábrica '" + nome + "' → Agendada: " + ok);
                contador++;
            }

            plugin.getLogger().info("✅ [Agendador] Total de fábricas processadas: " + contador);

        } catch (SQLException e) {
            plugin.getLogger().severe("❌ [Agendador] Erro ao agendar fábricas:");
            e.printStackTrace();
        }
    }
}

    public boolean agendarFabricaPorNome(String nomeFabrica) {
    String nome = nomeFabrica.toLowerCase();
    plugin.getLogger().info("📌 [Agendador] Solicitado agendamento para: " + nome);

    cancelarAgendamento(nome);

    Connection conn = ((Main) plugin).getConnection();
    if (conn == null) {
        plugin.getLogger().warning("⚠ [Agendador] Conexão com o banco está nula.");
        return false;
    }

    synchronized (conn) {
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT world, tnt_x, tnt_y, tnt_z
                FROM fabricas
                WHERE nome = ? AND ativa_agendamento = 1
            """)) {

            ps.setString(1, nome);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    plugin.getLogger().warning("⚠ [Agendador] Nenhuma fábrica ativa encontrada com nome '" + nome + "'.");
                    return false;
                }

                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("🌍 [Agendador] Mundo '" + worldName + "' não encontrado ou não carregado.");
                    return false;
                }

                Location tntLoc = new Location(world, rs.getInt("tnt_x"), rs.getInt("tnt_y"), rs.getInt("tnt_z"));
                Location observerLoc = tntLoc.clone().add(0, 0, -1);

                plugin.getLogger().info("🧨 [Agendador] Agendando execução para fábrica '" + nome + "' em " + formatLoc(tntLoc));

                ScheduledTask task = Bukkit.getRegionScheduler().runAtFixedRate(plugin, tntLoc, scheduled -> {
                    Block tnt = tntLoc.getBlock();
                    Block obs = observerLoc.getBlock();

                    if (tnt.getType() != Material.TNT) {
                        plugin.getLogger().info("💣 [Agendador] Repondo TNT na fábrica '" + nome + "'");
                        tnt.setType(Material.TNT);
                    }

                    if (obs.getType() != Material.OBSERVER) {
                        plugin.getLogger().info("📡 [Agendador] Repondo Observer atrás da TNT '" + nome + "'");
                        obs.setType(Material.OBSERVER);
                    }
                }, 20L, 180L);

                tarefasAgendadas.put(nome, task);
                plugin.getLogger().info("✅ [Agendador] Fábrica '" + nome + "' agendada com sucesso.");
                return true;
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("❌ [Agendador] Erro ao agendar fábrica '" + nome + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

    public void agendarAirdrop(Player jogador) {
    long delayTicks = 20L;
    long intervaloTicks = TimeUnit.HOURS.toSeconds(8) * 20L;

    plugin.getLogger().info("🪂 [Agendador] Agendando airdrop para " + jogador.getName() + " a cada 8h...");

    Bukkit.getRegionScheduler().runAtFixedRate(plugin, jogador.getLocation(), scheduledTask -> {
        try {
            plugin.getLogger().info("📤 [Agendador] Solicitando airdrop para " + jogador.getName());
            solana.solicitarAirdrop(jogador);
        } catch (Exception e) {
            plugin.getLogger().warning("❌ [Agendador] Erro ao solicitar airdrop para " + jogador.getName());
            e.printStackTrace();
        }
    }, delayTicks, intervaloTicks);
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