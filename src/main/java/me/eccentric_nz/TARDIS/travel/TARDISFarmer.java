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
package me.eccentric_nz.TARDIS.travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.TARDISConstants.COMPASS;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Undefined Storage Holds make up most of a TARDIS's interior volume. Each Hold
 * has an identifying number.
 *
 * @author eccentric_nz
 */
public class TARDISFarmer {

    private final TARDIS plugin;

    public TARDISFarmer(TARDIS plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks whether there are any animals inside the TARDIS Police Box. If
     * mobs are present they are teleported to the 'farm' room (if present),
     * otherwise a spawn egg for the mob type is placed in the player's
     * inventory. Only cows, sheep, pigs and chickens will be processed.
     *
     * @param l The location to check for farm animals. This will be the current
     * location of the TARDIS Police Box.
     * @param id The database key of the TARDIS.
     */
    public void farmAnimals(Location l, COMPASS d, int id, Player p) {
        switch (d) {
            case NORTH:
                l.setZ(l.getZ() - 1);
                break;
            case WEST:
                l.setX(l.getX() - 1);
                break;
            case SOUTH:
                l.setZ(l.getZ() + 1);
                break;
            default:
                l.setX(l.getX() + 1);
                break;
        }
        l.setY(l.getY() + 1);
        List<Entity> old_macd_had_a_chicken = new ArrayList<Entity>();
        List<Entity> old_macd_had_a_cow = new ArrayList<Entity>();
        List<Entity> old_macd_had_a_pig = new ArrayList<Entity>();
        List<Entity> old_macd_had_a_sheep = new ArrayList<Entity>();
        // spawn an entity at this location so we can get nearby entities - an egg will do
        World w = l.getWorld();
        Entity ent = w.spawnEntity(l, EntityType.EGG);
        List<Entity> mobs = ent.getNearbyEntities(3.5D, 3.5D, 3.5D);
        for (Entity e : mobs) {
            switch (e.getType()) {
                case CHICKEN:
                    old_macd_had_a_chicken.add(e);
                    break;
                case COW:
                    old_macd_had_a_cow.add(e);
                    break;
                case PIG:
                    old_macd_had_a_pig.add(e);
                    break;
                case SHEEP:
                    old_macd_had_a_sheep.add(e);
                    break;
                default:
                    break;
            }
        }
        ent.remove();
        // is there a farm room?
        HashMap<String, Object> where = new HashMap<String, Object>();
        where.put("tardis_id", id);
        ResultSetTardis rs = new ResultSetTardis(plugin, where, "", false);
        if (rs.resultSet()) {
            String farm = rs.getFarm();
            if (!farm.equals("")) {
                // get location of farm room
                String[] data = farm.split(":");
                World world = plugin.getServer().getWorld(data[0]);
                int x = plugin.utils.parseNum(data[1]);
                int y = plugin.utils.parseNum(data[2]) + 1;
                int z = plugin.utils.parseNum(data[3]);
                if (old_macd_had_a_chicken.size() > 0) {
                    Location chicken_pen = new Location(world, x + 3, y, z - 3);
                    while (!world.getChunkAt(chicken_pen).isLoaded()) {
                        world.getChunkAt(chicken_pen).load();
                    }
                    for (Entity e : old_macd_had_a_chicken) {
                        plugin.myspawn = true;
                        world.spawnEntity(chicken_pen, EntityType.CHICKEN);
                        e.remove();
                    }
                }
                if (old_macd_had_a_cow.size() > 0) {
                    Location cow_pen = new Location(world, x + 3, y, z + 3);
                    while (!world.getChunkAt(cow_pen).isLoaded()) {
                        world.getChunkAt(cow_pen).load();
                    }
                    for (Entity e : old_macd_had_a_cow) {
                        plugin.myspawn = true;
                        world.spawnEntity(cow_pen, EntityType.COW);
                        e.remove();
                    }
                }
                if (old_macd_had_a_pig.size() > 0) {
                    Location pig_pen = new Location(world, x - 3, y, z - 3);
                    while (!world.getChunkAt(pig_pen).isLoaded()) {
                        world.getChunkAt(pig_pen).load();
                    }
                    for (Entity e : old_macd_had_a_pig) {
                        plugin.myspawn = true;
                        world.spawnEntity(pig_pen, EntityType.PIG);
                        e.remove();
                    }
                }
                if (old_macd_had_a_sheep.size() > 0) {
                    Location sheep_pen = new Location(world, x - 3, y, z + 3);
                    while (!world.getChunkAt(sheep_pen).isLoaded()) {
                        world.getChunkAt(sheep_pen).load();
                    }
                    for (Entity e : old_macd_had_a_sheep) {
                        plugin.myspawn = true;
                        world.spawnEntity(sheep_pen, EntityType.SHEEP);
                        e.remove();
                    }
                }
            } else {
                // no farm, give the player spawn eggs
                Inventory inv = p.getInventory();
                if (old_macd_had_a_chicken.size() > 0) {
                    for (Entity e : old_macd_had_a_chicken) {
                        ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 93);
                        inv.addItem(is);
                        e.remove();
                    }
                }
                if (old_macd_had_a_cow.size() > 0) {
                    for (Entity e : old_macd_had_a_cow) {
                        ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 92);
                        inv.addItem(is);
                        e.remove();
                    }
                }
                if (old_macd_had_a_pig.size() > 0) {
                    for (Entity e : old_macd_had_a_pig) {
                        ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 90);
                        inv.addItem(is);
                        e.remove();
                    }
                }
                if (old_macd_had_a_sheep.size() > 0) {
                    for (Entity e : old_macd_had_a_sheep) {
                        ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 91);
                        inv.addItem(is);
                        e.remove();
                    }
                }
                p.updateInventory();
            }
        }
    }
}
