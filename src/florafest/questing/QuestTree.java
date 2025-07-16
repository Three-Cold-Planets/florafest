package florafest.questing;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Button;
import arc.struct.Seq;
import mindustry.content.TechTree;

public class QuestTree {
    private static QuestNode context = null;

    public static Seq<QuestTree> allTrees = Seq.with();

    public Seq<QuestNode> all = Seq.with();

    public QuestTree(TechTree.TechNode root){
        this.root = root;
        allTrees.add(this);
    }

    public TechTree.TechNode root;

    public static class QuestNode{

        public QuestNode(){
            data = new NodeData();
        }

        public QuestNode(NodeData data){
            this.data = data;
        }

        public NodeData data;

        //Loaded from data after every node is initialized
        public Seq<QuestNode> connections;
        public TextureRegion icon;


        public Button button;

        public TextureRegion defaultIcon(){
            return Core.atlas.find("duo");
        }

        public void setPos(float x, float y){
            data.x = x;
            data.y = y;
        }
    }

    public static class NodeData{
        public float x;
        public float y;
        public String name;
        public Seq<String> connections = Seq.with();
    }

    public Seq<NodeData> outputData(){
        Seq<NodeData> output = new Seq<NodeData>();
        all.each(node -> {
            output.add(node.data);
        });
        return output;
    }

    public void load(Seq<NodeData> in){
        all.clear();
        in.each(data -> {
            QuestNode node = new QuestNode(data);

            all.add(node);
        });
    }
}
