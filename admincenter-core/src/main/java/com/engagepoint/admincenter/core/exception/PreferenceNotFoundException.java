package com.engagepoint.admincenter.core.exception;

/**
 * User: dmytro.palczewski
 * Date: 3/17/14
 * Time: 4:13 PM
 */
public class PreferenceNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public PreferenceNotFoundException(String message) {
        super(message);
    }

    public PreferenceNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
