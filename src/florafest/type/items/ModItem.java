package florafest.type.items;

import arc.graphics.Color;
import florafest.content.FloraItems;
import mindustry.type.Item;

public class ModItem extends Item {

    public ModItem(String name) {
        super(name);
    }

    public ModItem(String name, Color color) {
        super(name, color);
        FloraItems.all.add(this);
    }

    //There was an attempt
    @Override
    public void postInit() {
        super.postInit();

        /*
        if(localizedName.equals(name)){
            localizedName.replace("-", " ");
            for(int i = 0; i < localizedName.length(); i++){
                localizedName.split(" ");
            }

        }

         */
    }
}
