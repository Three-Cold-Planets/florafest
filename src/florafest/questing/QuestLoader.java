package florafest.questing;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Writes;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class QuestLoader {
    public static Fi workingDir = new Fi("C:\\Users\\Sh1p\\AppData\\Roaming\\Mindustry\\questing");

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
        TestingObject output = j.fromJson(TestingObject.class, workingDir.child("test.json"));
        Log.info(output.name);

        TestingObject saving = new TestingObject("hewo :3");
        j.toJson(saving, TestingObject.class, workingDir.child("output.json"));
    }

    public static void save(QuestTree tree){
        j.toJson(tree.outputData(), Seq.class, workingDir.child("output.json"));
    }
    public static void load(QuestTree tree){
        Seq<QuestTree.NodeData> o = (Seq<QuestTree.NodeData>) j.fromJson(Seq.class, workingDir.child("output.json"));
        tree.load(o);
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

    private interface FieldParser{
        Object parse(Class<?> type, JsonValue value) throws Exception;
    }
}
