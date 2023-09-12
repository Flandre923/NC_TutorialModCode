package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchList extends ObjectSelectionList<BiomeSearchEntry> {

    private final NaturesCompassScrren parentScreen;
    public BiomeSearchList(NaturesCompassScrren parentScreen, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
        this.parentScreen = parentScreen;
        refreshList();
    }

    public NaturesCompassScrren getParentScreen() {
        return this.parentScreen;
    }

    public void refreshList(){
        clearEntries();
        for(int i =1;i<10;i++){
            addEntry(new BiomeSearchEntry(this ));
        }

    }
}
