package com.infinityraider.miney_games.client.render;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.infinityraider.miney_games.content.poker.TilePokerTable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PokerTableRenderer implements ITileRenderer<TilePokerTable>, IRenderUtilities {
    @Override
    public void render(TilePokerTable tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {

    }
}
