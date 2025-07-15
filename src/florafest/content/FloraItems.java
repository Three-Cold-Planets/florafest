package florafest.content;

import arc.graphics.Color;
import arc.struct.Seq;
import florafest.type.items.ModItem;
import mindustry.type.Item;

public class FloraItems {

    public static Seq<Item> all = Seq.with();

    public static Item plastick;

    //Stupidity begins here
    public static Item matter, fire, earth, air, water, soul,

    plantEssence, charredEssence;

    public static void load(){
        plastick = new ModItem("plastick");

        matter = new ModItem("matter", Color.white.cpy());

        plantEssence = new ModItem("plant-essence", Color.valueOf("524b24")){{

        }};

        charredEssence = new ModItem("charred-essence", Color.valueOf("222034")){{

        }};
    };
}

