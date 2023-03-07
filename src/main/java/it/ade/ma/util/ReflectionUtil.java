package it.ade.ma.util;

import lombok.SneakyThrows;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public interface ReflectionUtil {

    @SneakyThrows
    static Object getValue(Object o, String fieldName) {
        PropertyDescriptor pd = new PropertyDescriptor(fieldName, o.getClass());
        Method getter = pd.getReadMethod();
        return getter.invoke(o);
    }

}
