
package infixtopostfix;

public class RigidStack<E> {
    private E[] stack;
    private int numElements;

    public RigidStack(int cap) {
        stack = (E[]) new Object[cap];
        numElements = 0;
    }

    public E push(E c) {
        return numElements < stack.length ? stack[numElements++] = c : null;
    }

    public E peek() {
        return numElements > 0 ? stack[numElements - 1] : null;
    }

    public E pop() {
        return numElements > 0 ? stack[--numElements] : null;
    }

}
