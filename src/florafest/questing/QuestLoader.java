package florafest.questing;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Threads;
import arc.util.io.Writes;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.io.JsonIO;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class QuestLoader {
    public static Fi workingDir = Vars.dataDirectory.child("questing");

    public static Json j = new Json(){
        private <T> T internalRead(Class<T> type, Class elementType, JsonValue jsonData, Class keyType) {
            if (classParsers.containsKey(type)) {
                try {
                    return (T) classParsers.get(type).parse(type, jsonData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    };

    public static ObjectMap<Class<?>, FieldParser> classParsers = new ObjectMap<>(){{
        put(QuestTree.NodeData.class, (type, data) -> {
            QuestTree.NodeData output = new QuestTree.NodeData();
            output.x = data.get("x").size;
            output.x = data.get("y").size;

            return output;
        });
    }};

    public static void test(){
        Seq<TestingObject2> saving = Seq.with(new TestingObject2(Seq.with(69, 1337)));
        j.toJson(saving, Seq.class, TestingObject2.class, workingDir.child("test.json"));

        Seq<TestingObject2> output = j.fromJson(Seq.class, TestingObject2.class, workingDir.child("test.json"));
        Log.info(output.get(0).stuff);

        /*
        try {
            QuestTree.NodeData data = j.fromJson(QuestTree.NodeData.class, workingDir.child("output.json"));
            Log.info(data.x);
            Log.info(data.y);
        }
        catch(Error e){
            Log.info(Thread.getAllStackTraces());
        }


         */


    }

    public static void save(QuestTree tree){
        JsonIO.json.toJson(tree.outputData(), Seq.class, QuestTree.NodeData.class, workingDir.child(tree.name + ".json"));
    }

    public static boolean load(QuestTree tree){
        Fi questFile = workingDir.child(tree.name + ".json");

        if(questFile.length() == 0) {
            tree.loadDefaults();
            return false;
        }
        Seq<QuestTree.NodeData> o = JsonIO.json.fromJson(Seq.class, QuestTree.NodeData.class, workingDir.child(tree.name + ".json"));
        tree.load(o);
        return true;
    }

    public static class TestingObject{

        //Just for json
        public TestingObject(){

        }

        public TestingObject(String name){
            this.name = name;
        }

        public String name = "";
    }
    public static class TestingObject2{

        //Just for json
        public TestingObject2(){

        }

        public TestingObject2(Seq<Integer> stuff){
            this.stuff = stuff;
        }

        public Seq<Integer> stuff = new Seq<>();
    }

    private interface FieldParser{
        Object parse(Class<?> type, JsonValue value) throws Exception;
    }
}
