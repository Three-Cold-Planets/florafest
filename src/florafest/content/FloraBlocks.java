package florafest.content;

import arc.struct.Seq;
import florafest.entities.bullet.ChainLightningBulletType;
import florafest.world.ItemPump;
import florafest.world.OpenBox;
import florafest.world.UnitUnloadingPad;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.type.ItemStack.with;

public class FloraBlocks {
    public static Block
            //Cores
            coreCentral,

            pump, openBox, unloadingPad;

    public static void load(){

        coreCentral = new CoreBlock("core-central"){{
            requirements(
                    Category.effect, with(FloraItems.matter, 4000)
            );

            size = 5;
            itemCapacity = 2000;
            alwaysUnlocked = true;
        }};

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

        openBox = new OpenBox("open-box"){{
            size = 3;
            itemCapacity = 30;
            produceTime = 120;
            requirements(
                    Category.production, with(FloraItems.ash, 30)
            );
        }};

        unloadingPad = new UnitUnloadingPad("unloading-pad"){{
            size = 3;
            requirements(
                    Category.distribution, with(FloraItems.ash, 120)
            );
        }};

        BulletType bullet = ((PowerTurret) Blocks.afflict).shootType;
        bullet.lightning = 0;
        bullet.intervalBullet = new ChainLightningBulletType(){{
            lightningColor = Pal.surge;
            range = 75;
            damage = 65;
            distanceDamageFalloff = 4;
            jumpDamageFactor = 0.5f;
            hitSound = Sounds.spark;
            collidesTeam = true;
            targetRange = 75;
            segmentLength = 8;
            coils = 5;
            width = 4;
        }};
        bullet.fragBullets = 1;
        bullet.fragBullet = new ChainLightningBulletType(){{
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
            chainLightning = 6;
        }};

        bullet.intervalDelay = 20;
        bullet.bulletInterval = 9;
        bullet.intervalBullets = 1;
        bullet.scaleLife = true;
        bullet.collides = false;
        bullet.fragOnAbsorb = false;
        bullet.speed /= 1.5f;
        bullet.lifetime *= 1.5f;
        ((PowerTurret) Blocks.afflict).reload *= 2;

        /*
        PowerTurret turret = ((PowerTurret) Blocks.afflict);
        turret.reload /= 3;
        turret.shootSound = Sounds.spark;
        turret.shootType = new ChainLightningBulletType(){{
            lightningColor = Pal.surge;
            range = turret.range;
            damage = 240;
            distanceDamageFalloff = 0.05f;
            hitSound = Sounds.spark;
            segmentLength = 12;
            targetRange = 16;
            coils = 3;
            width = 4;
        }};
         */
    }
}
