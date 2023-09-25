package com.example.examplemod.network.packet;

import com.example.examplemod.item.ModItems;
import com.example.examplemod.item.custom.NaturesCompassItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.command.ModIdArgument;

import java.util.function.Supplier;

public class CompassSearchPacket {
    private ResourceLocation biomeKey;
    private int x;
    private int y;
    private int z;

    public CompassSearchPacket() {}

    public CompassSearchPacket(ResourceLocation biomeKey, BlockPos pos) {
        this.biomeKey = biomeKey;
        // 玩家的位置
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public CompassSearchPacket(FriendlyByteBuf buf) {
        biomeKey = buf.readResourceLocation();

        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(biomeKey);

        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        // server 客户端发给服务器，此处是服务器
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            ItemStack stack = ItemStack.EMPTY;
            if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == ModItems.COMPASS_ITEM.get()) {
                stack =  player.getMainHandItem();
            } else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == ModItems.COMPASS_ITEM.get()) {
                stack = player.getOffhandItem();
            }

            if (!stack.isEmpty()) {
                final NaturesCompassItem natureCompass = (NaturesCompassItem) stack.getItem();
                natureCompass.searchForBiome(ctx.get().getSender().serverLevel(), ctx.get().getSender(), biomeKey, new BlockPos(x, y, z), stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
