package iot.exceptions;

/**
 * This exception is thrown when there is an error initializing
 * the database
 */
public class DatabaseInitializationException extends Exception{
    public DatabaseInitializationException(String message){
        super(message);
    }
}
