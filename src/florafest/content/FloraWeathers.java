package florafest.content;

import arc.graphics.Color;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;

public class FloraWeathers {
    public static Weather ashfall, suspension,drizzle, deluge, monsoon;

    public static void load(){
        ashfall = new ParticleWeather("ashfall"){{
            color = Color.valueOf("#5f686b");
            attrs.set(FloraAttributes.ash, 1);
        }};

        suspension = new ParticleWeather("suspension"){{
            attrs.set(FloraAttributes.ash, 0.25f);
        }};
    }
}
