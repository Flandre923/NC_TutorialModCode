package com.example.examplemod.item.custom;

import com.example.examplemod.gui.NaturesCompassScrren;
import com.example.examplemod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NaturesCompassItem extends Item {
    public NaturesCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!player.isCrouching()){
            if(level.isClientSide){
                ItemStack stack = ItemStack.EMPTY ;
                if(!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem()== ModItems.COMPASS_ITEM.get()){
                     stack = player.getMainHandItem();
                }else if(!player.getOffhandItem().isEmpty()&&player.getOffhandItem().getItem() ==ModItems.COMPASS_ITEM.get()){
                    stack = player.getOffhandItem();
                }
                Minecraft.getInstance().setScreen(new NaturesCompassScrren(level,player,stack,stack.getItem()));
            }
        }
        return super.use(level, player,hand);
    }
}
