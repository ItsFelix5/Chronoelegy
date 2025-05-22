package chronoelegy;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;

public class Rendering {
    public static final RenderPipeline.Snippet MATRICES_FOG = RenderPipeline.builder(RenderPipelines.MATRICES_SNIPPET, RenderPipelines.FOG_SNIPPET).buildSnippet();

    public static final RenderPipeline SOLID_COLOR_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(MATRICES_FOG)
                    .withVertexShader(Main.id("core/solid_color"))
                    .withFragmentShader(Main.id("core/solid_color"))
                    .withSampler("Sampler2")
                    .withVertexFormat(VertexFormats.POSITION_COLOR_LIGHT, VertexFormat.DrawMode.QUADS)
                    .withLocation(Main.id("pipeline/solid_color"))
                    .build()
    );
    public static final RenderLayer SOLID_COLOR = RenderLayer.of(
            "solid_color",
            128,
            true,
            false,
            SOLID_COLOR_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().lightmap(RenderLayer.ENABLE_LIGHTMAP).build(true)
    );

}
