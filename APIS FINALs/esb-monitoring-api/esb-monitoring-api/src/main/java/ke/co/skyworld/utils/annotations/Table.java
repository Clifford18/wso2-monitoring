package ke.co.skyworld.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * sky-core (ke.co.skyworld.utilities.annotations)
 * Created by: elon
 * On: 22 Jul, 2019 22/07/19 11:18
 **/
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}
