package com.example.examplemod.renderer;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ModClientEventSubscriber {
    // OpenGL用于存储顶点数据的对象，本质是一个缓冲区，用于GPU存储顶点数据。当绘制图形时候从缓冲中读取顶点数据，进行绘制
    // OpenGL是一种跨平台的API，用于渲染2D，3D矢量图形，顶点数据指的是在三维空间定义的点，线，面等几个形状的属性信息，例如位置，颜色，纹理坐标。
    // 顶点数据通常在Vertex Buffer Object中 VBO是一种缓存对象，用于GPU显存中存储顶点数据。
    private static VertexBuffer vertexBuffer;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event){
        if(Minecraft.getInstance().player.getMainHandItem().getItem() == Items.GRASS_BLOCK) {
            int range = 5;
            for(int x = -range; x <= range; x++) {
                for(int y = -range; y <= range; y++) {
                    for(int z = -range; z <= range; z++) {
                        BlockPos pos = Minecraft.getInstance().player.blockPosition().offset(x, y, z);
                        if(Minecraft.getInstance().level.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK) {
                            // STATIC表示缓冲区数据不会经常修改
                            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                            // tessellator 用于在OpenGL中创建和处理几何图形，Tesselator类提供了一组API，
                            // 用于在GPU生成线，三角形等基本形状。
                            Tesselator tessellator = Tesselator.getInstance();
                            // BufferBuilder 用于构建顶点缓冲区，用于将几何数据转化为OpenGL可以理解的定点格式
                            BufferBuilder buffer = tessellator.getBuilder();

                            // 设置BufferBuilder的绘制模式和顶点格式
                            // VertexFormat.Mode.DEBUG 表示绘制模式为调试线，
                            // DefaultVertexFormat.POSITION_COLOR 表示顶点格式为位置+颜色模式，即每个顶点具有位置信息和颜色信息
                            buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

                            final double px = pos.getX(), py = pos.getY(), pz = pos.getZ();
                            final float red = 1;
                            final float green = 0;
                            final float blue = 0;
                            float size = 1f;
                            var opacity = 1F;


                            // UPPPER
                            buffer.vertex(px, py + size, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py + size, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py + size, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py + size, pz).color(red, green, blue, opacity).endVertex();

                            // BOTTOM
                            buffer.vertex(px + size, py, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py, pz).color(red, green, blue, opacity).endVertex();

                            // Edge 1
                            buffer.vertex(px + size, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz + size).color(red, green, blue, opacity).endVertex();

                            // Edge 2
                            buffer.vertex(px + size, py, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px + size, py + size, pz).color(red, green, blue, opacity).endVertex();

                            // Edge 3
                            buffer.vertex(px, py, pz + size).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py + size, pz + size).color(red, green, blue, opacity).endVertex();

                            // Edge 4
                            buffer.vertex(px, py, pz).color(red, green, blue, opacity).endVertex();
                            buffer.vertex(px, py + size, pz).color(red, green, blue, opacity).endVertex();

                            // 将顶点缓冲绑定在OpenGL顶点数组上
                            vertexBuffer.bind();
                            // 将缓冲区数据上传到顶点缓冲对象，buffer包含了绘制的顶点数据。
                            vertexBuffer.upload(buffer.end());
                            // 结束顶点缓冲的绑定，后续绘制不会在使用这个顶点缓冲对象了。
                            VertexBuffer.unbind();

                            if (vertexBuffer != null) {
                                // 获取Minecraft中相机的位置
                                Vec3 view = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();

                                // 启动混合模式，控制颜色和深度值合并在一起，即RGB通道和透明度通道
                                GL11.glEnable(GL11.GL_BLEND);
                                // 设置混合函数，设置混合函数是源颜色的透明通道和目标颜色的1-透明通道进行混合
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                // 启动抗锯齿
                                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                                // 禁用深度测试
                                GL11.glDisable(GL11.GL_DEPTH_TEST);

                                // 设置渲染系统的着色器为位置颜色着色器，返回的是着色器对象
                                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                                PoseStack matrix = event.getPoseStack();
                                matrix.pushPose();
                                // 平移操作
                                matrix.translate(-view.x, -view.y, -view.z);

                                vertexBuffer.bind();
                                // 绘制场景模型
                                vertexBuffer.drawWithShader(matrix.last().pose(), new Matrix4f(event.getProjectionMatrix()), RenderSystem.getShader());
                                VertexBuffer.unbind();
                                matrix.popPose();
                                // 开启深度
                                GL11.glEnable(GL11.GL_DEPTH_TEST);
                                // 关闭混合
                                GL11.glDisable(GL11.GL_BLEND);
                                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                            }
                        }


                    }
                }
            }
        }
    }

}
