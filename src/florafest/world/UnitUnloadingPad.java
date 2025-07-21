package florafest.world;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.*;

public class UnitUnloadingPad extends Block {

    public TextureRegion centerRegion;

    @Override
    public void load() {
        super.load();
        centerRegion = Core.atlas.find(name + "-center", Core.atlas.find("unloader-center"));
    }

    public float speed = 1f;

    /** Cached result of content.items() */
    static Item[] allItems;

    public UnitUnloadingPad(String name){
        super(name);
        update = true;
        solid = true;
        health = 70;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        itemCapacity = 0;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        unloadable = false;

        config(Item.class, (UnloadingPadBuild tile, Item item) -> tile.sortItem = item);
        configClear((UnloadingPadBuild tile) -> tile.sortItem = null);
    }

    @Override
    public void init(){
        super.init();

        allItems = content.items().toArray(Item.class);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.speed, 60f / speed, StatUnit.itemsSecond);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        drawPlanConfigCenter(plan, plan.config, "unloader-center");
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
    }

    public static class ContainerStat implements Poolable{
        Building building;
        float loadFactor;
        boolean canLoad;
        /** Cached !(building instanceof StorageBuild) */
        int lastUsed;

        @Override
        public void reset(){
            building = null;
        }
    }

    public class UnloadingPadBuild extends Building{

        public Item sortItem;
        public float unloadTimer;
        public Seq<Unit> candidates;
        public Seq<Building> loadTargets = Seq.with();

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            rebuildTargets();
        }

        public void rebuildTargets(){
            loadTargets.clear();
            proximity.each(b -> {
                if(b.interactable(team) && b.items != null && sortItem == null){
                    loadTargets.add(b);
                }
            });

            loadTargets.sort(b -> b.block instanceof CoreBlock ? 0 : 1);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            unload();
        }

        public void unload(){
            unloadTimer += Time.delta;
            if (unloadTimer >= 60) {
                int tsize = size * tilesize;
                candidates = Groups.unit.intersect(x - tsize/2, y - tsize/2, tsize, tsize);
                if(candidates.size == 0) return;
                unloadTimer %= 60;
                candidates.each(unit -> {
                    if(!unit.hasItem()) return;
                    loadTargets.each(build -> {
                        int transferAmount = Math.min(build.getMaximumAccepted(unit.item()) - build.items.get(unit.item()), unit.stack.amount);
                        if(transferAmount == 0) return;
                        Call.transferItemTo(unit, unit.item(), transferAmount, unit.x, unit.y, build);
                    });
                });
            }
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(sortItem == null ? Color.clear : sortItem.color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(sortItem);
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(UnitUnloadingPad.this, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config(){
            return sortItem;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortItem = id == -1 ? null : content.item(id);
        }
    }
}
