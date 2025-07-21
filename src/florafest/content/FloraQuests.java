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
                new QuestNode("pain1"){{
                    setPos(50, 0);
                }},
                new QuestNode("pain2"){{
                    setPos(75, 20);
                }},
                new QuestNode("pain3")
        );

        all.get(1).connect(all.get(2));
        all.get(1).data.completed = true;
    }
}
