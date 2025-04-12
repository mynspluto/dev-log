// 배열 기반 Stack
class ArrayStack {
    int maxSize;
    int[] stackArray;
    int top;

    public ArrayStack(int size) {
        maxSize = size;
        stackArray = new int[maxSize];
        top = -1;
    }

    public void push(int value) {
        if (isFull()) {
            System.out.println("스택이 가득 찼습니다.");
            return;
        }
        stackArray[++top] = value;
    }

    public int pop() {
        if (isEmpty()) {
            System.out.println("스택이 비었습니다.");
            return -1;
        }
        return stackArray[top--];
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("스택이 비었습니다.");
            return -1;
        }
        return stackArray[top];
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == maxSize - 1);
    }
}

// 배열 기반 Queue (원형 큐)
class ArrayQueue {
    int maxSize;
    int[] queueArray;
    int front;
    int rear;
    int itemCount;

    public ArrayQueue(int size) {
        maxSize = size;
        queueArray = new int[maxSize];
        front = 0;
        rear = -1;
        itemCount = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            System.out.println("큐가 가득 찼습니다.");
            return;
        }
        if (rear == maxSize - 1)
            rear = -1;
        queueArray[++rear] = value;
        itemCount++;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("큐가 비었습니다.");
            return -1;
        }
        int temp = queueArray[front++];
        if (front == maxSize)
            front = 0;
        itemCount--;
        return temp;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("큐가 비었습니다.");
            return -1;
        }
        return queueArray[front];
    }

    public boolean isEmpty() {
        return (itemCount == 0);
    }

    public boolean isFull() {
        return (itemCount == maxSize);
    }
}

// 연결 리스트 기반 Stack
class LinkedListStack {
    class StackNode {
        int data;
        StackNode next;

        StackNode(int data) {
            this.data = data;
        }
    }

    StackNode top;

    public void push(int data) {
        StackNode newNode = new StackNode(data);
        newNode.next = top;
        top = newNode;
    }

    public int pop() {
        if (isEmpty()) {
            System.out.println("스택이 비었습니다.");
            return -1;
        }
        int value = top.data;
        top = top.next;
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("스택이 비었습니다.");
            return -1;
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }
}

// 연결 리스트 기반 Queue
class LinkedListQueue {
    class QueueNode {
        int data;
        QueueNode next;

        QueueNode(int data) {
            this.data = data;
        }
    }

    QueueNode front, rear;

    public void enqueue(int data) {
        QueueNode newNode = new QueueNode(data);
        if (rear != null)
            rear.next = newNode;
        rear = newNode;
        if (front == null)
            front = newNode;
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("큐가 비었습니다.");
            return -1;
        }
        int value = front.data;
        front = front.next;
        if (front == null)
            rear = null;
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("큐가 비었습니다.");
            return -1;
        }
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }
}

// 테스트용 메인 클래스
public class StackQueue {
    public static void main(String[] args) {
        // 배열 기반 Stack 테스트
        ArrayStack aStack = new ArrayStack(5);
        aStack.push(10);
        aStack.push(20);
        System.out.println("[배열 Stack] peek: " + aStack.peek());
        System.out.println("[배열 Stack] pop: " + aStack.pop());

        // 배열 기반 Queue 테스트
        ArrayQueue aQueue = new ArrayQueue(5);
        aQueue.enqueue(100);
        aQueue.enqueue(200);
        System.out.println("[배열 Queue] peek: " + aQueue.peek());
        System.out.println("[배열 Queue] dequeue: " + aQueue.dequeue());

        // 연결 리스트 기반 Stack 테스트
        LinkedListStack lStack = new LinkedListStack();
        lStack.push(30);
        lStack.push(40);
        System.out.println("[리스트 Stack] peek: " + lStack.peek());
        System.out.println("[리스트 Stack] pop: " + lStack.pop());

        // 연결 리스트 기반 Queue 테스트
        LinkedListQueue lQueue = new LinkedListQueue();
        lQueue.enqueue(300);
        lQueue.enqueue(400);
        System.out.println("[리스트 Queue] peek: " + lQueue.peek());
        System.out.println("[리스트 Queue] dequeue: " + lQueue.dequeue());
    }
}
