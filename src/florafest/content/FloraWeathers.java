package florafest.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import florafest.entities.bullet.ChainLightningBulletType;
import florafest.type.weather.BulletParticleWeather;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;

public class FloraWeathers {
    public static Weather ashfall, suspension, drizzle, deluge, monsoon, arcfest;

    public static void load(){
        ashfall = new ParticleWeather("ashfall"){{
            color = Color.valueOf("#5f686b");
            attrs.set(FloraAttributes.ash, 1);
        }};

        suspension = new ParticleWeather("suspension"){{
            attrs.set(FloraAttributes.ash, 0.25f);
        }};

        arcfest = new BulletParticleWeather("arcfest"){{
            particleBullet  = new BulletType() {
                int radius = 120;

                @Override
                public void draw(Bullet b) {
                    Draw.z(Layer.effect);
                    Draw.alpha(b.fin());
                    color(Color.white);
                    Fill.circle(b.x, b.y, 4 * b.fout());
                    color(FloraPal.swarm, Color.white, b.fin() * 0.5f);
                    Fill.circle(b.x, b.y, 8 * b.foutpow());

                    stroke((0.7f + Mathf.absin(10, 0.7f)) * b.fin() * 1.6f, FloraPal.swarm);

                    float progress = b.fin(Interp.smooth);

                    for (int i = 0; i < 6; i++) {
                        float rot = i * 360f / 6 - 360 * progress;
                        Lines.arc(b.x, b.y, radius * progress + 3f, 0.08f + b.fin() * 0.06f, rot);
                    }

                    float fastProgress = Mathf.clamp(progress * 3 - 2);

                    Draw.blend(Blending.additive);
                    Fill.light(b.x, b.y, 20, radius/4, Tmp.c1.set(Color.white).lerp(FloraPal.swarm, fastProgress).a(fastProgress * 0.2f), Tmp.c2.set(FloraPal.swarm).a(0));
                    Draw.blend();
                }

                {
                    lifetime = 75;
                    speed = 0;
                    splashDamage = 450;
                    splashDamageRadius = 30;
                    shootEffect = despawnEffect = Fx.none;
                    hitEffect = new Effect(50f, 100f, e -> {
                        e.scaled(7f, b -> {
                            color(FloraPal.swarm, b.fout());
                            Draw.blend(Blending.additive);
                            Fill.light(b.x, b.y, 20, radius, Tmp.c1.set(FloraPal.swarm).a(b.fout()), Tmp.c2.set(Tmp.c1).a(b.fin() * b.fslope() * 0.2f));
                            Draw.blend();
                        });

                        color(FloraPal.swarm);
                        stroke(e.fout() * 3f);
                        Lines.circle(e.x, e.y, radius);

                        Fill.circle(e.x, e.y, 12f * e.fout());
                        color();
                        Fill.circle(e.x, e.y, 6f * e.fout());
                        Drawf.light(e.x, e.y, radius * 1.6f, FloraPal.swarm, e.fout());
                    });
                    hitSound = Sounds.spark;
                    fragBullet = new ChainLightningBulletType(){{
                        lightningColor = Pal.surge;
                        range = 160;
                        damage = 200;
                        distanceDamageFalloff = 2.5f;
                        jumpDamageFactor = 0.95f;
                        hitSound = Sounds.spark;
                        collidesTeam = true;
                        targetRange = 160;
                        segmentLength = 12;
                        coils = 2;
                        width = 4;
                        branches = 3;
                        chainLightning = 3;
                    }};
                    fragBullets = 2;
                    fragOffsetMax = 0;
                }
            };
        }};
    }
}
