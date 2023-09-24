package com.example.examplemod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.data.LanguageProvider;

@OnlyIn(Dist.CLIENT)
public class TransaprentButton extends Button {

    // 按钮
    public TransaprentButton(int x, int y, int width, int height, Component label, net.minecraft.client.gui.components.Button.OnPress press){
        super(x,y,width,height,label,press,DEFAULT_NARRATION);
    }


}
