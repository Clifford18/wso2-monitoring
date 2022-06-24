package ke.co.skyworld.domain.beans;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

/**
 * sahihi-le-scedar-api (ke.co.scedar.sahihi.configurations.beans)
 * Created by: elon
 * On: 05 Jun, 2018 6/5/18 4:30 PM
 **/
public class NullAwareBeanUtils extends BeanUtilsBean{

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if(value==null)return;
        super.copyProperty(dest, name, value);
    }

}