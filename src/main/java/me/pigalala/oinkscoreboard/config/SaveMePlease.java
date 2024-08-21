package me.pigalala.oinkscoreboard.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SaveMePlease {

    // Name to save the value as
    String value();
}
