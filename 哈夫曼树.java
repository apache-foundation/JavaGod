import java.util.ArrayList;
import java.util.PriorityQueue;

public class Huffman {
    class Node implements Comparable<Node>{
        int weight;
        Node left;
        Node right;
        Node(int weight) {
            this.weight = weight;
        }

        @Override
        public int compareTo(Node o) {
            if (weight > o.weight)
                return 1;
            else if (weight < o.weight)
                return -1;
            else
                return 0;
        }
    }

    Node root;
    PriorityQueue<Node> nodes = new PriorityQueue<>();

    void build() {
        while (nodes.size() != 1) {
            Node left = nodes.poll();
            Node right = nodes.poll();
            Node parent = new Node(left.weight + right.weight);
            parent.left = left;
            parent.right = right;
            nodes.add(parent);
        }
        root = nodes.poll();
    }

    void addNode(int weight) {
        nodes.add(new Node(weight));
    }

    void showTree() {
        ArrayList<Node> a = new ArrayList<>();
        ArrayList<Node> b = new ArrayList<>();
        a.add(root);
        while (!a.isEmpty()) {
            for (Node node: a) {
                System.out.print(node.weight + "|");
                if (node.left != null)
                    b.add(node.left);
                if (node.right != null)
                    b.add(node.right);
            }
            System.out.println();
            a = (ArrayList<Node>) b.clone();
            b.clear();
        }
    }

    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        huffman.addNode(5);
        huffman.addNode(2);
        huffman.addNode(7);
        huffman.addNode(13);
        huffman.build();
        huffman.showTree();
    }
}


