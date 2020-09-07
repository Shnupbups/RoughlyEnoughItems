/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.impl.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.rei.api.DrawableConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public final class TexturedDrawableConsumer implements DrawableConsumer {
    
    @NotNull
    private ResourceLocation identifier;
    private int x, y, width, height, uWidth, vHeight, textureWidth, textureHeight;
    private float u, v;
    
    public TexturedDrawableConsumer(@NotNull ResourceLocation identifier, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.identifier = identifier;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }
    
    @Override
    public void render(@NotNull AbstractGui helper, @NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft.getInstance().getTextureManager().bind(identifier);
        innerBlit(matrices.last().pose(), x, x + width, y, y + height, helper.getBlitOffset(), uWidth, vHeight, u, v, textureWidth, textureHeight);
    }
    
    private static void innerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, int width, int height, float u, float v, int texWidth, int texHeight) {
        innerBlit(matrix, xStart, xEnd, yStart, yEnd, z, u / texWidth, (u + width) / texWidth, v / texHeight, (v + height) / texHeight);
    }
    
    protected static void innerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.vertex(matrix, xStart, yEnd, z).uv(uStart, vEnd).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yEnd, z).uv(uEnd, vEnd).endVertex();
        bufferBuilder.vertex(matrix, xEnd, yStart, z).uv(uEnd, vStart).endVertex();
        bufferBuilder.vertex(matrix, xStart, yStart, z).uv(uStart, vStart).endVertex();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferBuilder);
    }
}
