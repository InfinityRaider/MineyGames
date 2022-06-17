package com.infinityraider.miney_games.client.render;

import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.mojang.blaze3d.vertex.*;
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
            double x1 = Math.max(tile.getChessSquareMin(iX) - tile.getAbsX(), 0);
            double x2 = Math.min(tile.getChessSquareMax(iX) - tile.getAbsX(), 1);
            double z1 = Math.max(tile.getChessSquareMin(iZ) - tile.getAbsY(), 0);
            double z2 = Math.min(tile.getChessSquareMax(iZ) - tile.getAbsY(), 1);
            // check if the bounds are within the block's position
            if(x1 >= 1 || x2 <= 0 || z1 >= 1 || z2 <= 0) {
                return;
            }
            VertexConsumer builder = buffer.getBuffer(HighLightRenderType.INSTANCE);
            if(!(builder instanceof BufferBuilder)) {
                return;
            }
            this.drawSquare(builder, transforms, x1, z1, x2, z2, 12*Constants.UNIT + 0.001, 0, 0, 255, 64);
        });
    }

    protected void drawSquare(VertexConsumer builder, PoseStack transforms, double x1, double z1, double x2, double z2, double y, int r, int g, int b, int a) {
        this.drawVertex(builder, transforms, x1, y, z1, r, g, b, a);
        this.drawVertex(builder, transforms, x1, y, z2, r, g, b, a);
        this.drawVertex(builder, transforms, x2, y, z2, r, g, b, a);
        this.drawVertex(builder, transforms, x2, y, z1, r, g, b, a);
    }

    protected void drawVertex(VertexConsumer builder, PoseStack transforms, double x, double y, double z, int r, int g, int b, int a) {
        builder.vertex(transforms.last().pose(), (float) x, (float) y, (float) z).color(r, g, b, a).endVertex();
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
