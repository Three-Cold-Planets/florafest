package florafest.content;

import arc.func.Floatp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import static mindustry.content.Fx.rand;

public class FloraFx {

    private static float percent = 0;

    public static Effect

            chainLightning = new Effect(15, 500 * 500/2 * Vars.tilesize, e -> {
        if(!(e.data instanceof VisualLightningHolder)) return;
        VisualLightningHolder p = (VisualLightningHolder) e.data;

        Draw.blend(Blending.additive);

        int seed = e.id;
        //get the start and ends of the lightning, then the distance between them
        float tx = Tmp.v1.set(p.start()).x, ty = Tmp.v1.y, dst = Tmp.v1.dst(Tmp.v2.set(p.end()));

        //Get the direction towards the endpoint from the start
        Tmp.v3.set(p.end()).sub(p.start()).nor();
        float normx = Tmp.v3.x, normy = Tmp.v3.y;

        rand.setSeed(seed);

        //Set arc width before rand gets based on time
        float arcWidth = rand.range(dst * p.arc());

        seed = e.id - (int) (e.time * 2);

        float angle = Tmp.v1.angleTo(Tmp.v2);

        //How offset each point is from the line based on an arc
        Floatp arcX = () -> Mathf.sinDeg(percent * 180) * arcWidth;

        //range of lightning strike's vary depending on turret
        float range = p.segLength();
        int links = Mathf.ceil(dst / p.segLength());
        float spacing = dst / links;

        Lines.stroke(p.width() * e.fout());
        Draw.color(Color.white, e.color, e.finpow());
        Fill.circle(Tmp.v2.x, Tmp.v2.y, p.width() * e.fout()/2);

        //begin the line
        //Lines.beginLine();

        //Lines.linePoint(Tmp.v1.x, Tmp.v1.y);

        //Join the links together

        int coils = p.coils();
        for(int u = 0; u < coils; u++){
            int coil = u + 1;
            float coilSpacing = spacing/coil;

            //Make the lower numbered, less eratic coils travel quicker
            float travelPercent = Mathf.clamp(e.finpow() * (coils - coil + 1));


            int coilLinks = links * coil;

            float lastx = Tmp.v1.x, lasty = Tmp.v1.y;
            for(int i = 0; i < Mathf.ceil(coilLinks * travelPercent); i++){
                float nx, ny;
                //Only put an endpoint at the very end of the lightning, ending early shoudn't end it at the end point
                if(i == links * coil - 1){
                    //line at end
                    nx = Tmp.v2.x;
                    ny = Tmp.v2.y;
                }else{
                    float len = (i) * coilSpacing + rand.range(coilSpacing/2) + coilSpacing;
                    rand.setSeed(seed + i);

                    //Gets more random with each coil
                    Tmp.v3.trns(rand.random(360), range/2/(coils - coil + 1));
                    percent = ((float) (i + 1))/coilLinks;

                    nx = tx + normx * len + Tmp.v3.x + Tmp.v4.set(0, arcX.get()).rotate(angle).x;
                    ny = ty + normy * len + Tmp.v3.y + Tmp.v4.y;
                }

                Drawf.light(lastx, lasty, nx, ny, Lines.getStroke(), Draw.getColor(), Draw.getColor().a);

                //Using a quad instead of just a line so that the edges can join together
                Lines.line(lastx, lasty, nx, ny);

                lastx = nx;
                lasty = ny;
                //Lines.linePoint(nx, ny);
            }
        }


        //ines.endLine();
        Draw.blend();
    });

    public interface VisualLightningHolder{
        Vec2 start();

        Vec2 end();

        float width();

        float segLength();

        float arc();

        int coils();
    }
}
