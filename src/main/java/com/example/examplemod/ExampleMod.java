package com.example.examplemod;

import com.example.examplemod.item.ModItems;
import com.example.examplemod.item.custom.NaturesCompassItem;
import com.example.examplemod.network.MyChannel;
import com.example.examplemod.util.CompassState;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 好，本系列视频就到这里结束了，代码基本上和mod一致，有了这些基础大家应该去看mod的代码应该会更容易理解一些，主要功能已经实现了，其他的是一些小功能，例如排序等等，
 * 大家可以自己去看看怎么实现的了。谢谢大家的支持。
 */
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 是否能传送 客户端从这里读数据
    public static boolean canTeleport;
    // 生物群系
    public static List<ResourceLocation> allowedBiomes;
    public static ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys;

    public ExampleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // 物品注册
        ModItems.register(modEventBus);
        // 网络
        MyChannel.register();


        MinecraftForge.EVENT_BUS.register(this);
    }


    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 初始化数据
        allowedBiomes = new ArrayList<ResourceLocation>();
        dimensionKeysForAllowedBiomeKeys = ArrayListMultimap.create();
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        // 最后让指针转起来，这里的详细的原理我就不说了，因为我也没看懂，看懂的老哥可以说一下，我这里大概讲下怎么实现的。
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemProperties.register(ModItems.COMPASS_ITEM.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
                @OnlyIn(Dist.CLIENT)
                private double rotation;
                @OnlyIn(Dist.CLIENT)
                private double rota;
                @OnlyIn(Dist.CLIENT)
                private long lastUpdateTick;
                // 还是这个函数，这个函数的返回值决定了采用了按个数值的模型
                @Override
                public float unclampedCall(ItemStack stack, ClientLevel world, LivingEntity entityLiving, int seed) {
                    if (entityLiving == null && !stack.isFramed()) { // 物品在显示框
                        return 0.0F;
                    } else {
                        // 物品不再展示框
                        // 获得一些数据
                        final boolean entityExists = entityLiving != null;
                        final Entity entity = (Entity) (entityExists ? entityLiving : stack.getFrame());
                        if (world == null && entity.level() instanceof ClientLevel) {
                            world = (ClientLevel) entity.level();
                        }
                        // 玩家朝向  或者 获得画框中的角度
                        double rotation = entityExists ? (double) entity.getYRot() : getFrameRotation((ItemFrame) entity);
                        // 归一化到 0 - 360
                        rotation = rotation % 360.0D;
                        // 下面啥计算我看不懂了，反正运算的最后是将目标位置向量和玩家朝向的向量做出一个夹角并映射到模型的所有数值中
                        // 其中目标位置的向量
                        double adjusted = Math.PI - ((rotation - 90.0D) * 0.01745329238474369D - getAngle(world, entity, stack));

                        if (entityExists) {
                            adjusted = wobble(world, adjusted);
                        }
                        //  adjusted / 2 * pi
                        final float f = (float) (adjusted / (Math.PI * 2D));
                        // 最后这个角度就是选择的模型，具体的逻辑由大佬讲解吧
                        return Mth.positiveModulo(f, 1.0F);
                    }
                }

                @OnlyIn(Dist.CLIENT)
                private double wobble(ClientLevel world, double amount) {
                    if (world.getGameTime() != lastUpdateTick) {
                        lastUpdateTick = world.getGameTime();
                        double d0 = amount - rotation;
                        d0 = Mth.positiveModulo(d0 + Math.PI, Math.PI * 2D) - Math.PI;
                        d0 = Mth.clamp(d0, -1.0D, 1.0D);
                        rota += d0 * 0.1D;
                        rota *= 0.8D;
                        rotation += rota;
                    }

                    return rotation;
                }

                @OnlyIn(Dist.CLIENT)
                private double getFrameRotation(ItemFrame itemFrame) {
                    Direction direction = itemFrame.getDirection();
                    int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                    return (double) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + i);
                }

                @OnlyIn(Dist.CLIENT)
                private double getAngle(ClientLevel world, Entity entity, ItemStack stack) {
                    if (stack.getItem() == ModItems.COMPASS_ITEM.get()) {
                        NaturesCompassItem compassItem = (NaturesCompassItem) stack.getItem();
                        BlockPos pos;
                        if (compassItem.getState(stack) == CompassState.FOUND) {
                            // 当为FOUND是的时候就是找到了，那么就是将找到坐标返回
                            pos = new BlockPos(compassItem.getFoundBiomeX(stack), 0, compassItem.getFoundBiomeZ(stack));
                        } else {
                            pos = world.getSharedSpawnPos();
                        }
                        // 返回到玩家位置的夹角
                        return Math.atan2((double) pos.getZ() - entity.position().z(), (double) pos.getX() - entity.position().x());
                    }
                    return 0.0D;
                }

            });
        }
    }
}
