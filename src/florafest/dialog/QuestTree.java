package florafest.dialog;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.Element;
import arc.scene.ui.Button;
import arc.struct.Seq;
import mindustry.content.TechTree;
import mindustry.ui.dialogs.ResearchDialog;

public class QuestTree {
    private static QuestNode context = null;

    public static Seq<QuestTree> allTrees = Seq.with();

    public Seq<QuestNode> all = Seq.with();

    public QuestTree(TechTree.TechNode root){
        this.root = root;
        allTrees.add(this);
    }

    public TechTree.TechNode root;

    public class QuestNode{
        public float x;
        public float y;
        public QuestNode parrent;
        public TextureRegion icon;
        public Button button;

        public TextureRegion defaultIcon(){
            return Core.atlas.find("duo");
        }

        public boolean root(){
            return parrent == null;
        }
    }
}
