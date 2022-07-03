package com.infinityraider.miney_games.client.render;

import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.miney_games.MineyGames;
import com.infinityraider.miney_games.client.handler.ModelHandler;
import com.infinityraider.miney_games.content.chess.ChessGameWrapper;
import com.infinityraider.miney_games.content.chess.TileChessTable;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class ChessTableRenderer implements ITileRenderer<TileChessTable>, IRenderUtilities {
    private static final int[] NO_HIGHLIGHT = {-1,-1};
    private static final int ALPHA = 64;
    private static final float Y = (float) (12*Constants.UNIT + 0.001);
    private static final Quaternion FLIP_WHITE = Vector3f.YP.rotationDegrees(180);

    @Override
    public void render(TileChessTable tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        this.renderPieces(tile, transforms, buffer, overlay);
        this.drawSquareHighlights(tile, transforms, buffer);
    }

    protected void renderPieces(TileChessTable tile, PoseStack transforms, MultiBufferSource buffer, int overlay) {
        // check if the world is null
        if(tile.getLevel() == null) {
            return;
        }
        // fetch the vertex builder
        VertexConsumer builder = buffer.getBuffer(RenderType.solid());
        // get the indices of the first square
        int i_x0 = Math.max(0, tile.getSize().getSquareIndex(tile.getBlockPos().getX() - tile.getAbsX()));
        int i_z0 = Math.max(0, tile.getSize().getSquareIndex(tile.getBlockPos().getZ() - tile.getAbsY()));
        // get the coordinates of the first square
        double x0 = tile.getSize().getSquareMin(i_x0);
        double z0 = tile.getSize().getSquareMin(i_z0);
        // calculate the step width
        double step = tile.getSize().getSquareSize();
        // get the scale factor
        float scale = (float) tile.getSize().getPieceScale();
        // iterate over the squares until the bounds of the tile are exceeded, or all squares have been treated
        for (int dx = 0; x0 + (dx + 0.5)*step <= 1 && i_x0 + dx < 8; dx++) {
            for (int dz = 0; z0 + (dz + 0.5) * step <= 1 && i_z0 + dz < 8; dz++) {
                // get absolute square indices
                int xAbs = i_x0 + dx;
                int yAbs = i_z0 + dz;
                // calculate the relative indices
                int xRel = tile.xAbsToRel(xAbs, yAbs);
                int yRel = tile.yAbsToRel(xAbs, yAbs);
                // calculate coordinates for the centre of the square
                final double x = x0 + (dx + 0.5) * step;
                final double z = z0 + (dz + 0.5) * step;
                // check if there is a piece
                tile.getWrapper().getPiece(xRel, yRel).ifPresent(piece -> {
                    // fetch the model
                    BakedModel model = this.getModelManager().getModel(ModelHandler.getChessModel(piece));
                    // push pose
                    transforms.pushPose();
                    // translate to the centre of the square
                    transforms.translate(x, Y, z);
                    // rotate according to the orientation
                    transforms.mulPose(tile.getOrientation().getHorizontalRotation());
                    // flip the white pieces
                    if(piece.isWhite()) {
                        transforms.mulPose(FLIP_WHITE);
                    }
                    // translate back to the square origin
                    transforms.translate(-0.5 * step, 0, -0.5 * step);
                    // apply the scale
                    transforms.scale(scale, scale, scale);
                    // render the piece
                    this.renderPiece(tile, model, transforms, builder, overlay);
                    // pop pose
                    transforms.popPose();
                });
            }
        }
    }

    protected void renderPiece(TileChessTable tile, BakedModel model, PoseStack transforms, VertexConsumer builder, int overlay) {
        this.renderBlockModel(
                tile.getLevel(), model, tile.getBlockState(), tile.getBlockPos(), transforms, builder, false,
                tile.getRandom(), tile.getRandom().nextLong(), overlay, EmptyModelData.INSTANCE
        );
    }

    protected void drawSquareHighlights(TileChessTable tile, PoseStack transforms, MultiBufferSource buffer) {
        // if there is no client player, do no highlighting
        Player player = MineyGames.instance.getClientPlayer();
        if (player == null) {
            return;
        }
        // if the game is not running, do no highlighting
        ChessGameWrapper game = tile.getWrapper();
        if (!game.isRunning()) {
            return;
        }
        // only do highlighting for the participants
        game.asParticipant(player).ifPresent(participant -> {
            // get hovered coordinates
            int[] hovered = tile.rayTrace(player).map(hitResult -> {
                // check if the ray trace was successful
                if (hitResult.getType() == HitResult.Type.MISS) {
                    return NO_HIGHLIGHT;
                }
                // fetch highlighted square indices
                Vec3 hit = tile.offsetAbs(hitResult.getLocation());
                int iX = tile.getChessSquareIndexAbsX(hit);
                int iZ = tile.getChessSquareIndexAbsY(hit);
                return new int[]{iX, iZ};
            }).orElse(NO_HIGHLIGHT);
            // get the indices of the first square
            int x0 = Math.max(0, tile.getSize().getSquareIndex(tile.getBlockPos().getX() - tile.getAbsX()));
            int z0 = Math.max(0, tile.getSize().getSquareIndex(tile.getBlockPos().getZ() - tile.getAbsY()));
            // calculate the step width
            double step = tile.getSize().getSquareSize();
            // iterate over the squares until the bounds of the tile are exceeded, or all squares have been treated
            for (int dx = 0; step * dx <= 1 && x0 + dx < 8; dx++) {
                for (int dz = 0; step * dz <= 1 && z0 + dz < 8; dz++) {
                    // get absolute square indices
                    int xAbs = x0 + dx;
                    int yAbs = z0 + dz;
                    // calculate the relative indices
                    int xRel = tile.xAbsToRel(xAbs, yAbs);
                    int yRel = tile.yAbsToRel(xAbs, yAbs);
                    // fetch the highlight colour
                    participant.getHighLightColour(xRel, yRel, xAbs == hovered[0] && yAbs == hovered[1]).ifPresent(colour ->
                            this.highLightSquare(tile, xAbs, yAbs, transforms, buffer, colour.getX(), colour.getY(), colour.getZ())
                    );
                }
            }
        });
    }

    protected void highLightSquare(TileChessTable tile, int iX, int iZ, PoseStack transforms, MultiBufferSource buffer, int r, int g, int b) {
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
        this.drawSquare(builder, transforms, x1, z1, x2, z2, r, g, b);
    }

    protected void drawSquare(VertexConsumer builder, PoseStack transforms, double x1, double z1, double x2, double z2, int r, int g, int b) {
        this.drawVertex(builder, transforms, x1, z1, r, g, b);
        this.drawVertex(builder, transforms, x1, z2, r, g, b);
        this.drawVertex(builder, transforms, x2, z2, r, g, b);
        this.drawVertex(builder, transforms, x2, z1, r, g, b);
    }

    protected void drawVertex(VertexConsumer builder, PoseStack transforms, double x, double z, int r, int g, int b) {
        builder.vertex(transforms.last().pose(), (float) x, Y, (float) z).color(r, g, b, ALPHA).endVertex();
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
