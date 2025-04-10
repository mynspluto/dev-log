import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Base2 {
  public static void main(String[] args) {
    List<String> names = new ArrayList<>(List.of("asd", "sdf", "aaa"));
    List<String> _names = new ArrayList<>(names);
    names.sort((a, b) -> {
      return a.compareTo(b);
    });
    names.stream().forEach(v -> {
      System.out.println(v);
    });
    System.out.println(_names);

    Map<String, Object> map = new HashMap<>(Map.of("aa", 1, "bb", "2"));
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object val = entry.getValue();

      System.out.println("key: " + key + " value: " + val);
    }

    List<Map.Entry<String, Object>> entries = new ArrayList<>(map.entrySet());
    entries.sort((a, b) -> {
      String key = a.getKey();
      Object val = a.getValue();

      return key.compareTo(b.getKey());
      // "apple".compareTo("banana") → -1 (apple이 앞)
      // "cherry".compareTo("banana") → 1 (cherry가 뒤)
      // "apple".compareTo("apple") → 0 (같음)
    });
  }
}
