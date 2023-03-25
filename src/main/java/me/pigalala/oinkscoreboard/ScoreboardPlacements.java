package me.pigalala.oinkscoreboard;

public enum ScoreboardPlacements {
    NORMAL(0),
    CORNER(1);

    final int id;
    ScoreboardPlacements(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
