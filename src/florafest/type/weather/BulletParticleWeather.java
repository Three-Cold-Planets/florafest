package florafest.type.weather;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.MultiPacker;

public class BulletParticleWeather extends BaseParticleWeather {
    //Bullet which the weather can create.
    public BulletType particleBullet;

    //whether or not to generate a random number instead of using chanceSpawn. Can be useful for making more dynamic weather.
    public boolean dynamicSpawning = true;

    //chance for a bullet to spawn. Defaults to 1.
    public double chanceSpawn = 1;

    //Range within a bullet can be spawned from on a valid tile.
    public Vec2 randRange = new Vec2(55, 55);

    //Fade time for weather, since WeatherState's fadeTime is private
    public int fadeTime = 60 * 6;

    //Colour of generated region. Only change if PixmapRegion is not found
    public Color regionColour = Color.white;

    //Amount of bullets/square tilles per spawn cycle on average
    public float saturation;

    //Amount of cycles/tick on average
    public float rate;

    TextureRegion drawParticleRegion = null;

    public BulletParticleWeather(String name) {
        super(name);
        //DO NOT ENABLE, until I get a separate region for particles working, don't enable this.
        drawParticles = false;
        status = null;
        statusDuration = 125000;
        saturation = 1/50f/50f;
        rate = 1/60f;
    }

    @Override public void load(){
        region = Core.atlas.find(name);
        drawParticleRegion = Core.atlas.find(particleRegion);
        if(region != null && region != Core.atlas.find("error")) return;
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);
        if(region != null) return;
        //Pixmap genRegion = Drawr.pigmentae(Core.atlas.getPixmap(EndlessRusting.modname + "-" + "bullet-particle-weather-template"), color);
        //packer.add(MultiPacker.PageType.main, name, genRegion);
    }

    @Override
    public void update(WeatherState state){
        double infinity = Float.POSITIVE_INFINITY;
        float speed = force * state.intensity * Time.delta;

        if(speed > 0.001f){
            float windx = state.windVector.x * speed, windy = state.windVector.y * speed;

            for(Unit unit : Groups.unit){
                unit.impulse(windx, windy);
            }
        }



        //Saturation adjusted for the world you're in
        float adjustedSaturation = Vars.world.tiles.height * Vars.world.tiles.width * saturation;

        //Since saturation only works in integer amounts, any leftover saturation below 1 will be used to affect the rate.
        float adjustedRate = rate * (1 + Mathf.floor(adjustedSaturation) - adjustedSaturation);

        //Check for if a cycle should happen
        if(!Mathf.chance(adjustedRate)) return;

        //Then for each bullet spawn more bullet
        for(int i = 0; i < adjustedSaturation; i++) {

            int x = Mathf.random(1, Vars.world.tiles.width - 1) * Vars.tilesize;
            int y = Mathf.random(1, Vars.world.tiles.height - 1) * Vars.tilesize;

            Call.createBullet(particleBullet, Team.derelict, x, y, state.windVector.angle(), particleBullet.damage, 1, 1);
        }

    }
}