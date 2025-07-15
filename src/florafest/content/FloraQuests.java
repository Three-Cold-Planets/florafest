package florafest.content;

import florafest.dialog.QuestTree;
import mindustry.content.TechTree;
import mindustry.ui.dialogs.ResearchDialog;

public class FloraQuests extends QuestTree {
    public FloraQuests(TechTree.TechNode root) {
        super(root);

        all.addAll(
                new QuestNode(){{
                    x = 50;
                }},
                new QuestNode()
        );
    }
}
