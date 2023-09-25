package com.example.examplemod.item.custom;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gui.NaturesCompassScrren;
import com.example.examplemod.item.ModItems;
import com.example.examplemod.network.MyChannel;
import com.example.examplemod.network.packet.SyncPacket;
import com.example.examplemod.util.BiomeSearchWorker;
import com.example.examplemod.util.BiomeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.network.NetworkDirection;

import java.util.List;
import java.util.Optional;

public class NaturesCompassItem extends Item {
    public NaturesCompassItem(Properties properties) {
        super(properties);
    }
    private BiomeSearchWorker worker;


    // 物品右键使用添加回调
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 玩家是否按下了shift
        if(!player.isCrouching()){
            // 是否是客户端
            if(level.isClientSide){
                ItemStack stack = ItemStack.EMPTY ;
                // 获得玩家的手里的物品
                if(!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem()== ModItems.COMPASS_ITEM.get()){
                     stack = player.getMainHandItem();
                }else if(!player.getOffhandItem().isEmpty()&&player.getOffhandItem().getItem() ==ModItems.COMPASS_ITEM.get()){
                    stack = player.getOffhandItem();
                }
                // 打开screen
                Minecraft.getInstance().setScreen(new NaturesCompassScrren(level,player,stack,stack.getItem(), ExampleMod.allowedBiomes));
            }else{
                // 右键时候 服务器要给客户端发送数据
                final ServerLevel serverLevel = (ServerLevel) level;
                final ServerPlayer serverPlayer = (ServerPlayer) player;
                final List<ResourceLocation> allowedBiomeKeys = BiomeUtils.getAllowedBiomeKeys(level);
                MyChannel.INSTANCE.sendTo(new SyncPacket(true, allowedBiomeKeys, BiomeUtils.getGeneratingDimensionsForAllowedBiomes(serverLevel)), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS,player.getItemInHand(hand));
    }

    // 之后存NBT数据，用与之后的操作
    public void setSearching(ItemStack stack, ResourceLocation biomeKey, Player player) {
        if (stack.isEmpty() || stack.getItem() != ModItems.COMPASS_ITEM.get()) {
            return;
        } else if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
//        stack.getTag().putString("BiomeKey", biomeKey.toString());
//        stack.getTag().putInt("State", CompassState.SEARCHING.getID());
    }


    // pos  玩家的位置
    // key 搜索的生物群系
    public void searchForBiome(ServerLevel level, Player player, ResourceLocation biomeKey, BlockPos pos, ItemStack stack) {
        setSearching(stack, biomeKey, player);
        Optional<Biome> optionalBiome = BiomeUtils.getBiomeForKey(level, biomeKey);
        if (optionalBiome.isPresent()) {
            // 每个work代表了一次搜索，所以我们再次搜索时候，需要亭子上一次的搜索。
            if (worker != null) {
                worker.stop();
            }
            worker = new BiomeSearchWorker(level, player, stack, optionalBiome.get(), pos);
            worker.start();
        }
    }
}
