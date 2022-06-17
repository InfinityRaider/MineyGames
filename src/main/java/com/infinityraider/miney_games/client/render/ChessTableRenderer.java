package com.infinityraider.miney_games.client.render;

import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChessTableRenderer implements ITileRenderer<TileChessTable>, IRenderUtilities {
    @Override
    public void render(TileChessTable tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        // TODO: render chess pieces
        this.drawSquareHighlight(tile, transforms, buffer);
    }

    protected void drawSquareHighlight(TileChessTable tile, PoseStack transforms, MultiBufferSource buffer) {
        if(!(buffer instanceof MultiBufferSource.BufferSource)) {
            return;
        }
        Player player = MineyGames.instance.getClientPlayer();
        if(player == null) {
            return;
        }
        tile.rayTrace(player).ifPresent(hitResult -> {
            // check if the ray trace was successful
            if(hitResult.getType() == HitResult.Type.MISS) {
                return;
            }
            // fetch highlighted square indices
            Vec3 hit = tile.offsetAbs(hitResult.getLocation());
            int iX = tile.getChessSquareIndexAbsX(hit);
            int iZ = tile.getChessSquareIndexAbsY(hit);
            if(iX < 0 || iZ < 0) {
                return;
            }
            // calculate highlight rendering bounds
            double x1 = Math.max(tile.getBlockPos().getX() - tile.getAbsX() + tile.getChessSquareMin(iX), tile.getBlockPos().getX());
            double x2 = Math.min(tile.getBlockPos().getX() - tile.getAbsX() + tile.getChessSquareMax(iX), tile.getBlockPos().getX() + 1);
            double z1 = Math.max(tile.getBlockPos().getZ() - tile.getAbsY() + tile.getChessSquareMin(iZ), tile.getBlockPos().getZ());
            double z2 = Math.min(tile.getBlockPos().getZ() - tile.getAbsY() + tile.getChessSquareMax(iZ), tile.getBlockPos().getZ() + 1);
            double y = tile.getBlockPos().getY() + 12*Constants.UNIT + 0.001 + 1;
            // draw the highlight
            VertexConsumer builder = buffer.getBuffer(HighLightRenderType.INSTANCE);
            if(!(builder instanceof BufferBuilder)) {
                return;
            }
            this.drawSquare(builder, transforms, x1, z1, x2, z2, y, 0, 0, 255, 128);


            this.drawSquare(
                    builder,
                    transforms,
                    tile.getBlockPos().getX(),
                    tile.getBlockPos().getZ(),
                    tile.getBlockPos().getX() + 1,
                    tile.getBlockPos().getZ() + 1,
                    tile.getBlockPos().getY() + 1,
                    0, 255, 0, 255
            );

            ((MultiBufferSource.BufferSource) buffer).endBatch(HighLightRenderType.INSTANCE);
        });
    }

    protected void drawSquare(VertexConsumer builder, PoseStack transforms, double x1, double z1, double x2, double z2, double y, int r, int g, int b, int a) {
        this.drawVertex(builder, transforms, x1, y, z1, r, g, b, a);
        this.drawVertex(builder, transforms, x2, y, z1, r, g, b, a);
        this.drawVertex(builder, transforms, x2, y, z2, r, g, b, a);
        this.drawVertex(builder, transforms, x1, y, z2, r, g, b, a);


        this.drawVertex(builder, transforms, x1, y, z1, b, g, r, a);
        this.drawVertex(builder, transforms, x1, y, z2, b, g, r, a);
        this.drawVertex(builder, transforms, x2, y, z2, b, g, r, a);
        this.drawVertex(builder, transforms, x2, y, z1, b, g, r, a);
    }

    protected void drawVertex(VertexConsumer builder, PoseStack transforms, double x, double y, double z, int r, int g, int b, int a) {
        Matrix4f matrix = transforms.last().pose();
        Matrix3f normal = transforms.last().normal();
        builder.vertex(matrix, (float) x, (float) y, (float) z).color(r, g, b, a).endVertex();//.normal(normal,0, 1, 0);
    }

    public static class HighLightRenderType extends RenderType {
        // We need to put the static instance inside a class, as to initialize it we need to access a Builder, which has protected access
        // Therefore we need a dummy constructor which will never be called ¯\_(ツ)_/¯
        private HighLightRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean delegate, boolean sorted, Runnable pre, Runnable post) {
            super(name, format, mode, bufferSize, delegate, sorted, pre, post);
        }

        public static final RenderType INSTANCE = create(
                MineyGames.instance.getModId() + ":chess_highlight",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(TRANSLUCENT_TARGET)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .createCompositeState(false));
    }
}
