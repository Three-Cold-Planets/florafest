package florafest.world;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;

import static florafest.graphics.ModDraw.drawCornerIcon;

public class ItemPump extends Block {

    public Seq<Item> curSeq = Seq.with();

    public float craftTime;


    public Seq<DropEntry> drops;

    Seq<Item> possibleItems = Seq.with();

    @Override
    public void setBars() {
        super.setBars();
    }

    public class DropEntry{

        public DropEntry(LiquidStack cost, ItemStack[] results){
            this.cost = cost;
            this.results = results;
        }

        public LiquidStack cost;
        public ItemStack[] results;
    }

    static LiquidStack tmpStack = new LiquidStack(null, 0);

    public ItemPump(String name) {
        super(name);
        this.update = true;
        this.solid = true;
        hasItems = true;
        hasLiquids = true;
        floating = true;
        this.envEnabled |= 6;
    }

    @Override
    public void init() {
        super.init();
        for(DropEntry drop: drops){
            for(ItemStack stack: drop.results){
                if(!possibleItems.contains(stack.item)) possibleItems.add(stack.item);
            }
        }
    }

    public DropEntry drop(Liquid liquid, float cost, ItemStack[] results){
        return new DropEntry(new LiquidStack(liquid, cost), results);
    }

    static boolean canPump(Tile tile) {
        return tile != null && tile.floor().liquidDrop != null;
    }

    public class ItemPumpBuild extends Building {
        public float amount = 0;

        public float progress;
        public float totalProgress;
        public float warmup;
        public int seed;

        public Seq<Item> que = Seq.with();

        public int state = 0;

        public DropEntry drop;
        public Liquid liquidDrop;
        public float targAmount = 0;

        @Override
        public float progress() {
            return progress;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            float curAmount = liquidDrop == null ? 0 : liquids.get(liquidDrop);
            float curCost = drop == null ? 0 : drop.cost.amount;

            switch(state) {
                //Filling
                case 0:

                    if(curAmount >= targAmount || liquidDrop == null) {
                        //Stock up on items to output
                        float itemAmount = (float)Math.floor(curAmount/curCost);

                        curSeq.clear();
                        while (itemAmount > 0){
                            if(curSeq.size <= 0){
                                for(ItemStack stack: drop.results){
                                    for(int i = 0; i < stack.amount; i++){
                                        curSeq.add(stack.item);
                                    }
                                }
                            }
                            que.add(curSeq.pop());
                            itemAmount--;
                        }
                        que.shuffle();

                        //Change the state
                        state = 1;
                        break;
                    }

                    liquids.add(liquidDrop, amount);

                    break;
                //Processing
                case 1:
                    //Avoid trying to process items if it's impossible
                    if((que.size == 0 || curAmount < curCost)) {
                        //Switch states if the block can pump liquids
                        if(liquidDrop != null) state = 0;

                        //Stop the rest of the state from running
                        break;
                    }

                    if(shouldConsume()) {
                        progress += edelta();
                        totalProgress += edelta();
                    }

                    if(progress >= craftTime){
                        progress %= craftTime;
                        items.add(que.pop(), 1);
                        liquids.remove(liquidDrop, curCost);
                    }

                    break;
            }



            if(timer(timerDump, dumpTime)){
                for(Item item : possibleItems){
                    dump(item);
                }
            }
        }

        @Override
        public boolean shouldConsume() {
            if (que.size > 0 && this.items.get(que.get(que.size - 1)) + 1 > itemCapacity) {
                return false;
            }
            return true;
        }

        @Override
        public boolean canDump(Building to, Item item) {
            return super.canDump(to, item);
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            this.amount = 0.0F;
            this.liquidDrop = null;


            for(Tile other : tile.getLinkedTilesAs(block, tempTiles)){
                Log.info(other.floor().liquidDrop);
                if(other.floor().liquidDrop == null) continue;
                Log.info("continuing!");
                if (!ItemPump.canPump(other) || (liquidDrop != null && other.floor().liquidDrop != liquidDrop)) {
                    Log.info("fuck");
                    Fx.explosion.at(other.worldx(), other.worldy());
                    liquidDrop = null;
                    return;
                }
                this.liquidDrop = other.floor().liquidDrop;
                this.amount += other.floor().liquidMultiplier;
            }

            if(liquidDrop == null) return;

            tmpStack.liquid = liquidDrop;
            drop = drops.find(d -> d.cost.liquid == liquidDrop);
            //Make sure we don't fill more liquid than required
            targAmount = (float)Math.floor(liquidCapacity/drop.cost.amount) * drop.cost.amount;
        }

        @Override
        public void drawSelect(){
            if(drop != null) {
                Tmp.v1.set(x, y);
                for(int i = 0; i < Math.min(que.size, 20); i++){
                    float f = (float)i;
                    //We'll grab the top item on the que
                    Item item = que.get(que.size - i - 1);


                    float dx = Tmp.v1.x - (float)(size * 8) / 2.0F;
                    float dy = Tmp.v1.y + (float)(size * 8) / 2.0F;
                    float s = 6.0F;

                    Draw.color(Tmp.c1.set(Color.white).a(Math.max(1.0f - (f)/20, 0)));
                    Draw.rect(item.fullIcon, dx, dy, s, s);
                    Tmp.v1.add(Tmp.v2.trns(i*2, 2));
                }
            }
            else drawCornerIcon(size, x, y, Icon.cancel.getRegion());
        }

        @Override
        public void draw() {
            super.draw();

            Draw.z(Layer.overlayUI);
            Tmp.v1.set(x, y).add(Vars.tilesize, Vars.tilesize);
            Draw.color(Pal.gray);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, 4);

            if(liquids.current() != null) {
                Draw.color(liquids.current().color);
                float radius = state == 0 ? Interp.exp5Out.apply(Interp.smooth2.apply(liquids.currentAmount()/liquidCapacity)) : liquids.currentAmount()/liquidCapacity;
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 3.5f * radius);
            }

            Tmp.v1.add(Vars.tilesize/2, Vars.tilesize/4);
            Draw.color(Pal.gray);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, 3);

            Draw.color(Pal.accent);
            Lines.stroke(0.5f);
            Lines.arc(Tmp.v1.x, Tmp.v1.y, 2, progress/ craftTime);
        }
    }
}
