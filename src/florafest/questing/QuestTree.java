package florafest.questing;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.TechTree;

import java.util.UUID;

public class QuestTree {
    public String name;

    private static QuestNode context = null;

    public static Seq<QuestTree> allTrees = Seq.with();

    public Seq<QuestNode> all = Seq.with();

    public QuestTree(String name, TechTree.TechNode root){
        this.root = root;
        this.name = name;
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
        public Seq<QuestNode> connections = Seq.with();
        public TextureRegionDrawable icon;
        public TextureRegion texture;


        public Button button;

        public TextureRegion defaultRegion(){
            return Blocks.duo.uiIcon;
        }

        public void setPos(float x, float y){
            data.x = x;
            data.y = y;
        }
    }

    public static class NodeData{

        public NodeData(){
            name = UUID.randomUUID().toString();
            connections = Seq.with();
        }

        public float x;
        public float y;
        public String name;
        public Seq<String> connections;
        public boolean completed = false;
        public boolean hidden = false;
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
            node.data.connections.each(c -> {
                QuestNode other = all.find(n -> n.data.name.equals(c));
                if(other != null) node.connections.add(other);
            });

            all.add(node);
        });
    }

    //For those who want to define their tree in code
    public void loadDefaults(){

    }
}
