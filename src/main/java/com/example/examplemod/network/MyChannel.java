package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.packet.CompassSearchPacket;
import com.example.examplemod.network.packet.SyncPacket;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lwjgl.system.windows.MSG;

public class MyChannel {
    public static  SimpleChannel INSTANCE;


    public static void register(){
        SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExampleMod.MODID,ExampleMod.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE = channel;

        //Server packet
        INSTANCE.registerMessage(0, CompassSearchPacket.class, CompassSearchPacket::toBytes, CompassSearchPacket::new, CompassSearchPacket::handle);

        // Client packet
        INSTANCE.registerMessage(2, SyncPacket.class, SyncPacket::toBytes, SyncPacket::new, SyncPacket::handle);

    }

    public <MSG> void sendToPlayer(MSG message,Connection player){
        INSTANCE.sendTo(message,player,NetworkDirection.PLAY_TO_CLIENT);

    }

}
