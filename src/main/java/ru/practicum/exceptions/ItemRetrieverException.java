package ru.practicum.exceptions;

public class ItemRetrieverException extends RuntimeException {
    public ItemRetrieverException(String message) {
        super(message);
    }

    public ItemRetrieverException(String message, Throwable cause) {
        super(message, cause);
    }
}
