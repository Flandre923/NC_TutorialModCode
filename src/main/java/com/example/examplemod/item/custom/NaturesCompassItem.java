package com.example.examplemod.item.custom;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gui.NaturesCompassScrren;
import com.example.examplemod.item.ModItems;
import com.example.examplemod.network.MyChannel;
import com.example.examplemod.network.packet.SyncPacket;
import com.example.examplemod.util.BiomeSearchWorker;
import com.example.examplemod.util.BiomeUtils;
import com.example.examplemod.util.CompassState;
import com.example.examplemod.util.ItemUtils;
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
    // 其余的代码就是一个，表示搜索的过程，是否开始搜索，是否搜索成功
    // 之后存NBT数据，用与之后的操作
    // 搜索是存储当前搜索的key和更改状态为
    public void setSearching(ItemStack stack, ResourceLocation biomeKey, Player player) {
        // 获得nbt ， 将key 和  state状态放进入
        if (ItemUtils.verifyNBT(stack)) {
            stack.getTag().putString("BiomeKey", biomeKey.toString());
            stack.getTag().putInt("State", CompassState.SEARCHING.getID());
        }
    }
    // 搜索成功的时候
    public void succeed(ItemStack stack, Player player, int x, int z, int samples, boolean displayCoordinates) {
        setFound(stack, x, z, samples, player);
        setDisplayCoordinates(stack, displayCoordinates);
        worker = null;
    }
    // 失败
    public void fail(ItemStack stack, Player player, int radius, int samples) {
        setNotFound(stack, player, radius, samples);
        worker = null;
    }
    // 存入xz坐标，更改state状态
    public void setFound(ItemStack stack, int x, int z, int samples, Player player) {
        if (ItemUtils.verifyNBT(stack)) {
            stack.getTag().putInt("State", CompassState.FOUND.getID());
            stack.getTag().putInt("FoundX", x);
            stack.getTag().putInt("FoundZ", z);
            stack.getTag().putInt("Samples", samples);
        }
    }
    // 大同效益
    public void setNotFound(ItemStack stack, Player player, int searchRadius, int samples) {
        if (ItemUtils.verifyNBT(stack)) {
            stack.getTag().putInt("State", CompassState.NOT_FOUND.getID());
            stack.getTag().putInt("SearchRadius", searchRadius);
            stack.getTag().putInt("Samples", samples);
        }
    }
    // 好吧这里是是否展示position不是停掉work，work在搜索到之后就停了
    public void setDisplayCoordinates(ItemStack stack, boolean displayPosition) {
        if (ItemUtils.verifyNBT(stack)) {
            stack.getTag().putBoolean("DisplayCoordinates", displayPosition);
        }
    }
    public int getFoundBiomeX(ItemStack stack) {
        if (ItemUtils.verifyNBT(stack)) {
            return stack.getTag().getInt("FoundX");
        }

        return 0;
    }

    public int getFoundBiomeZ(ItemStack stack) {
        if (ItemUtils.verifyNBT(stack)) {
            return stack.getTag().getInt("FoundZ");
        }

        return 0;
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

    public CompassState getState(ItemStack stack) {
        if (ItemUtils.verifyNBT(stack)) {
            return CompassState.fromID(stack.getTag().getInt("State"));
        }
        return null;
    }
}
