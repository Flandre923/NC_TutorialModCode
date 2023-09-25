package com.example.examplemod.gui;

import com.example.examplemod.util.BiomeUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSearchEntry extends ObjectSelectionList.Entry<BiomeSearchEntry>{
    // 列表的每一项
    // 以下的几个字段是我们之后会使用的。
    private final Minecraft mc;
    private final NaturesCompassScrren parentScreen;
    private final Biome biome;
    private final BiomeSearchList biomesList;
    private long lastClickTime;
    public BiomeSearchEntry(BiomeSearchList biomesList, Biome biome) {
        this.biomesList = biomesList;
        this.biome = biome;
        parentScreen = biomesList.getParentScreen();
        mc = Minecraft.getInstance();
    }
    // 当选项被点击时候回调
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // 选中
            biomesList.selectBiome(this);
        }
        return false;
    }
    @Override
    public Component getNarration() {
        return Component.literal("hello");
    }

    // 这render方法就是表示了这个列表中的一个选项的显示的内容
    // 分别是字体，内容，位置，和颜色
    // 0xffffff 白色。RGB
    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int par6, int par7, boolean par8, float par9) {
        guiGraphics.drawString(mc.font, Component.literal(BiomeUtils.getBiomeNameForDisplay(parentScreen.level, biome)), left + 1, top + 1, 0xffffff);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
