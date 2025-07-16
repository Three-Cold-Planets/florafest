package florafest;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import florafest.content.FloraBlocks;
import florafest.content.FloraItems;
import florafest.content.FloraQuests;
import florafest.content.FloraTechTree;
import florafest.dialog.FloraUI;
import florafest.graphics.ModShaders;
import florafest.questing.QuestLoader;
import florafest.questing.QuestTree;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.*;
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
        });
    }

    @Override
    public void loadContent(){
        //EntityRegistry.register();
        FloraItems.load();
        FloraBlocks.load();
        FloraTechTree.load();
        quests = new FloraQuests(FloraTechTree.root);
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
    }
}
