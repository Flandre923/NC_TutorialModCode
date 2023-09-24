package com.example.examplemod.network.packet;

import com.example.examplemod.ExampleMod;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/***
 * 这个类用于信息同步，当玩家右键物品时候，需要给客户端发送生物群系的信息，
 */
public class SyncPacket {
    private boolean canTeleport; // 是否可以传送
    private List<ResourceLocation> allowedBiomes; // 将允许的生物群KEY 这个是主要的生物群系
    private ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys; // 生物对应的维度


    public SyncPacket() {}

    public SyncPacket(boolean canTeleport, List<ResourceLocation> allowedBiomes, ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys) {
        this.canTeleport = canTeleport;
        this.allowedBiomes = allowedBiomes;
        this.dimensionKeysForAllowedBiomeKeys = dimensionKeysForAllowedBiomeKeys;
    }

    // 从buf中构建这个类
    public SyncPacket(FriendlyByteBuf buf) {
        canTeleport = buf.readBoolean();
        allowedBiomes = new ArrayList<ResourceLocation>();
        dimensionKeysForAllowedBiomeKeys = ArrayListMultimap.create();

        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            ResourceLocation biomeKey = buf.readResourceLocation();
            int numDimensions = buf.readInt();
            List<ResourceLocation> dimensionKeys = new ArrayList<ResourceLocation>();
            for (int j = 0; j < numDimensions; j++) {
                dimensionKeys.add(buf.readResourceLocation());
            }

            if (biomeKey != null) {
                allowedBiomes.add(biomeKey);
                dimensionKeysForAllowedBiomeKeys.putAll(biomeKey, dimensionKeys);
            }
        }
    }

    // 转为buf
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(canTeleport);
        buf.writeInt(allowedBiomes.size());
        for (ResourceLocation biomeKey : allowedBiomes) {
            buf.writeResourceLocation(biomeKey);
            List<ResourceLocation> dimensionKeys = dimensionKeysForAllowedBiomeKeys.get(biomeKey);
            buf.writeInt(dimensionKeys.size());
            for (ResourceLocation dimensionKey : dimensionKeys) {
                buf.writeResourceLocation(dimensionKey);
            }
        }
    }
    // 处理这个类
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ExampleMod.canTeleport = canTeleport;
            ExampleMod.allowedBiomes = allowedBiomes;
            ExampleMod.dimensionKeysForAllowedBiomeKeys = dimensionKeysForAllowedBiomeKeys;
        });
        ctx.get().setPacketHandled(true);
    }

}
