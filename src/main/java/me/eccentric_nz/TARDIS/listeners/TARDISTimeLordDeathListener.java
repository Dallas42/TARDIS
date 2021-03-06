/*
 * Copyright (C) 2013 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.TARDIS.listeners;

import java.util.HashMap;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.TARDISConstants.COMPASS;
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetAreas;
import me.eccentric_nz.TARDIS.database.ResultSetPlayerPrefs;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Several events can trigger an Automatic Emergency Landing. Under these
 * circumstances a TARDIS will use the coordinate override to initiate an
 * Automatic Emergency Landing on the "nearest" available habitable planet.
 *
 * @author eccentric_nz
 */
public class TARDISTimeLordDeathListener implements Listener {

    private final TARDIS plugin;

    public TARDISTimeLordDeathListener(TARDIS plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens for player death. If the player is a time lord and the autonomous
     * circuit is engaged, then the TARDIS will automatically return to its
     * 'home' location, or the nearest Recharge area.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTimeLordDeath(PlayerDeathEvent event) {
        if (plugin.getConfig().getBoolean("allow_autonomous")) {
            final Player player = event.getEntity();
            if (player.hasPermission("tardis.autonomous")) {
                String playerNameStr = player.getName();
                HashMap<String, Object> where = new HashMap<String, Object>();
                where.put("owner", playerNameStr);
                ResultSetTardis rs = new ResultSetTardis(plugin, where, "", false);
                // are they a time lord?
                if (rs.resultSet()) {
                    HashMap<String, Object> wherep = new HashMap<String, Object>();
                    wherep.put("player", playerNameStr);
                    ResultSetPlayerPrefs rsp = new ResultSetPlayerPrefs(plugin, wherep);
                    if (rsp.resultSet()) {
                        // do they have the autonomous circuit on?
                        if (rsp.isAuto_on()) {
                            Location death_loc = player.getLocation();
                            String death_world = death_loc.getWorld().getName();
                            int id = rs.getTardis_id();
                            // where is the TARDIS Police Box?
                            String save = (plugin.tardisHasDestination.containsKey(id)) ? rs.getCurrent() : rs.getSave();
                            String[] save_data = save.split(":");
                            World sw = plugin.getServer().getWorld(save_data[0]);
                            int sx = plugin.utils.parseNum(save_data[1]);
                            int sy = plugin.utils.parseNum(save_data[2]);
                            int sz = plugin.utils.parseNum(save_data[3]);
                            Location sl = new Location(sw, sx, sy, sz);
                            // where is home?
                            String home = rs.getHome();
                            String[] home_data = home.split(":");
                            World hw = plugin.getServer().getWorld(home_data[0]);
                            int hx = plugin.utils.parseNum(home_data[1]);
                            int hy = plugin.utils.parseNum(home_data[2]);
                            int hz = plugin.utils.parseNum(home_data[3]);
                            Location home_loc = new Location(hw, hx, hy, hz);
                            Location goto_loc;
                            // if home world is NOT the death world
                            if (!home_data[0].equals(death_world)) {
                                // look for a recharge location
                                goto_loc = getRecharger(death_world, player);
                                if (goto_loc == null) {
                                    // no parking spots - default to TARDIS home location
                                    goto_loc = home_loc;
                                }
                            } else {
                                // died in home world get closest location
                                Location recharger = getRecharger(death_world, player);
                                if (recharger != null) {
                                    // which is closer?
                                    goto_loc = (death_loc.distanceSquared(home_loc) > death_loc.distanceSquared(recharger)) ? recharger : home_loc;
                                } else {
                                    // no parking spots - set to TARDIS home location
                                    goto_loc = home_loc;
                                }
                            }
                            // destroy police box
                            COMPASS d = rs.getDirection();
                            plugin.destroyPB.destroyPlatform(rs.getPlatform(), id);
                            plugin.destroyPB.destroySign(sl, d);
                            plugin.destroyPB.destroyTorch(sl);
                            plugin.destroyPB.destroyPoliceBox(sl, d, id, false);
                            // rebuild police box
                            plugin.buildPB.buildPoliceBox(id, goto_loc, d, rs.isChamele_on(), player, false);
                            String save_loc = goto_loc.getWorld().getName() + ":" + goto_loc.getBlockX() + ":" + goto_loc.getBlockY() + ":" + goto_loc.getBlockZ();
                            QueryFactory qf = new QueryFactory(plugin);
                            HashMap<String, Object> tid = new HashMap<String, Object>();
                            HashMap<String, Object> set = new HashMap<String, Object>();
                            tid.put("tardis_id", id);
                            set.put("save", save_loc);
                            qf.doUpdate("tardis", set, tid);
                            HashMap<String, Object> wherea = new HashMap<String, Object>();
                            wherea.put("tardis_id", id);
                            int amount = plugin.getConfig().getInt("autonomous") * -1;
                            qf.alterEnergyLevel("tardis", amount, wherea, player);
                        }
                    }
                }
            }
        }
    }

    private Location getRecharger(String world, Player player) {
        Location l = null;
        HashMap<String, Object> wherea = new HashMap<String, Object>();
        wherea.put("world", world);
        ResultSetAreas rsa = new ResultSetAreas(plugin, wherea, false);
        if (rsa.resultSet()) {
            String area = rsa.getArea_name();
            if (!player.hasPermission("tardis.area." + area) || !player.isPermissionSet("tardis.area." + area)) {
                return null;
            }
            l = plugin.ta.getNextSpot(rsa.getArea_name());
        }
        return l;
    }
}
