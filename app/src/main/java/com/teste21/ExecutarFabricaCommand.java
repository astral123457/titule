package com.teste21;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;

import java.sql.*;

public class ExecutarFabricaCommand implements CommandExecutor {

    private final Main plugin;

    public ExecutarFabricaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("❓ Use: /verificatnt <nomeDaFabrica>");
            return true;
        }

        String nome = args[0];
        Connection conn = plugin.getConnection();

        if (conn == null) {
            sender.sendMessage("❌ Banco de dados não conectado.");
            return true;
        }

        synchronized (conn) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM fabricas WHERE nome = ?")) {
                ps.setString(1, nome);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    sender.sendMessage("⚠️ Fábrica '" + nome + "' não encontrada.");
                    return true;
                }

                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    sender.sendMessage("🌍 Mundo '" + worldName + "' não está carregado.");
                    return true;
                }

                Location tntLoc = new Location(world, rs.getInt("tnt_x"), rs.getInt("tnt_y"), rs.getInt("tnt_z"));
                Location observerLoc = new Location(world, rs.getInt("observer_x"), rs.getInt("observer_y"), rs.getInt("observer_z"));

                Block tntBlock = tntLoc.getBlock();
                Block observerBlock = observerLoc.getBlock();

                if (tntBlock.getType() == Material.OBSERVER) {
                    tntBlock.setType(Material.AIR);
                    observerBlock.setType(Material.OBSERVER);
                    sender.sendMessage("🔁 Observador movido para " + formatLoc(observerLoc));

                    tntBlock.setType(Material.TNT);
                    sender.sendMessage("💣 TNT recolocada em " + formatLoc(tntLoc));
                } else {
                    sender.sendMessage("ℹ️ Nenhum OBSERVER encontrado na posição da TNT.");
                }

            } catch (SQLException e) {
                sender.sendMessage("❌ Erro ao executar fábrica.");
                plugin.getLogger().severe("Erro ao executar fábrica '" + nome + "':");
                e.printStackTrace();
            }
        }

        return true;
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}