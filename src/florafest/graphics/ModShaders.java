package florafest.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Time;
import florafest.Florafest;
import florafest.dialog.QuestbookDialog;
import mindustry.Vars;
import mindustry.graphics.Shaders;
import arc.graphics.gl.FrameBuffer;

import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;

public class ModShaders {

    public static TexturedFogShader fog;
    public static GridShader grid;

    public static NamedShader ice;
    public static boolean loaded = false;
    public static FrameBuffer effectBuffer,
    //captures everything before ui starts rendering
    bufferScreen;
    public static void load(){
        if(headless) return;
        loaded = true;

        effectBuffer = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        bufferScreen = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight());
        try {
            fog = new TexturedFogShader();
            Shaders.fog = fog;
            grid = new GridShader("grid");
        }
        catch (IllegalArgumentException error){
            loaded = false;
            Log.err("Failed to load Florafest's shaders: " + error);
        }
    }

    public static void dispose(){
        if(!headless && loaded){
            fog.dispose();
            grid.dispose();
        }
    }

    public static class GridShader extends NamedShader{
        public GridShader(String name){
            super(name);
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_size", Florafest.ui.questbook.view.lastZoom);
            setUniformf("u_offset", Florafest.ui.questbook.view.panX, Florafest.ui.questbook.view.panY);
        }
    }

    public static class TexturedFogShader extends Shaders.FogShader{

        public Texture noiseTex;

        public TexturedFogShader(){
            super();
            loadNoise();
        }

        @Override
        public void apply() {
            super.apply();
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_drawCol", Draw.getColor().r,  Draw.getColor().g,  Draw.getColor().b,  Draw.getColor().a);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }
    }

    public static class NamedShader extends Shader {
        public NamedShader(String name) {
            super(Core.files.internal("shaders/screenspace.vert"),
                    Vars.tree.get("shaders/" + name + ".frag"));
        }

        @Override
        public void apply() {
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_drawCol", Draw.getColor().r,  Draw.getColor().g,  Draw.getColor().b,  Draw.getColor().a);
        }
    }
}
