package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchEntry extends ObjectSelectionList.Entry<BiomeSearchEntry>{

    private final Minecraft mc;
    private final NaturesCompassScrren parentScreen;
//    private final Biome biome;
    private final BiomeSearchList biomesList;
//    private final String tags;
    private long lastClickTime;
    public BiomeSearchEntry(BiomeSearchList biomesList) {
        this.biomesList = biomesList;
//        this.biome = biome;
        parentScreen = biomesList.getParentScreen();
        mc = Minecraft.getInstance();
    }

    @Override
    public Component getNarration() {
        return Component.literal("hello");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int par6, int par7, boolean par8, float par9) {
        guiGraphics.drawString(mc.font, Component.literal("hahha"), left + 1, top + 1, 0xffffff);

    }
}
