package florafest.content;

import florafest.questing.QuestTree;
import mindustry.content.TechTree;

public class FloraQuests extends QuestTree {


    public FloraQuests(String name, TechTree.TechNode root) {
        super(name, root);

    }

    @Override
    public void loadDefaults(){

        all.addAll(
                new QuestNode(){{
                    setPos(50, 0);
                }},
                new QuestNode()
        );
    }
}
