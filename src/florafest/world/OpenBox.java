package florafest.world;

import arc.struct.ObjectMap;
import arc.util.Time;
import florafest.content.FloraAttributes;
import florafest.content.FloraItems;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.Attributes;
import mindustry.world.meta.Attribute;

public class OpenBox extends Block {

    public OpenBox(String name) {
        super(name);
        update = true;
        hasItems = true;
        rate = 1;
        produceTime = 300;
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    public float rate;
    public float produceTime;

    public ObjectMap<Attribute, ItemStack> collectableItems;
    public ObjectMap<Attribute, LiquidStack> collectableFluids;

    public class OpenBoxBuild extends Building{
        public float progress = 0;

        @Override
        public void created() {
            super.created();

        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(getMaximumAccepted(FloraItems.ash) > items.get(FloraItems.ash)) {
                progress += Time.delta * rate * FloraAttributes.ash.env();
                if (progress >= produceTime) {
                    progress %= produceTime;
                    items.add(FloraItems.ash, 1);
                }
            }
        }
    }
}
