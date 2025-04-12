class MaxHeap {
    private int[] heap;
    private int size;
    private int capacity;

    // 생성자
    public MaxHeap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new int[capacity];
    }

    // 부모 인덱스 계산
    private int parent(int i) {
        return (i - 1) / 2;
    }

    // 왼쪽 자식 인덱스 계산
    private int leftChild(int i) {
        return 2 * i + 1;
    }

    // 오른쪽 자식 인덱스 계산
    private int rightChild(int i) {
        return 2 * i + 2;
    }

    // 힙 구성: 노드를 아래로 내려가며 힙의 특성 만족
    private void heapifyDown(int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int largest = i;

        if (left < size && heap[left] > heap[largest]) {
            largest = left;
        }
        if (right < size && heap[right] > heap[largest]) {
            largest = right;
        }

        if (largest != i) {
            // swap
            int temp = heap[i];
            heap[i] = heap[largest];
            heap[largest] = temp;

            // 재귀적으로 계속 heapify
            heapifyDown(largest);
        }
    }

    // 힙에 새 값을 추가
    public void insert(int value) {
        if (size == capacity) {
            System.out.println("Heap is full");
            return;
        }
        
        // 새로운 값을 배열 끝에 추가하고
        heap[size] = value;
        size++;

        // 부모와 비교하며 올바른 위치를 찾는다
        int i = size - 1;
        while (i != 0 && heap[parent(i)] < heap[i]) {
            // swap
            int temp = heap[i];
            heap[i] = heap[parent(i)];
            heap[parent(i)] = temp;

            i = parent(i);
        }
    }

    // 힙에서 최대값을 제거하고, 마지막 노드를 루트로 가져온 뒤, 재구성
    public void deleteMax() {
        if (size <= 0) {
            System.out.println("Heap is empty");
            return;
        }
        if (size == 1) {
            size--;
            return;
        }

        // 루트 노드와 마지막 노드를 교환
        heap[0] = heap[size - 1];
        size--;
        
        // 새로운 루트에서 heapify
        heapifyDown(0);
    }

    // 최대값을 반환 (삭제하지 않음)
    public int getMax() {
        if (size <= 0) {
            System.out.println("Heap is empty");
            return -1;
        }
        return heap[0];
    }
}

public class Heap {
    public static void main(String[] args) {
        MaxHeap maxHeap = new MaxHeap(10);
        maxHeap.insert(10);
        maxHeap.insert(20);
        maxHeap.insert(5);
        maxHeap.insert(30);
        maxHeap.insert(50);

        System.out.println("Max value: " + maxHeap.getMax());  // 최대값 출력 (50)

        maxHeap.deleteMax();  // 최대값 삭제
        System.out.println("Max value after deletion: " + maxHeap.getMax());  // 최대값 출력 (30)
    }
}
