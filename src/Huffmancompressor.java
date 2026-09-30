import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffmancompressor {

    /*
    repeatedly merges the lowest frequency nodes
     */
    public static HuffmanNode buildTree(List<HuffmanNode> nodes) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(nodes);   // FIX: pass nodes in
        while (queue.size() > 1) {
            HuffmanNode a = queue.poll();
            HuffmanNode b = queue.poll();
            queue.add(new HuffmanNode(a, b));
        }
        return queue.poll();
    }

    /*
    walks the tree: left = '0' and right = '1'
     */
    public static Map<Character, String> buildCodes(HuffmanNode root) {
        Map<Character, String> codes = new HashMap<>();
        if (root == null) {
            return codes;
        }
        if (root.isLeaf()) {
            codes.put(root.character, "0");
            return codes;
        }
        fillCodes(root, "", codes);
        return codes;
    }

    private static void fillCodes(HuffmanNode node, String path, Map<Character, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.character, path);
            return;
        }

        fillCodes(node.left, path + "0", codes);
        fillCodes(node.right, path + "1", codes);
    }   // FIX: close fillCodes here

    public static void compress(String inPath, String outPath, HashMap<Character, Integer> freq) throws IOException {

        HuffmanNode root = buildTree(HuffmanNode.createNodes(freq));
        Map<Character, String> codes = buildCodes(root);
        String text = Files.readString(Paths.get(inPath), StandardCharsets.UTF_8);

        // FIX: '=' instead of '-'
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outPath)))) {
            out.writeInt(freq.size());
            // FIX: value type is Integer, not String
            for (Map.Entry<Character, Integer> e : freq.entrySet()) {
                out.writeChar(e.getKey());
                out.writeInt(e.getValue());
            }

            int buffer = 0, bitCount = 0;
            for (int i = 0; i < text.length(); i++) {
                for (char bit : codes.get(text.charAt(i)).toCharArray()) {
                    buffer = (buffer << 1) | (bit - '0');
                    if (++bitCount == 8) {
                        out.write(buffer);
                        buffer = 0;
                        bitCount = 0;
                    }
                }
            }
            if (bitCount > 0) {
                out.write(buffer << (8 - bitCount));
            }
        }
    }

    public static void decompress(String inPath, String outPath) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(inPath)))) {

            int distinct = in.readInt();
            HashMap<Character, Integer> freq = new HashMap<>();
            long total = 0;
            for (int i = 0; i < distinct; i++) {
                char c = in.readChar();
                int f = in.readInt();
                freq.put(c, f);
                total += f;
            }

            StringBuilder result = new StringBuilder();
            if (distinct > 0) {
                HuffmanNode root = buildTree(HuffmanNode.createNodes(freq));

                if (root.isLeaf()) {
                    for (long i = 0; i < total; i++) result.append(root.character);
                } else {
                    HuffmanNode node = root;
                    long decoded = 0;
                    int b;
                    while (decoded < total && (b = in.read()) != -1) {
                        for (int bit = 7; bit >= 0 && decoded < total; bit--) {
                            node = ((b >> bit) & 1) == 0 ? node.left : node.right;
                            if (node.isLeaf()) {
                                result.append(node.character);
                                node = root;
                                decoded++;
                            }
                        }
                    }
                }
            }
            Files.writeString(Paths.get(outPath), result.toString(), StandardCharsets.UTF_8);
        }
    }
}   // FIX: only one closing brace for the class