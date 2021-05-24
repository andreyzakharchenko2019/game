package com.game.dao;

import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.models.Player;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMapper implements RowMapper<Player> {
    @Override
    public Player mapRow(ResultSet rs, int rowNum) {
        Player player = new Player();
        try {
            player.setId(rs.getInt("id"));
            player.setName(rs.getString("name"));
            player.setTitle(rs.getString("title"));
            player.setRace((Race) rs.getObject("race"));
            player.setProfession((Profession) rs.getObject("profession"));
            player.setExperience(rs.getInt("experience"));
            player.setLevel(rs.getInt("level"));
            player.setUntilNextLevel(rs.getInt("untilNextLevel"));
            player.setBirthday(rs.getDate("birthday"));
            player.setBanned(rs.getBoolean("banned"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return player;
    }
}
