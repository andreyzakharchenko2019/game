package com.game.models;

import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.Date;

public class Player {

    private long id;
    private String name;
    private String title;
    private Race race;
    private Profession profession;
    private int experience;
    private int level;
    private int untilNextLevel;
    private Date birthday;
    private boolean banned;

    public Player() {
    }

    public Player(long id, String name, String title, Race race, Profession profession, int experience, int level, int untilNextLevel, Date date, boolean banned) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.level = level;
        this.untilNextLevel = untilNextLevel;
        this.birthday = date;
        this.banned = banned;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(int untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "{\"id\":" + "\"" + id + "\"" +
                ",\"name\":" + "\"" + name + "\"" +
                ",\"title:\"" + "\"" + title + "\"" +
                ",\"race\":" + "\"" + race + "\"" +
                ",\"profession\":" + "\"" + profession + "\"" +
                ",\"experience\":" + "\"" + experience + "\"" +
                ",\"level\":" + "\"" + level + "\"" +
                ",\"untilNextLevel\":" + "\"" + untilNextLevel + "\"" +
                ",\"birthday\":" + "\"" + birthday.getTime() + "\"" +
                ",\"banned\":" + "\"" + banned + "\"" +
                '}';
    }
}
