package com.witchcraft.data;

/**
 * Represents a rank within a coven.
 * Hierarchy: PRIEST/PRIESTESS > COUNCIL > INITIATE
 */
public enum CovenRank {

    PRIEST("Priest", "Priest", 3),
    PRIESTESS("Priestess", "Priestess", 3),
    COUNCIL("Council", "Council Member", 2),
    INITIATE("Initiate", "Initiate", 0);

    private final String name;
    private final String title;
    private final int authorityLevel;

    CovenRank(String name, String title, int authorityLevel) {
        this.name = name;
        this.title = title;
        this.authorityLevel = authorityLevel;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getAuthorityLevel() {
        return authorityLevel;
    }

    public boolean isLeader() {
        return this == PRIEST || this == PRIESTESS;
    }

    public boolean isAtLeast(CovenRank other) {
        return this.authorityLevel >= other.authorityLevel;
    }
}
