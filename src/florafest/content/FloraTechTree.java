package florafest.content;

import mindustry.content.TechTree;

public class FloraTechTree extends TechTree {
    public static TechNode root;

    public static void load(){
        root = nodeRoot("Centra", FloraBlocks.coreCentral, () -> {
            FloraItems.all.each(item -> {
                nodeProduce(item, () -> {});
            });
        });
    }
}
