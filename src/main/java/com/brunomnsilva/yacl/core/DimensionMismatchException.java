package com.brunomnsilva.yacl.core;

/**
 * Exception thrown to indicate a mismatch between the lengths of vectors.
 * This exception is typically used when operations are performed on vectors or matrices
 * with incompatible dimensions.
 *
 * @author brunomnsilva
 */
public class DimensionMismatchException extends RuntimeException {

    /**
     * Constructs a new DimensionMismatchException with no specified detail message.
     */
    public DimensionMismatchException() {
    }

    /**
     * Constructs a new DimensionMismatchException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public DimensionMismatchException(String message) {
        super(message);
    }
}
