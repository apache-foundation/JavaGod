import java.util.ArrayList;
import java.util.Arrays;

public class A {
    public static void main(String[] args) {
        B_Tree b_tree = new B_Tree(6);

        for (int i = 0; i < 80; i++) {
            int a = (int)(Math.random() * 100);
            b_tree.insert(a);
        }
        b_tree.showTree();
    }
}

class B_Tree { //B树
    int m; //B树的阶
    int h; //B树的高度
    BNode root;

    class BNode { //B树的结点
        int n = 0; //结点的元素数
        int[] keys = new int[m - 1];
        BNode[] children = new BNode[m];
        BNode father = null; //结点的父结点

        BNode() {
            for (int i = 0; i < m - 1; i++) {
                keys[i] = 99;
            }
        }

        void addKey(int v) {
            keys[n] = v;
            n++;
            Arrays.sort(keys);
        }

        boolean isFull() {
            return n == (m - 1);
        }
        //找到可能包含 v 的结点
        BNode findChild(int v) {
            for (int i = 0; i < m - 1; i++) {
                if (v <= keys[i])
                    return children[i];
            }
            return children[m - 1];
        }

        boolean containedKey(int v) {
            for (int i = 0; i < n; i++) {
                if (v == keys[i])
                    return true;
            }
            return false;
        }
    }

    B_Tree(int m) { //初始化一个 m 阶的B树
        this.m = m;
        root = new BNode();
    }

    void insert(int v) {
        BNode inNode = find(v);
        if (!inNode.containedKey(v))
            inNode.addKey(v);

        if (inNode.isFull())
            split(inNode);
    }

    BNode find(int v) { //返回可以插入 或已拥有的结点
        BNode temp = root;

        for (int i = 0; i < h; i++) {
            temp = temp.findChild(v);
        }

        return temp;
    }

    void split(BNode node) {
        //已经是根结点
        if (node.father == null) {
            node.father = new BNode();
            root = node.father;
            h++;
        }

        int n = m - 1; //最大键数

        //拆分当前结点
        //最中间的键上升到父结点
        int fatherKey = node.keys[n / 2];
        node.father.addKey(fatherKey);

        //两边的键分别生成两个结点 将原来结点的键 原来结点的孩子分别添加到新结点
        BNode leftNode = new BNode();
        leftNode.father = node.father;
        BNode rightNode = new BNode();
        rightNode.father = node.father;

        for (int i = 0; i < n / 2; i++) {
            leftNode.addKey(node.keys[i]);
        }
        for (int i = 0; i <= n / 2; i++) {
            if (node.children[i] != null) {
                leftNode.children[i] = node.children[i];
                leftNode.children[i].father = leftNode;
            }
        }

        for (int i = n / 2 + 1; i < m - 1; i++) {
            rightNode.addKey(node.keys[i]);
        }
        for (int i = 0, j = n / 2 + 1; i <= n / 2; i++, j++) {
            if (node.children[j] != null) {
                rightNode.children[i] = node.children[j];
                rightNode.children[i].father = rightNode;
            }
        }

        //将新生成的两个结点添加到原来结点的父结点中
        for (int i = 0; i < node.n; i++) {
            if (node.father.keys[i] == fatherKey) {
                node.father.children[i] = leftNode;
                if (node.father.children[i + 1] != null) {
                    for (int j = m - 1; j >= i + 1; j--) { //有东西 往后推
                        node.father.children[j] = node.father.children[j - 1];
                    }
                }
                node.father.children[i + 1] = rightNode;
            }
        }

        if (node.father.isFull())
            split(node.father);
    }

    void showTree() {
        ArrayList<BNode> nodeList = new ArrayList<>();
        ArrayList<BNode> tempList = new ArrayList<>();
        nodeList.add(root);

        for (int i = 0; i < h + 1; i++) {
            for (BNode temp: nodeList) {
                for (int j = 0; j < temp.n; j++) {
                    System.out.print(temp.keys[j] + " ");
                }
                System.out.print("|");
                for (int j = 0; j < m; j++) {
                    if (temp.children[j] != null)
                        tempList.add(temp.children[j]);
                }
            }
            System.out.println();
            nodeList = (ArrayList<BNode>) tempList.clone();
            tempList.clear();
        }
    }
}

