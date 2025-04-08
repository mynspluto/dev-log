import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Base {
  public static void main(String[] args) {
    System.out.println("11");

    List<String> names = List.of("Alice", "Bob", "Chris");
    System.out.println(names);
    names.stream()
        .filter(name -> name.startsWith("c"))
        .forEach(System.out::println);

    Map<String, Integer> score = new HashMap<>();
    score.put("Alice", 90);
    score.put("Bob", 85);
    score.put("Chrlie", 92);
    for (Map.Entry<String, Integer> entry : score.entrySet()) {
      System.out.println(entry.getKey() + " -> " + entry.getValue());
    }
    int bobScore = score.get("Bob");
    System.out.println("Bob's score: " + bobScore);

    List<Person> people = new ArrayList<>(List.of(
        new Person("Alice", 30),
        new Person("Bob", 25),
        new Person("Charlie", 35)));

    // 나이 기준 오름차순
    people.sort((p1, p2) -> p1.age - p2.age);

    people.forEach(System.out::println);
  }

  public static class Person {
    String name;
    int age;

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String toString() {
      return name + " (" + age + ")";
    }
  }
}
