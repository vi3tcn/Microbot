package net.runelite.client.plugins.microbot.tempoross;

import net.runelite.api.TileObject;

public class MastInfo {
    public String type;
    public TileObject tileObject;
    public boolean repair;

    public MastInfo(String type, TileObject tileObject, boolean repair) {
        this.type = type;
        this.tileObject = tileObject;
        this.repair = repair;
    }
}