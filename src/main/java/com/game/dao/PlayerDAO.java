package com.game.dao;

import com.game.controller.PlayerOrder;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.NotFoundException;
import com.game.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PlayerDAO {

    private List<Player> players;

    private final JdbcTemplate jdbcTemplate;

   /* {
        players = new ArrayList<>(); //'Ниус', 'Приходящий Без Шума', 'HOBBIT', 'ROGUE', '2010-10-12', false, 58347, 33, 1153
        players.add(new Player(1, "Ниус", "Приходящий Без Шума", Race.HOBBIT, Profession.ROGUE, 58347, 33, 1153, new Date(), false));
        players.add(new Player(2, "Никрашш", "Приходящий Без Шума", Race.HUMAN, Profession.DRUID, 5847, 48, 153, new Date(), false));
        players.add(new Player(3, "Эззэссэль", "Приходящий Без Шума", Race.GIANT, Profession.PALADIN, 12838, 13, 7566, new Date(), false));
    }*/

    @Autowired
    public PlayerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Player> index() {
        return jdbcTemplate.query("SELECT * FROM player", new BeanPropertyRowMapper<>(Player.class));
    }

    public List<Player> indexFilter(String name, String title, String minExperience, String maxExperience,
                                    String minLevel, String maxLevel, String race, String profession, String banned,
                                    String after, String before, String order) {
        String sqlQuery = "SELECT * FROM player ";
        List<Object> parameterList = new ArrayList<>();
        if (name != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "name LIKE ? AND ";
            parameterList.add("%" + name + "%");
        }
        if (title != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "title LIKE ? AND ";
            parameterList.add("%" + title + "%");
        }
        if (minExperience != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "experience >= ? AND ";
            parameterList.add(minExperience);
        }
        if (maxExperience != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "experience <= ? AND ";
            parameterList.add(maxExperience);
        }
        if (minLevel != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "level >= ? AND ";
            parameterList.add(minLevel);
        }
        if (maxLevel != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "level <= ? AND ";
            parameterList.add(maxLevel);
        }
        if (race != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "race = ? AND ";
            parameterList.add(race);
        }

        if (profession != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "profession = ? AND ";
            parameterList.add(profession);
        }

        if (banned != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            sqlQuery += "banned = ? AND ";
            parameterList.add(banned);
        }
        if (after != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            sqlQuery += "birthday >= ? AND ";
            parameterList.add(simpleDateFormat.format(new Date(Long.parseLong(after))));
        }
        if (before != null) {
            if (!sqlQuery.contains("WHERE")) {
                sqlQuery += "WHERE ";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            sqlQuery += "birthday <= ? AND ";
            parameterList.add(simpleDateFormat.format(new Date(Long.parseLong(before))));
        }

        if (sqlQuery.endsWith(" AND ")) {
            sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 5);
        }

        if (order == null) {
            sqlQuery += " ORDER BY " + PlayerOrder.ID;
        } else {
            sqlQuery += " ORDER BY " + PlayerOrder.valueOf(order);
        }


        return jdbcTemplate.query(sqlQuery, parameterList.toArray(), new BeanPropertyRowMapper<>(Player.class));

/*        return jdbcTemplate.query("SELECT * FROM player WHERE name LIKE COALESCE(NULLIF(?, ''), ?)",
                new Object[]{"%" + name + "%", "%" + title + "%"}, new BeanPropertyRowMapper<>(Player.class));*/
    }

    public Player show(long id) {
        if (id == 0) {
            throw new BadRequestException();
        }
        List<Player> playerList = jdbcTemplate.query("SELECT * FROM player WHERE id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Player.class));
        if (playerList.size() > 0) {
            return playerList.get(0);
        } else {
            throw new NotFoundException();
        }
        //return playerList.stream().filter(player -> playerList.get((int) id).equals(id)).findFirst().orElseThrow(NotFoundException::new);
        // return players.stream().filter(player -> player.getId() == id).findAny().orElse(null);
    }

    public Player createPlayer(Player player) {
        if (player.getName().length() >= 12 || player.getName().length() == 0) {
            throw new BadRequestException();
        }
        if (player.getTitle().length() >= 30) {
            throw new BadRequestException();
        }

        if (player.getExperience() <= 0 || player.getExperience() >= 10000000) {
            throw new BadRequestException();
        }

        if (player.getBirthday().getTime() < 0) {
            throw new BadRequestException();
        }

        System.out.println("playerDAO createPlayer");

        currentLevel(player, player.getExperience());

        jdbcTemplate.update("INSERT INTO player(name, title, race, profession, birthday, banned, " +
                        "experience, level, untilNextLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",
                player.getName(), player.getTitle(), player.getRace().name(), player.getProfession().name(),
                player.getBirthday(), player.isBanned(), player.getExperience(), player.getLevel(),
                player.getUntilNextLevel());


        return index().get(index().size() - 1);
    }

    public Player updatePlayer(Player player, Player updatePlayer) {

        if (updatePlayer.getName() != null && updatePlayer.getName().length() > 0) {
            player.setName(updatePlayer.getName());
        }
        if (updatePlayer.getTitle() != null) {
            player.setTitle(updatePlayer.getTitle());
        }
        if (updatePlayer.getRace() != null) {
            player.setRace(updatePlayer.getRace());
        }
        if (updatePlayer.getProfession() != null) {
            player.setProfession(updatePlayer.getProfession());
        }
        if (updatePlayer.getBirthday() != null) {
            player.setBirthday(updatePlayer.getBirthday());
        }
        if (updatePlayer.isBanned() != player.isBanned()) {
            player.setBanned(updatePlayer.isBanned());
        }
        if (updatePlayer.getExperience() != 0) {
            player.setExperience(updatePlayer.getExperience());
        }


        if (player.getBirthday().getTime() < 0) {
            throw new BadRequestException();
        }

        if (player.getExperience() <= 0 || player.getExperience() >= 10000000) {
            throw new BadRequestException();
        }

        currentLevel(player, player.getExperience());
        jdbcTemplate.update("UPDATE player SET name=?, title=?, race=?, profession=?, birthday=?, banned=?, " +
                        "experience=?, level=?, untilNextLevel=? WHERE id=?", player.getName(), player.getTitle(), player.getRace().name(),
                player.getProfession().name(), player.getBirthday(), player.isBanned(), player.getExperience(), player.getLevel(),
                player.getUntilNextLevel(), player.getId());
        return player;

    }

    private void currentLevel(Player player, int experience) {
        player.setLevel((int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - experience);
    }

    public void deletePlayer(long id) {
        if (show(id) == null) {
            throw new BadRequestException();
        }
        jdbcTemplate.update("DELETE FROM player WHERE id=?", id);
    }
}
