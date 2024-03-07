package net.shoreline.client.init;

import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.shoreline.client.impl.shaders.GradientProgram;
import net.shoreline.client.mixin.accessor.AccessorBufferBuilderStorage;
import net.shoreline.client.util.Globals;
import org.lwjgl.opengl.GL11;

public class Programs implements Globals
{
    public static GradientProgram GRADIENT;
    //
    public static final RenderLayer GLINT = RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.GLINT_PROGRAM).texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, true, false))
            .writeMaskState(RenderPhase.COLOR_MASK).cull(RenderPhase.DISABLE_CULLING).depthTest(new DepthTest()).transparency(RenderPhase.GLINT_TRANSPARENCY).texturing(RenderPhase.GLINT_TEXTURING).build(false));

    public static void renderInit()
    {
        GRADIENT = new GradientProgram();
        ((AccessorBufferBuilderStorage) mc.getBufferBuilders()).hookGetEntityBuilders()
                .put(GLINT, new BufferBuilder(GLINT.getExpectedBufferSize()));
    }

    protected static class DepthTest extends RenderPhase.DepthTest
    {
        public DepthTest()
        {
            super("depth_test", GL11.GL_ALWAYS);
        }

        @Override
        public void startDrawing()
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_EQUAL);
        }

        @Override
        public void endDrawing()
        {
            // GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            // GL11.glClearDepth(1.0);
        }
    }
}