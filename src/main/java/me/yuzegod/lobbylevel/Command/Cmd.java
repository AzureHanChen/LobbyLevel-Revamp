package me.yuzegod.lobbylevel.Command;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd {
    String value() default "";
    
    int minArgs() default 0;
    
    String description() default "";
    
    String permission() default "LobbyLevel.default";
    
    boolean onlyPlayer() default false;
    
    boolean onlyConsole() default false;
}
