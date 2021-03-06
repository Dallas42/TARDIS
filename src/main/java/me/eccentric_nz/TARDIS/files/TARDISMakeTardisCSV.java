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
package me.eccentric_nz.TARDIS.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.TARDISConstants;

/**
 * Cybermen are a "race" of cybernetically augmented humanoid. They vary greatly
 * in design, with different factions throughout time and space.
 *
 * @author eccentric_nz
 */
public class TARDISMakeTardisCSV {

    private final TARDIS plugin;
    TARDISInteriorSchematicReader reader;

    public TARDISMakeTardisCSV(TARDIS plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads CSV data into a 3D array for use by the TARDIS builder classes. If
     * the required files are not present in the TARDIS plugin folder, then they
     * are created. The WorldEdit schematics are first read and converted to CSV
     * format, then the CSV data is loaded into an array. This allows server
     * administrators to use their own schematic files.
     */
    public void loadCSV() {
        try {
            File schematicDir = new File(plugin.getDataFolder() + File.separator + "schematics");
            if (!schematicDir.exists()) {
                boolean result = schematicDir.mkdir();
                if (result) {
                    schematicDir.setWritable(true);
                    schematicDir.setExecutable(true);
                    plugin.console.sendMessage(plugin.pluginName + "Created schematics directory.");
                }
            }
            // load tardisCSV files - create them if they don't exist
            plugin.biggerSchematicCSV = createFile(TARDISConstants.SCHEMATIC_BIGGER + ".csv");
            plugin.budgetSchematicCSV = createFile(TARDISConstants.SCHEMATIC_BUDGET + ".csv");
            plugin.deluxeSchematicCSV = createFile(TARDISConstants.SCHEMATIC_DELUXE + ".csv");
            plugin.eleventhSchematicCSV = createFile(TARDISConstants.SCHEMATIC_ELEVENTH + ".csv");
            plugin.redstoneSchematicCSV = createFile(TARDISConstants.SCHEMATIC_REDSTONE + ".csv");
            reader = new TARDISInteriorSchematicReader(plugin);
            // load schematic files - copy the defaults if they don't exist
            String basepath = plugin.getDataFolder() + File.separator + "schematics" + File.separator;
            String bignstr = basepath + TARDISConstants.SCHEMATIC_BIGGER;
            String budnstr = basepath + TARDISConstants.SCHEMATIC_BUDGET;
            String delnstr = basepath + TARDISConstants.SCHEMATIC_DELUXE;
            String elenstr = basepath + TARDISConstants.SCHEMATIC_ELEVENTH;
            String rednstr = basepath + TARDISConstants.SCHEMATIC_REDSTONE;
            plugin.biggerSchematicFile = copy(bignstr, plugin.getResource(TARDISConstants.SCHEMATIC_BIGGER));
            plugin.budgetSchematicFile = copy(budnstr, plugin.getResource(TARDISConstants.SCHEMATIC_BUDGET));
            plugin.deluxeSchematicFile = copy(delnstr, plugin.getResource(TARDISConstants.SCHEMATIC_DELUXE));
            plugin.eleventhSchematicFile = copy(elenstr, plugin.getResource(TARDISConstants.SCHEMATIC_ELEVENTH));
            plugin.redstoneSchematicFile = copy(rednstr, plugin.getResource(TARDISConstants.SCHEMATIC_REDSTONE));

            // copy default room files as well
            String antnstr = basepath + TARDISConstants.SCHEMATIC_ANTIGRAVITY;
            String arbornstr = basepath + TARDISConstants.SCHEMATIC_ARBORETUM;
            String baknstr = basepath + TARDISConstants.SCHEMATIC_BAKER;
            String bednstr = basepath + TARDISConstants.SCHEMATIC_BEDROOM;
            String cronstr = basepath + TARDISConstants.SCHEMATIC_CROSS;
            String empnstr = basepath + TARDISConstants.SCHEMATIC_EMPTY;
            String farnstr = basepath + TARDISConstants.SCHEMATIC_FARM;
            String granstr = basepath + TARDISConstants.SCHEMATIC_GRAVITY;
            String grenstr = basepath + TARDISConstants.SCHEMATIC_GREENHOUSE;
            String harnstr = basepath + TARDISConstants.SCHEMATIC_HARMONY;
            String kitnstr = basepath + TARDISConstants.SCHEMATIC_KITCHEN;
            String libnstr = basepath + TARDISConstants.SCHEMATIC_LIBRARY;
            String lonnstr = basepath + TARDISConstants.SCHEMATIC_LONG;
            String musnstr = basepath + TARDISConstants.SCHEMATIC_MUSHROOM;
            String passnstr = basepath + TARDISConstants.SCHEMATIC_PASSAGE;
            String poolnstr = basepath + TARDISConstants.SCHEMATIC_POOL;
            String vaunstr = basepath + TARDISConstants.SCHEMATIC_VAULT;
            String woonstr = basepath + TARDISConstants.SCHEMATIC_WOOD;
            String wornstr = basepath + TARDISConstants.SCHEMATIC_WORKSHOP;
            copy(antnstr, plugin.getResource(TARDISConstants.SCHEMATIC_ANTIGRAVITY));
            copy(arbornstr, plugin.getResource(TARDISConstants.SCHEMATIC_ARBORETUM));
            copy(baknstr, plugin.getResource(TARDISConstants.SCHEMATIC_BAKER));
            copy(bednstr, plugin.getResource(TARDISConstants.SCHEMATIC_BEDROOM));
            copy(cronstr, plugin.getResource(TARDISConstants.SCHEMATIC_CROSS));
            copy(empnstr, plugin.getResource(TARDISConstants.SCHEMATIC_EMPTY));
            copy(farnstr, plugin.getResource(TARDISConstants.SCHEMATIC_FARM));
            copy(granstr, plugin.getResource(TARDISConstants.SCHEMATIC_GRAVITY));
            copy(grenstr, plugin.getResource(TARDISConstants.SCHEMATIC_GREENHOUSE));
            copy(harnstr, plugin.getResource(TARDISConstants.SCHEMATIC_HARMONY));
            copy(kitnstr, plugin.getResource(TARDISConstants.SCHEMATIC_KITCHEN));
            copy(libnstr, plugin.getResource(TARDISConstants.SCHEMATIC_LIBRARY));
            copy(lonnstr, plugin.getResource(TARDISConstants.SCHEMATIC_LONG));
            copy(musnstr, plugin.getResource(TARDISConstants.SCHEMATIC_MUSHROOM));
            copy(passnstr, plugin.getResource(TARDISConstants.SCHEMATIC_PASSAGE));
            copy(poolnstr, plugin.getResource(TARDISConstants.SCHEMATIC_POOL));
            copy(vaunstr, plugin.getResource(TARDISConstants.SCHEMATIC_VAULT));
            copy(woonstr, plugin.getResource(TARDISConstants.SCHEMATIC_WOOD));
            copy(wornstr, plugin.getResource(TARDISConstants.SCHEMATIC_WORKSHOP));

            // read the schematics and make the CSV files
            reader.readAndMakeInteriorCSV(bignstr, TARDISConstants.SCHEMATIC.BIGGER);
            reader.readAndMakeInteriorCSV(budnstr, TARDISConstants.SCHEMATIC.BUDGET);
            reader.readAndMakeInteriorCSV(delnstr, TARDISConstants.SCHEMATIC.DELUXE);
            reader.readAndMakeInteriorCSV(elenstr, TARDISConstants.SCHEMATIC.ELEVENTH);
            reader.readAndMakeInteriorCSV(rednstr, TARDISConstants.SCHEMATIC.REDSTONE);
            // load the schematic data from the tardisCSV files
            plugin.biggerschematic = TARDISSchematic.schematic(plugin.biggerSchematicCSV, plugin.biggerdimensions[0], plugin.biggerdimensions[1], plugin.biggerdimensions[2]);
            plugin.budgetschematic = TARDISSchematic.schematic(plugin.budgetSchematicCSV, plugin.budgetdimensions[0], plugin.budgetdimensions[1], plugin.budgetdimensions[2]);
            plugin.deluxeschematic = TARDISSchematic.schematic(plugin.deluxeSchematicCSV, plugin.deluxedimensions[0], plugin.deluxedimensions[1], plugin.deluxedimensions[2]);
            plugin.eleventhschematic = TARDISSchematic.schematic(plugin.eleventhSchematicCSV, plugin.eleventhdimensions[0], plugin.eleventhdimensions[1], plugin.eleventhdimensions[2]);
            plugin.redstoneschematic = TARDISSchematic.schematic(plugin.redstoneSchematicCSV, plugin.redstonedimensions[0], plugin.redstonedimensions[1], plugin.redstonedimensions[2]);
        } catch (Exception e) {
            plugin.console.sendMessage(plugin.pluginName + "Failed to retrieve files from directory. Using defaults.");
        }
    }

    /**
     * Tries to find the specified CSV file. If it doesn't exist, an empty file
     * is created.
     */
    public File createFile(String filename) {
        File file = new File(plugin.getDataFolder() + File.separator + "schematics" + File.separator, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException io) {
                plugin.console.sendMessage(plugin.pluginName + filename + " could not be created! " + io.getMessage());
            }
        }
        return file;
    }

    /**
     * Copies the schematic file to the TARDIS plugin directory if it is not
     * present.
     */
    public File copy(String filepath, InputStream in) {
        File file = new File(filepath);
        if (!file.exists()) {
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                try {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException io) {
                    plugin.console.sendMessage(plugin.pluginName + "Could not save the file (" + file.toString() + ").");
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                plugin.console.sendMessage(plugin.pluginName + "File not found.");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return file;
    }
}
