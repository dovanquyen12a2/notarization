package com.vinorsoft.microservices.core.notarization.util.helpers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ClassHelper {
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        return fields;
    }
}
