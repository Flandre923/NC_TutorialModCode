package com.example.examplemod.util;

import com.example.examplemod.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

// 用于创建nbt的小工具函数
public class ItemUtils {
    public static boolean verifyNBT(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != ModItems.COMPASS_ITEM.get()) {
            return false;
        } else if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        return true;
    }

}
