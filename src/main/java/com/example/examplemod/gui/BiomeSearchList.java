package com.example.examplemod.gui;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

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

    // 设置滚动的条的位置
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }

    // 用于判断是否选中
    public boolean hasSelection() {
        return getSelected() != null;
    }

    // 宽度
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }
    // 获得最低端
    @Override
    protected int getRowBottom(int itemIndex) {
        return getRowTop(itemIndex) + itemHeight;
    }
    // 判断index是否选中
    @Override
    protected boolean isSelectedItem(int slotIndex) {
        return slotIndex >= 0 && slotIndex < children().size() ? children().get(slotIndex).equals(getSelected()) : false;
    }
    // 渲染list
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderList(guiGraphics, mouseX, mouseY, partialTicks);
    }
    // 渲染list
    @Override
    protected void renderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < getItemCount(); ++i) {
            int top = getRowTop(i);
            int bottom = getRowBottom(i);
            if (bottom >= y0 && top <= y1) {
                // 获得 第i个选项
                BiomeSearchEntry entry = getEntry(i);
                // 是否被选中
                if (isSelectedItem(i)) {
                    // 若被选中，则渲染颜色加深
                    final int insideLeft = x0 + width / 2 - getRowWidth() / 2 + 2;
                    guiGraphics.fill(insideLeft - 4, top - 4, insideLeft + getRowWidth() + 4, top + itemHeight, 255 / 2 << 24);
                }
                // 否则正常渲染
                entry.render(guiGraphics, i, top, getRowLeft(), getRowWidth(), itemHeight - 4, mouseX, mouseY, isMouseOver((double) mouseX, (double) mouseY) && Objects.equals(getEntryAtPosition((double) mouseX, (double) mouseY), entry), partialTicks);
            }
        }

        if (getMaxScroll() > 0) {
            int left = getScrollbarPosition();
            int right = left + 6;
            int height = (int) ((float) ((y1 - y0) * (y1 - y0)) / (float) getMaxPosition());
            height = Mth.clamp(height, 32, y1 - y0 - 8);
            int top = (int) getScrollAmount() * (y1 - y0 - height) / getMaxScroll() + y0;
            if (top < y0) {
                top = y0;
            }

            guiGraphics.fill(left, y0, right, y1, (int) (2.35F * 255.0F) / 2 << 24);
            guiGraphics.fill(left, top, right, top + height, (int) (1.9F * 255.0F) / 2 << 24);
        }
    }

    // 渲染List
    public void refreshList(){
        clearEntries();
        for (Biome biome : parentScreen.sortBiomes()) {
            addEntry(new BiomeSearchEntry(this, biome));
        }
        // 打开Screen之前将选中的选项清空
        selectBiome(null);
    }
    // 选中时候调用该方法
    public void selectBiome(BiomeSearchEntry entry) {
        setSelected(entry);
        parentScreen.selectBiome(entry);
        ExampleMod.LOGGER.info("===============ListClick");
    }
}
