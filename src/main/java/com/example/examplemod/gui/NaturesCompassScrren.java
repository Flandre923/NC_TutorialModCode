package com.example.examplemod.gui;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.util.BiomeUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class NaturesCompassScrren extends Screen {
    public Level level;
    private List<Biome> allowedBiomes;
    private List<Biome> biomesMatchingSearch;
//
    private Button cancelButton;
    private Button sortByButton;
    private Button startSearchButton;
    private Button teleportButton;
    private EditBox searchTextField;
    private BiomeSearchList selectionList;


    // 构造方法 其中super传入一个Component类
    public NaturesCompassScrren(Level level, Player player, ItemStack itemStack, Item item, List<ResourceLocation> allowedBiomes) {
        super(Component.literal("select biome"));
        this.level = level;
        // 初始化时候传递数据
        this.allowedBiomes = new ArrayList<Biome>();
        loadAllowedBiomes(allowedBiomes);

        biomesMatchingSearch = new ArrayList<Biome>(this.allowedBiomes);
    }



    // 初始化方法，用于初始化界面
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
        startSearchButton.active = false;
        // 列表
        if (selectionList == null) {
            selectionList = new BiomeSearchList(this, minecraft, width + 110, height, 40, height, 45);
        }
        addRenderableWidget(selectionList);

        searchTextField = addRenderableWidget(new TransparentTextField(font,130,10,140,20,Component.literal("editor")));
    }

    public List<Biome> sortBiomes() {
        final List<Biome> biomes = biomesMatchingSearch;

        return biomes;
    }

    // 添加一下选中列表项的功能
    // 该方法用于鼠标在screen上滚动时候，滚动list
    @Override
    public boolean mouseScrolled(double scroll1, double scroll2, double scroll3) {
        return selectionList.mouseScrolled(scroll1, scroll2, scroll3);
    }
    // 该方法用于选中时候将 搜索button设置为可用
    public void selectBiome(BiomeSearchEntry entry) {
        boolean enable = entry != null;
        startSearchButton.active = enable;
    }


    // 当数据不一致时候需要对列表进行刷新，因为网路是一个异步的操作。有可能服务器发的数据还没到，界面及打开了，需要对list进行刷新
    @Override
    public void tick() {
        searchTextField.tick();

        if (allowedBiomes.size() != ExampleMod.allowedBiomes.size()) {
            teleportButton.visible = ExampleMod.canTeleport;
            removeWidget(selectionList);
            loadAllowedBiomes(ExampleMod.allowedBiomes);
            biomesMatchingSearch = new ArrayList<Biome>(allowedBiomes);
            selectionList = new BiomeSearchList(this, minecraft, width + 110, height, 40, height, 45);
            addRenderableWidget(selectionList);
        }
    }


    private void loadAllowedBiomes(List<ResourceLocation> allowedBiomeKeys) {
        this.allowedBiomes = new ArrayList<Biome>();
        for (ResourceLocation biomeKey : allowedBiomeKeys) {
            Optional<Biome> optionalBiome = BiomeUtils.getBiomeForKey(level, biomeKey);
            if (optionalBiome.isPresent()) {
                this.allowedBiomes.add(optionalBiome.get());
            }
        }
    }
}
