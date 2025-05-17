package florafest.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.ctype.UnlockableContent;

public class ModDraw {
    public static void drawCornerIcon(int size, float x, float y, TextureRegion icon) {
        float dx = x - (float)(size * 8) / 2.0F;
        float dy = y + (float)(size * 8) / 2.0F;
        float s = 6.0F;
        Draw.z(Draw.z() - 0.1f);
        Draw.mixcol(Color.darkGray, 1.0F);
        Draw.rect(icon, dx, dy - 1.0F, s, s);
        Draw.z(Draw.z() + 0.1f);
        Draw.mixcol();
        Draw.rect(icon, dx, dy, s, s);
    }
}
