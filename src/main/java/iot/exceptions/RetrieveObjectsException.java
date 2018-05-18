package iot.exceptions;

/**
 * This exception is thrown when there is an error retrieving objects
 * from the database
 */
public class RetrieveObjectsException extends Exception {
    public RetrieveObjectsException(String message){
        super(message);
    }
}
