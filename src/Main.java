import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ProcessNode test = new ProcessNode(1, "test", 1.2);
        ProcessNode test2 = new ProcessNode(1, "test", 1.2);
        InputNode test3 = new InputNode(0, "test", 1.3, true);
        System.out.println(test);
        System.out.println(test2);
        System.out.println(test3);

        InputNode copy = test3;
        System.out.println(copy);

        HashMap<IdentityData, Node> map = new HashMap<>();
        map.put(test3.getIdentity(), test2);
        map.put(copy.getIdentity(), test2);
        System.out.println(map);
    }
}