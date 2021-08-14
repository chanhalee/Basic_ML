import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, HashMap> test1= new HashMap<>();
        HashMap<Integer, Integer> test = new HashMap<>();
        test.put(1, null);
        System.out.println(test);
        test.put(1, 2);
        System.out.println(test);
        test.put(1,3);
        System.out.println(test);
        test1.put(1, test);
        test.put(1,1);
        test1.put(2, test);
        System.out.println(test1);
        test.put(2, 2);
        System.out.println(test1);
    }
}
