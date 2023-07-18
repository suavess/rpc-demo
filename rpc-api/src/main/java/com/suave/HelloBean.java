package com.suave;

import java.io.Serializable;

/**
 * @author Suave
 * @since 2023/07/18 17:40
 */
public class HelloBean implements Serializable {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HelloBean(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HelloBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
