package florafest.content;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class FloraPal {

    public static Color
    swarm = color(Pal.surge.toString())
    ;

    public static Color color(String hex){
        return Color.valueOf(hex);
    }
}

