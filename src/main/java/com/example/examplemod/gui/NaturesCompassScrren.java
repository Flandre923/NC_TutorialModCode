package com.example.examplemod.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NaturesCompassScrren extends Screen {


    private Button button;
    private EditBox editBox;
    public NaturesCompassScrren(Level level, Player player, ItemStack itemStack, Item item) {
        super(Component.literal("select biome"));
    }

    @Override
    protected void init() {
        setupWidgets();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font,Component.literal("Select Biome"),65,15,0xffffff);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void setupWidgets(){
        clearWidgets();
        button =  addRenderableWidget(new TransaprentButton(10,height-30,110,20,Component.literal("button"),(onPress)->{
            minecraft.setScreen(null);
        }));


        editBox = addRenderableWidget(new EditBox(font,130,10,140,20,Component.literal("editor")));
    }
}
