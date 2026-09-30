import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuffmanNode implements Comparable<HuffmanNode> {

    public char character;
    public int frequency;
    HuffmanNode left;
    HuffmanNode right;

    private char minChar;

    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.minChar = character;
        this.left = null;
        this.right = null;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.character = '\0';
        this.frequency = left.frequency + right.frequency;
        this.minChar = (char) Math.min(left.minChar, right.minChar);
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        if (frequency != other.frequency) {
            return Integer.compare(frequency, other.frequency);
        }
        return Character.compare(minChar, other.minChar);
    }

    /*
    Creates a node for every entry in the HashMap and stores those nodes into an array list
     */
    public static List<HuffmanNode> createNodes(HashMap<Character, Integer> map) {
        List<HuffmanNode> nodeList = new ArrayList<>();

        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            nodeList.add(node);
        }
        return nodeList;
    }

    @Override
    public String toString() {
        return "Character: " + character + " Frequency: " + frequency;
    }
}
