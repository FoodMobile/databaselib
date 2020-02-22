package com.foodmobile.databaselib.exceptions;

public class NoAdapterOpenException extends Exception {
    @Override
    public String toString() {
        return "No database adapter is currently open!";
    }
}
