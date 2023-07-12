package ru.pin120.transystem.exceptions;

public class WarehouseNotFoundException extends RuntimeException{
    public WarehouseNotFoundException(String message) {
        super(message);
    }
}
