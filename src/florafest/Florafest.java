package florafest;

import florafest.content.FloraBlocks;
import florafest.content.FloraItems;
import mindustry.mod.*;
//import florafest.gen.*;

public class Florafest extends Mod{
    @Override
    public void loadContent(){
        //EntityRegistry.register();
        FloraItems.load();
        FloraBlocks.load();
    }
}
