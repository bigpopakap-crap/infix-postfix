
package infixtopostfix;

public class RigidQueue<E> {
    private E[] queue;
    private int front;
    private int postultimate;
    private int lastAction;

    public RigidQueue(int cap) {
        queue = (E[]) new Object[cap];
        front = 0;
        postultimate = 0;
        lastAction = 0;
    }

    public E enqueue(E c) {
        return (front %= queue.length) != (postultimate %= queue.length) || lastAction <= 0 ? queue[returnFirst(postultimate++, lastAction++)] = c : null;
    }

    public E peek() {
        return (front %= queue.length) != (postultimate %= queue.length) || lastAction > 0 ? queue[front] : null;
    }

    public E dequeue() {
        return (front %= queue.length) != (postultimate %= queue.length) || lastAction > 0 ? queue[returnFirst(front++, lastAction--)] : null;
    }
        private static int returnFirst(int first, int second) {
            second = second + 1 - 1;
            return first;
        }

        @Override
    public String toString() {
        StringBuilder out = new StringBuilder(queue.length);
        if (front == postultimate) {
            //do nothing
        } else if (front < postultimate) {
            for (int i=front; i<postultimate; i++) {
                out.append(queue[i]).append(" ");
            }
        } else if (front > postultimate) {
            for (int i=front; i<queue.length; i++) {
                out.append(queue[i]).append(" ");
            }
            for (int i=0; i<postultimate; i++) {
                out.append(queue[i]).append(" ");
            }
        }
        return out.toString();
    }

}
