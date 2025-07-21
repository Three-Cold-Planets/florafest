package florafest;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import florafest.content.*;
import florafest.dialog.FloraUI;
import florafest.graphics.ModShaders;
import florafest.questing.QuestLoader;
import florafest.questing.QuestTree;
import florafest.type.weapons.CollectionWeapon;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.graphics.Pal;
import mindustry.mod.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;
//import florafest.gen.*;

public class Florafest extends Mod{

    public Mods.LoadedMod MOD;
    public static String NAME = "florafest";
    public static QuestTree quests;

    public static FloraUI ui;

    public Florafest(){
        Events.on(EventType.FileTreeInitEvent.class, e -> {
            Core.app.post(ModShaders::load);
            MOD = Vars.mods.getMod(NAME);

            QuestLoader.test();
        });
    }

    @Override
    public void loadContent(){
        //EntityRegistry.register();
        FloraWeathers.load();
        FloraItems.load();
        FloraBlocks.load();
        FloraTechTree.load();
        quests = new FloraQuests("centri", FloraTechTree.root);

        UnitType sacrifice = UnitTypes.alpha;

        sacrifice.weapons.clear();
        sacrifice.itemCapacity = 90;
        sacrifice.weapons.add(new CollectionWeapon(){{
            widthSinMag = 0.11f;
            reload = 5f;
            quantity = 10;
            x = 0f;
            y = 6.5f;
            rotate = false;
            shootY = 0f;
            beamWidth = 0.7f;
            repairSpeed = 3.1f;
            fractionRepairSpeed = 0.06f;
            UnitTypes.alpha.aimDst = 0f;
            shootCone = 15f;
            mirror = false;

            targetUnits = false;
            targetBuildings = true;
            autoTarget = false;
            controllable = true;
            laserColor = Pal.accent;
            healColor = Pal.accent;

            bullet = new BulletType(){{
                maxRange = 60f;
            }};
        }});
    }

    @Override
    public void init(){
        //Todo: Automatically import classes into the js console
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "florafest",
                "florafest.content",
                "florafest.questing"
        );

        packages.each(name -> {

            NativeJavaPackage p = new NativeJavaPackage(name, Vars.mods.mainLoader());

            p.setParentScope(scope);

            scope.importPackage(p);
        });

        ui = new FloraUI();

        QuestLoader.load(quests);
    }
}
