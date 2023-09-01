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

    public TransaprentButton(int x, int y, int width, int height, Component label, net.minecraft.client.gui.components.Button.OnPress press){
        super(x,y,width,height,label,press,DEFAULT_NARRATION);
    }

//    @Override
//    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
//        if(visible){
//            Minecraft mc = Minecraft.getInstance();
//            float state = 2;
//            if(!active){
//                state  =5;
//            }else if(isHovered){
//                state =4;
//            }
//            final float f = state/2*0.9f+0.1f;
//            final int color = (int)(255.0f * f);
//            guiGraphics.fill(getX(),);
//        }
//    }
}
