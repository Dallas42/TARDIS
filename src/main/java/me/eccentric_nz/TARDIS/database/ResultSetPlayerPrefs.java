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
package me.eccentric_nz.TARDIS.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import me.eccentric_nz.TARDIS.TARDIS;

/**
 * Many facts, figures, and formulas are contained within the Matrix,
 * including... the personal preferences of the Time lords themselves.
 *
 * @author eccentric_nz
 */
public class ResultSetPlayerPrefs {

    private TARDISDatabase service = TARDISDatabase.getInstance();
    private Connection connection = service.getConnection();
    private TARDIS plugin;
    private HashMap<String, Object> where;
    private int pp_id;
    private String player;
    private String key;
    private boolean sfx_on;
    private boolean platform_on;
    private boolean quotes_on;
    private boolean auto_on;
    private int artron_level;
    private String wall;
    private String floor;

    /**
     * Creates a class instance that can be used to retrieve an SQL ResultSet
     * from the player_prefs table.
     *
     * @param plugin an instance of the main class.
     * @param where a HashMap<String, Object> of table fields and values to
     * refine the search.
     */
    public ResultSetPlayerPrefs(TARDIS plugin, HashMap<String, Object> where) {
        this.plugin = plugin;
        this.where = where;
    }

    /**
     * Retrieves an SQL ResultSet from the player_prefs table. This method
     * builds an SQL query string from the parameters supplied and then executes
     * the query. Use the getters to retrieve the results.
     */
    public boolean resultSet() {
        PreparedStatement statement = null;
        ResultSet rs = null;
        String wheres = "";
        if (where != null) {
            StringBuilder sbw = new StringBuilder();
            for (Map.Entry<String, Object> entry : where.entrySet()) {
                sbw.append(entry.getKey()).append(" = ? AND ");
            }
            wheres = " WHERE " + sbw.toString().substring(0, sbw.length() - 5);
        }
        String query = "SELECT * FROM player_prefs" + wheres;
        //plugin.debug(query);
        try {
            statement = connection.prepareStatement(query);
            if (where != null) {
                int s = 1;
                for (Map.Entry<String, Object> entry : where.entrySet()) {
                    if (entry.getValue().getClass().equals(String.class)) {
                        statement.setString(s, entry.getValue().toString());
                    } else {
                        statement.setInt(s, plugin.utils.parseNum(entry.getValue().toString()));
                    }
                    s++;
                }
                where.clear();
            }
            rs = statement.executeQuery();
            if (rs.next()) {
                this.pp_id = rs.getInt("pp_id");
                this.player = rs.getString("player");
                this.key = rs.getString("key");
                this.sfx_on = rs.getBoolean("sfx_on");
                this.platform_on = rs.getBoolean("platform_on");
                this.quotes_on = rs.getBoolean("quotes_on");
                this.auto_on = rs.getBoolean("auto_on");
                this.artron_level = rs.getInt("artron_level");
                this.wall = rs.getString("wall");
                this.floor = rs.getString("floor");
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.debug("ResultSet error for player_prefs table! " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                plugin.debug("Error closing player_prefs table! " + e.getMessage());
            }
        }
        return true;
    }

    public int getPp_id() {
        return pp_id;
    }

    public String getPlayer() {
        return player;
    }

    public String getKey() {
        return key;
    }

    public boolean isSFX_on() {
        return sfx_on;
    }

    public boolean isPlatform_on() {
        return platform_on;
    }

    public boolean isQuotes_on() {
        return quotes_on;
    }

    public boolean isAuto_on() {
        return auto_on;
    }

    public int getArtron_level() {
        return artron_level;
    }

    public String getWall() {
        return wall;
    }

    public String getFloor() {
        return floor;
    }
}
