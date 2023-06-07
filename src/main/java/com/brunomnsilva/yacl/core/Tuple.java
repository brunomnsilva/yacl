package com.brunomnsilva.yacl.core;

/**
 * A simple generic class representing a tuple with two elements of types T1 and T2.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 *
 * @author brunomnsilva
 */
public class Tuple<T1, T2> {
    private T1 first;
    private T2 second;

    /**
     * Constructs a new Tuple with the specified first and second elements.
     *
     * @param first  the first element of the tuple
     * @param second the second element of the tuple
     */
    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the tuple.
     *
     * @return the first element of the tuple
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Sets the first element of the tuple.
     *
     * @param first the first element to set
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * Returns the second element of the tuple.
     *
     * @return the second element of the tuple
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Sets the second element of the tuple.
     *
     * @param second the second element to set
     */
    public void setSecond(T2 second) {
        this.second = second;
    }
}

