package MonoLightTech;

import org.json.simple.JSONObject;

public class Summary {
    public static boolean Save() {
        JSONObject json = new JSONObject();
        json.put("A", 123);
        json.put("B", "Test");
        System.out.println(json.toJSONString());

        return false;
    }
}
