package net.syn100.ecocraft;

import net.minecraft.world.item.Item;

public interface IFurnaceMixin {
    Item getCurrentFuel();

    void setCurrentFuel(Item fuel);

    int getLitTime();
}
