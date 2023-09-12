package com.example.examplemod.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NaturesCompassScrren extends Screen {


    private Button cancelButton;
    private Button sortByButton;
    private Button startSearchButton;
    private Button teleportButton;
    private EditBox searchTextField;
//    private BiomeSearchList selectionList;


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
        cancelButton =  addRenderableWidget(new TransaprentButton(10,height-30,110,20,Component.literal("cancel"),(onPress)->{
            minecraft.setScreen(null);
        }));

        sortByButton = addRenderableWidget(new TransaprentButton(10, 65, 110, 20, Component.literal("sort"), (onPress) -> {
            sortByButton.setMessage(Component.literal("整理"));
        }));
        startSearchButton = addRenderableWidget(new TransaprentButton(10, 40, 110, 20, Component.literal("start search"), (onPress) -> {

        }));
        teleportButton = addRenderableWidget(new TransaprentButton(width - 120, 10, 110, 20, Component.literal("teleport"), (onPress) -> {

        }));

//
//        if (selectionList == null) {
//            selectionList = new BiomeSearchList(this, minecraft, width + 110, height, 40, height, 45);
//        }
//        addRenderableWidget(selectionList);

        searchTextField = addRenderableWidget(new EditBox(font,130,10,140,20,Component.literal("editor")));
    }
}
