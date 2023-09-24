package com.example.examplemod.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransparentTextField extends EditBox {
    // 输入框
    private Font font;
    private Component label;
    public TransparentTextField(Font font, int x, int y, int width, int height, Component label) {
        super(font, x, y, width, height, label);
        this.font = font;
        this.label = label;
    }

}
