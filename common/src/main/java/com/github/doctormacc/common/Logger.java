package com.github.doctormacc.common;

public interface Logger {

    void info(String message);
    void debug(String message);

    void setDebug(boolean debug);
}
