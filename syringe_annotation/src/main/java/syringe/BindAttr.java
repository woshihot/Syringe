package syringe;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;
/**
 * Created by Fred Zhao on 2017/5/5.
 */
@Retention(CLASS)
@Target(FIELD)
public @interface BindAttr {

}
