class BinarySearchTree {
    // 노드 클래스
    class Node {
        int key;
        Node left, right;

        public Node(int item) {
            key = item;
            left = right = null;
        }
    }

    // 루트 노드
    Node root;

    // 생성자
    BinarySearchTree() {
        root = null;
    }

    // 값 삽입 메소드
    void insert(int key) {
        root = insertRec(root, key);
        System.out.println("root: " + root.key);
    }

    // 재귀로 삽입
    Node insertRec(Node root, int key) {
        if (root == null) {
            root = new Node(key);
            return root;
        }
        if (key < root.key)
            root.left = insertRec(root.left, key);
        else if (key > root.key)
            root.right = insertRec(root.right, key);
        return root;
    }

    // 값 탐색 메소드
    boolean search(int key) {
        return searchRec(root, key) != null;
    }

    // 재귀로 탐색
    Node searchRec(Node root, int key) {
        if (root == null || root.key == key)
            return root;

        if (key < root.key)
            return searchRec(root.left, key);

        return searchRec(root.right, key);
    }

    // 중위 순회 (오름차순 출력)
    void inorder() {
        inorderRec(root);
        System.out.println();
    }

    void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.print(root.key + " ");
            inorderRec(root.right);
        }
    }

    // 메인 함수
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();

        /* 값 삽입 */
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);

        /* 중위 순회 결과 출력 */
        System.out.print("중위 순회 결과: ");
        tree.inorder();

        /* 탐색 */
        int findKey = 40;
        System.out.println(findKey + " 탐색 결과: " + (tree.search(findKey) ? "존재함" : "없음"));
    }
}
