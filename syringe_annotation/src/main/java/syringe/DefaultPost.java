package syringe;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;
/**
 * Created by Fred Zhao on 2017/3/5.
 */
@Retention(CLASS)
@Target(METHOD)
public @interface DefaultPost {

}
