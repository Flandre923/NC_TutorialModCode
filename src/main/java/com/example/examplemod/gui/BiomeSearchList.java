package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchList extends ObjectSelectionList<BiomeSearchEntry> {
    // 列表用于之后显示生物群系
    private final NaturesCompassScrren parentScreen;
    public BiomeSearchList(NaturesCompassScrren parentScreen, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
        this.parentScreen = parentScreen;
        refreshList();// 显示列表项用的。
    }


    // 添加了一个方法用于返回父窗口，
    public NaturesCompassScrren getParentScreen() {
        return this.parentScreen;
    }



    // 渲染List
    public void refreshList(){
        clearEntries();
        for (Biome biome : parentScreen.sortBiomes()) {
            addEntry(new BiomeSearchEntry(this, biome));
        }
        selectBiome(null);
    }

    public void selectBiome(BiomeSearchEntry entry) {
        setSelected(entry);
        parentScreen.selectBiome(entry);
    }
}
