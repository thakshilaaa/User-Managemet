package org.example.datastructure;

import java.util.function.Predicate;

/**
 * Custom generic LinkedList implementation
 * @param <T> Type of elements in the list
 */
public class LinkedList<T> {
    private Node<T> head;
    private int size;

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Add an element to the end of the list
     * @param data Element to add
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    /**
     * Remove the first occurrence of an element
     * @param data Element to remove
     * @return true if element was found and removed, false otherwise
     */
    public boolean remove(T data) {
        if (head == null) return false;

        if (head.getData().equals(data)) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().equals(data)) {
                current.setNext(current.getNext().getNext());
                size--;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Find first element matching the predicate
     * @param predicate Condition to match
     * @return First matching element or null if none found
     */
    public T find(Predicate<T> predicate) {
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.getData())) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Update first element matching the predicate
     * @param predicate Condition to match
     * @param newData New data to set
     * @return true if element was found and updated, false otherwise
     */
    public boolean update(Predicate<T> predicate, T newData) {
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.getData())) {
                current.setData(newData);
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Get all elements as an array
     * @return Array containing all elements
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        if (size == 0) {
            return (T[]) new Object[0];
        }
        
        // Get the first non-null element to determine the component type
        Node<T> current = head;
        while (current != null && current.getData() == null) {
            current = current.getNext();
        }
        
        // If all elements are null, return Object array
        if (current == null) {
            return (T[]) new Object[size];
        }
        
        // Create array of the correct type
        T[] array = (T[]) java.lang.reflect.Array.newInstance(current.getData().getClass(), size);
        
        // Fill the array
        current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.getData();
            current = current.getNext();
        }
        
        return array;
    }

    /**
     * Get size of the list
     * @return Number of elements in the list
     */
    public int size() {
        return size;
    }

    /**
     * Check if list is empty
     * @return true if list has no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
} 