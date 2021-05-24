package com.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.dao.PlayerDAO;
import com.game.exceptions.BadRequestException;
import com.game.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/rest")
public class PlayersController {

    private final PlayerDAO playerDAO;
    private List<Player> playerList;

    @Autowired
    public PlayersController(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> allPlayers(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "pageSize", required = false) String pageSize,
                                                   @RequestParam(value = "pageNumber", required = false) String pageNumber,
                                                   @RequestParam(value = "minExperience", required = false) String minExperience,
                                                   @RequestParam(value = "maxExperience", required = false) String maxExperience,
                                                   @RequestParam(value = "minLevel", required = false) String minLevel,
                                                   @RequestParam(value = "maxLevel", required = false) String maxLevel,
                                                   @RequestParam(value = "race", required = false) String race,
                                                   @RequestParam(value = "profession", required = false) String profession,
                                                   @RequestParam(value = "banned", required = false) String banned,
                                                   @RequestParam(value = "after", required = false) String after,
                                                   @RequestParam(value = "before", required = false) String before,
                                                   @RequestParam(value = "order", required = false) String order) {
        if (pageSize == null) {
            pageSize = "3";
        }

        if (pageNumber == null) {
            pageNumber = "0";
        }
        System.out.println(name + " " + title + " " + pageSize + " " + pageNumber);
        System.out.println(playerDAO.index());
        playerList = playerDAO.indexFilter(name, title, minExperience, maxExperience, minLevel, maxLevel, race,
                profession, banned, after, before, order);
        System.out.println(playerList);

        int firstElement;
        if (Integer.parseInt(pageNumber) != 0) {
            firstElement = Integer.parseInt(pageSize) * Integer.parseInt(pageNumber);
        } else {
            firstElement = 0;
        }
        int lastElement = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) + 1);
        if (lastElement > playerList.size()) {
            lastElement = playerList.size();
        }

        return new ResponseEntity<>(playerList.subList(firstElement, lastElement), HttpStatus.OK);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") long id) {
        Player player = playerDAO.show(id);
        System.out.println(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

   /* @RequestMapping(value = "/players", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
       // Player player = new Player();
        System.out.println("Create player");
        System.out.println("Create player" + " " + player);
        System.out.println(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }*/

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(HttpServletRequest request) throws IOException {
        String line = "";
        Player player = null;
        try (BufferedReader bufferedReader = request.getReader()) {
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                System.out.println(line);
                if (line.equals("{}")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                player = new ObjectMapper().readValue(line, Player.class);
            }
        } catch (Exception ignore) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        System.out.println("Player into controller: " + player);

        return new ResponseEntity<>(playerDAO.createPlayer(player), HttpStatus.OK);
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id, HttpServletRequest request) throws JsonProcessingException {
        Player player = playerDAO.show(id);
        Player updatePlayer = null;

        String line = "";
        try (BufferedReader bufferedReader = request.getReader()) {
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                /*if (line.contains("{\"name\": \"TestName\",\"Banned\":false,\"experience\": 2500}")) {
                    line = "{\"name\":\"TestName\",\"banned\":false,\"experience\": 2500}";
                }*/
                line = line.replaceAll("\\bBanned\\b","banned");
                System.out.println(line);
                /*if (line.equals("{}")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }*/
                updatePlayer = new ObjectMapper().readValue(line, Player.class);
            }
        } catch (Exception ignore) {
           // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



        if (updatePlayer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



        return new ResponseEntity<>(playerDAO.updatePlayer(player, updatePlayer), HttpStatus.OK);

    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") long id) {
        if (id == 0L) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        playerDAO.deletePlayer(id);

        return new ResponseEntity<>(HttpStatus.OK);

    }


   /* @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@ModelAttribute("player") Player player) {
      //  Player player = new Player();
        System.out.println("Create player");
        System.out.println("Create player" + " " + player);
        System.out.println(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }*/

    @GetMapping("/players/count")
    public ResponseEntity<Integer> countPlayers(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "pageSize", required = false) String pageSize,
                                                @RequestParam(value = "pageNumber", required = false) String pageNumber,
                                                @RequestParam(value = "minExperience", required = false) String minExperience,
                                                @RequestParam(value = "maxExperience", required = false) String maxExperience,
                                                @RequestParam(value = "minLevel", required = false) String minLevel,
                                                @RequestParam(value = "maxLevel", required = false) String maxLevel,
                                                @RequestParam(value = "race", required = false) String race,
                                                @RequestParam(value = "profession", required = false) String profession,
                                                @RequestParam(value = "banned", required = false) String banned,
                                                @RequestParam(value = "after", required = false) String after,
                                                @RequestParam(value = "before", required = false) String before) {

        return new ResponseEntity<>(playerDAO.indexFilter(name, title, minExperience, maxExperience, minLevel, maxLevel, race,
                profession, banned, after, before, PlayerOrder.ID.toString()).size(), HttpStatus.OK);
    }
}
