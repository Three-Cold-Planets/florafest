package florafest.content;

import arc.struct.Seq;
import florafest.world.ItemPump;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.world.Block;

import static mindustry.type.ItemStack.with;

public class FloraBlocks {
    public static Block pump;
    public static void load(){

        pump = new ItemPump("pump"){{
            drops = Seq.with(
                    drop(Liquids.water, 60, with(Items.sand, 2)),
                    drop(Liquids.slag, 30, with(Items.scrap, 5, Items.graphite, 2, Items.titanium, 1)),
                    drop(Liquids.oil, 10, with(Items.coal, 5, Items.sporePod, 2, FloraItems.plastick, 1)),
                    drop(Liquids.cryofluid, 20, with(Items.titanium, 2))
            );

            size = 3;
            itemCapacity = 20;
            liquidCapacity = 360;
            craftTime = 30;

            requirements(Category.production, with(Items.copper, 1));
        }};
    }
}
