package com.example.examplemod.item;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            ExampleMod.MODID);


    public static final RegistryObject<Item> COMPASS_ITEM = ITEMS.register("naturescompass",()->
            new CompassItem(new Item.Properties()));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
