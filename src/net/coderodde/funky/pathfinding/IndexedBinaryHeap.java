package net.coderodde.funky.pathfinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements an indexed binary heap that supports 
 * {@code decreasePriority} in logarithmic time. "Indexed" means that this heap
 * maintains internally a hash map mapping each present element to the heap node
 * holding that element. This allows efficient decrease key operation.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class IndexedBinaryHeap<E, P extends Comparable<? super P>> {

    /**
     * This class bundles the element and its priority.
     * 
     * @param <E> the element type.
     * @param <P> the priority type;
     */
    private static final class BinaryHeapNode<E, P> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The priority of {@code element}.
         */
        P priority;
        
        /**
         * The node array index at which this node is stored.
         */
        int index;
        
        BinaryHeapNode(E element, P priority, int index) {
            this.element = element;
            this.priority = priority;
            this.index = index;
        }
    }
    
    /**
     * The default capacity of the underlying array.
     */
    private static final int DEFAULT_CAPACITY = 1024;
    
    /**
     * Stores the actual array of binary heap nodes.
     */
    private BinaryHeapNode[] binaryHeapNodeArray;
    
    /**
     * Caches the number of elements hold by this heap.
     */
    private int size;
    
    /**
     * This map maps each element to its node.
     */
    private final Map<E, BinaryHeapNode<E, P>> map = new HashMap<>();
    
    public IndexedBinaryHeap() {
        this.binaryHeapNodeArray = new BinaryHeapNode[DEFAULT_CAPACITY];
    }
    
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
            
        expandStorageArrayIfNeeded();
        
        BinaryHeapNode<E, P> newBinaryHeapNode = new BinaryHeapNode<>(element,
                                                                      priority,
                                                                      size);
        binaryHeapNodeArray[size] = newBinaryHeapNode;
        siftUp(size++);
        map.put(element, newBinaryHeapNode);
    }

    public boolean decreasePriority(E element, P newPriority) {
        BinaryHeapNode<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        targetNode.priority = newPriority;
        siftUp(targetNode.index);
        return true;
    }

    public E top() {
        checkHeapIsNotEmpty();
        return (E) binaryHeapNodeArray[0].element;
    }
    
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        BinaryHeapNode<E, P> topNode = binaryHeapNodeArray[0];
        binaryHeapNodeArray[0] = binaryHeapNodeArray[--size];
        siftDownRoot();
        binaryHeapNodeArray[size] = null;
        E element = topNode.element;
        map.remove(element);
        return element;
    }

    public int size() {
        return size;
    }
    
    public void clear() {
        Arrays.fill(binaryHeapNodeArray, 0, size, null);
        map.clear();
        size = 0;
    }
    
    @Override
    public String toString() {
        return "IndexedBinaryHeap";
    }
    
    private void siftUp(int index) {
        if (index == 0) {
            return;
        }
     
        BinaryHeapNode<E, P> targetNode = binaryHeapNodeArray[index];
        P targetNodePriority = targetNode.priority;
        int parentNodeIndex = getParentNodeIndex(index);
        
        while (true) {
            BinaryHeapNode<E, P> parentNode = 
                    binaryHeapNodeArray[parentNodeIndex];
            
            P parentNodePriority = parentNode.priority;
            
            if (targetNodePriority.compareTo(parentNodePriority) < 0) {
                binaryHeapNodeArray[index] = parentNode;
                parentNode.index = index;
                
                index = parentNodeIndex;
                parentNodeIndex = getParentNodeIndex(index);
            } else {
                break;
            }
            
            if (index == 0) {
                break;
            }
        }
        
        binaryHeapNodeArray[index] = targetNode;
        targetNode.index = index;
    }
    
    private void siftDownRoot() {
        int index = 0;
        int leftChildNodeIndex = getLeftChildIndex(0);
        int rightChildNodeIndex = leftChildNodeIndex + 1;
        int minChildNodeIndex = 0;
        
        BinaryHeapNode<E, P> targetHeapNode = binaryHeapNodeArray[0];
        BinaryHeapNode<E, P> leftChildNode = null;
        
        while (true) {
            if (leftChildNodeIndex < size) {
                leftChildNode = binaryHeapNodeArray[leftChildNodeIndex];
                
                if (leftChildNode.priority
                        .compareTo(targetHeapNode.priority) < 0) {
                    minChildNodeIndex = leftChildNodeIndex;
                }
            } else {
                // This avoids checking 'minChildNodeIndex == index' which was
                // measured to have a positive effect on efficiency.
                binaryHeapNodeArray[minChildNodeIndex] = targetHeapNode;
                targetHeapNode.index = minChildNodeIndex;
                return;
            } 
            
            if (minChildNodeIndex == index) {
                if (rightChildNodeIndex < size) {
                    BinaryHeapNode<E, P> rightChildNode = 
                            binaryHeapNodeArray[rightChildNodeIndex];
                    
                    if (rightChildNode.priority
                            .compareTo(targetHeapNode.priority) < 0) {
                        minChildNodeIndex = rightChildNodeIndex;
                    }
                }
            } else {
                if (rightChildNodeIndex < size) {
                    BinaryHeapNode<E, P> rightChildNode = 
                            binaryHeapNodeArray[rightChildNodeIndex];
                    
                    if (rightChildNode.priority
                            .compareTo(leftChildNode.priority) < 0) {
                        minChildNodeIndex = rightChildNodeIndex;
                    }
                }
            }
            
            if (minChildNodeIndex == index) {
                binaryHeapNodeArray[minChildNodeIndex] = targetHeapNode;
                targetHeapNode.index = minChildNodeIndex;
                return;
            }
            
            binaryHeapNodeArray[index] = binaryHeapNodeArray[minChildNodeIndex];
            binaryHeapNodeArray[index].index = index;
            index = minChildNodeIndex;
            leftChildNodeIndex = getLeftChildIndex(index);
            rightChildNodeIndex = leftChildNodeIndex + 1;
        }
    }
    
    /**
     * Given the index of a start node, returns the index of the parent node of
     * the start node.
     * 
     * @param index the index of the start node.
     * @return the index of the parent node of the start node.
     */
    private static int getParentNodeIndex(int index) {
        return (index - 1) >>> 1;
    }
    
    /**
     * Given the index of a start node, returns the index of the left child node
     * of the start node. The index of the right child node may be computed via
     * {@code getLeftChildIndex(index) + 1}.
     * 
     * @param index the index of the start node.
     * @return the index of the parent node of the start node.
     */
    private static int getLeftChildIndex(int index) {
        return (index << 1) + 1;
    }
    
    /**
     * Makes sure that the underlying storage array has capacity for new
     * elements.
     */
    private void expandStorageArrayIfNeeded() {
        if (size == binaryHeapNodeArray.length) {
            binaryHeapNodeArray = Arrays.copyOf(binaryHeapNodeArray, 2 * size);
        }
    }
    
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "This IndexedBinaryHeap is empty.");
        }
    }
}
