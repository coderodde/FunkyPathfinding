package net.coderodde.funky.pathfinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements an indexed Fibonacci heap. "Indexed" means that this 
 * heap maintains internally a hash map mapping each present element to the heap 
 * node holding that element. This allows efficient decrease key operation.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type. 
 */
public final class FibonacciHeap<E, P extends Comparable<? super P>> {

    private static final int DEFAULT_CHILD_ARRAY_LENGTH = 5;
    
    /**
     * This class implements the Fibonacci heap nodes.
     * 
     * @param <E> the element type.
     * @param <P> the priority key type.
     */
    private static final class FibonacciHeapNode<E, P> {
        
        /**
         * The actual element.
         */
        private final E element;
        
        /**
         * The priority key of this node.
         */
        private P priority;
        
        /**
         * The parent node of this node.
         */
        private FibonacciHeapNode<E, P> parent;
        
        /**
         * The left sibling.
         */
        private FibonacciHeapNode<E, P> left = this;
        
        /**
         * The right sibling.
         */
        private FibonacciHeapNode<E, P> right = this;
        
        /**
         * The number of children of this node.
         */
        private FibonacciHeapNode<E, P> child;
        
        /**
         * The number of children this node has.
         */
        private int degree;
        
        /**
         * Indicates whether this node has lost a child since the last time this
         * node was made the child of another node.
         */
        private boolean marked;
        
        FibonacciHeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * Fibonacci heap -related math.
     */
    private static final double LOG_PHI = Math.log((1 + Math.sqrt(5)) / 2);
    
    /**
     * The node with the minimum priority.
     */
    private FibonacciHeapNode<E, P> minimumNode;
    
    /**
     * The number of elements stored in this Fibonacci heap.
     */
    private int size;
    
    /**
     * The cached array for consolidation routine.
     */
    private FibonacciHeapNode<E, P>[] array = 
            new FibonacciHeapNode[DEFAULT_CHILD_ARRAY_LENGTH];
    
    /**
     * Maps each element to its Fibonacci heap node.
     */
    private final Map<E, FibonacciHeapNode<E, P>> map = new HashMap<>();
    
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // The element is already stored in this heap.
            return;
        }
        
        FibonacciHeapNode<E, P> node = new FibonacciHeapNode<>(element, 
                                                               priority);
        
        if (minimumNode != null) {
            node.left = minimumNode;
            node.right = minimumNode.right;
            minimumNode.right = node;
            node.right.left = node;
            
            if (priority.compareTo(minimumNode.priority) < 0) {
                minimumNode = node;
            }
        } else {
            minimumNode = node;
        }
        
        map.put(element, node);
        ++size;
    }

    public boolean decreasePriority(E element, P newPriority) {
        FibonacciHeapNode<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // The element is not in this heap.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // The element IS in this heap, yet we cannot improve its priority.
            return false;
        }
        
        targetNode.priority = newPriority;
        FibonacciHeapNode<E, P> y = targetNode.parent;
        FibonacciHeapNode<E, P> x = targetNode;
        
        if (y != null && x.priority.compareTo(y.priority) < 0) {
            cut(x, y);
            cascadingCut(y);
        }
        
        if (minimumNode.priority.compareTo(x.priority) > 0) {
            minimumNode = x;
        }
        
        return true;
    }

    public E extractMinimum() {
        checkHeapIsNotEmpty();
        
        FibonacciHeapNode<E, P> z = minimumNode;
        int numberOfChildren = z.degree;
        FibonacciHeapNode<E, P> x = z.child;
        FibonacciHeapNode<E, P> tmpRight;
        
        while (numberOfChildren > 0) {
            tmpRight = x.right;
            
            x.left.right = x.right;
            x.right.left = x.left;
            
            x.left = minimumNode;
            x.right = minimumNode.right;
            minimumNode.right = x;
            x.right.left = x;
            
            x.parent = null;
            x = tmpRight;
            numberOfChildren--;
        }
        
        z.left.right = z.right;
        z.right.left = z.left;
        
        if (z == z.right) {
            minimumNode = null;
        } else {
            minimumNode = z.right;
            consolidate();
        }
        
        --size;
        E element = z.element;
        map.remove(element);
        return element;
    }

    public int size() {
        return size;
    }

    public void clear() {
        minimumNode = null;
        size = 0;
        map.clear();
    }
    
    @Override
    public String toString() {
        return "IndexedFibonacciHeap";
    }
    
    private void cut(FibonacciHeapNode<E, P> x, FibonacciHeapNode<E, P> y) {
        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;
        
        if (y.child == x) {
            y.child = x.right;
        }
        
        if (y.degree == 0) {
            y.child = null;
        }
        
        x.left = minimumNode;
        x.right = minimumNode.right;
        minimumNode.right = x;
        x.right.left = x;
        
        x.parent = null;
        x.marked = false;
    }
    
    private void cascadingCut(FibonacciHeapNode<E, P> y) {
        FibonacciHeapNode<E, P> z = y.parent;
        
        if (z != null) {
            if (y.marked == false) {
                y.marked = true;
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(size) / LOG_PHI)) + 1;
        ensureArraySize(arraySize);
        Arrays.fill(array, null);
        
        FibonacciHeapNode<E, P> x = minimumNode;
        int rootListSize = 0;
        
        if (x != null) {
            rootListSize = 1;
            x = x.right;
            
            while (x != minimumNode) {
                rootListSize++;
                x = x.right;
            }
        }
        
        while (rootListSize > 0) {
            int degree = x.degree;
            FibonacciHeapNode<E, P> next = x.right;
            
            while (array[degree] != null) {
                FibonacciHeapNode<E, P> y = array[degree];
                
                if (x.priority.compareTo(y.priority) > 0) {
                    FibonacciHeapNode<E, P> tmp = y;
                    y = x;
                    x = tmp;
                }
                
                link(y, x);
                array[degree] = null;
                degree++;
            }
            
            array[degree] = x;
            x = next;
            rootListSize--;
        }
        
        minimumNode = null;
        
        for (FibonacciHeapNode<E, P> y : array) {
            if (y == null) {
                continue;
            }
            
            if (minimumNode == null) {
                minimumNode = y;
            } else {
                moveToRootList(y);
            }
        }
    }
    
    private void moveToRootList(FibonacciHeapNode<E, P> node) {
        node.left.right = node.right;
        node.right.left = node.left;
        
        node.left = minimumNode;
        node.right = minimumNode.right;
        minimumNode.right = node;
        node.right.left = node;
        
        if (minimumNode.priority.compareTo(node.priority) > 0) {
            minimumNode = node;
        }
    }
    
    private void link(FibonacciHeapNode<E, P> y, FibonacciHeapNode<E, P> x) {
        y.left.right = y.right;
        y.right.left = y.left;
        
        y.parent = x;
        
        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }
        
        ++x.degree;
    }
    
    private void ensureArraySize(int arraySize) {
        if (array.length < arraySize) {
            array = new FibonacciHeapNode[arraySize];
        }
    }
        
    
    /**
     * Makes sure that the heap is not empty. If it is, an exception is thrown.
     */
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "This IndexedFibonacciHeap is empty.");
        }
    }
}
