package florafest.dialog;

import arc.Events;
import arc.util.Log;
import florafest.Florafest;
import florafest.content.FloraBlocks;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.dialogs.ResearchDialog;

public class FloraUI {
    public QuestbookDialog questbook;

    public FloraUI(){
        questbook = new QuestbookDialog();

    }

    public static void update(){
    }
}
