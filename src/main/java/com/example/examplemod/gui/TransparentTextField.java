package com.example.examplemod.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;


public class TransparentTextField extends EditBox {
    private Font font;
    private Component label;
    public TransparentTextField(Font font, int x, int y, int width, int height, Component label) {
        super(font, x, y, width, height, label);
        this.font = font;
        this.label = label;
    }

    @Override
    public void render(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
        super.render(p_282421_, p_93658_, p_93659_, p_93660_);
    }
}
