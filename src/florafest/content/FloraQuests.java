package florafest.content;

import florafest.questing.QuestTree;
import mindustry.content.TechTree;

public class FloraQuests extends QuestTree {
    public FloraQuests(TechTree.TechNode root) {
        super(root);

        all.addAll(
                new QuestNode(){{
                    setPos(50, 0);
                }},
                new QuestNode()
        );
    }
}
