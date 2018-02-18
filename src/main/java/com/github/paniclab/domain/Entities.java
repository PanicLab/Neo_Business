package com.github.paniclab.domain;


import java.lang.reflect.Field;
import java.util.Objects;

public final class Entities {

    private Entities() {
        throw new Entity.InternalException("This operation is not allowed.");
    }

    public static <T extends Entity> boolean hasSameContentExceptId(T one, T another) {
        int numberOfFields = one.getEntityClass().getDeclaredFields().length;
        if (another.getEntityClass().getDeclaredFields().length != numberOfFields) return false;

        Field[] fields = one.getEntityClass().getDeclaredFields();
        for (int x = 0; x < numberOfFields; x++  ) {
            Field current = fields[x];
            if (current.getName().equals("id")) continue;
            try {
                if (isNot(Objects.equals(current.get(one), current.get(another)))) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new Entity.InternalException("Unable to compare two entities. Entity: " + one +
                        System.lineSeparator() + "Another entity: " + another, e);
            }
        }

        return true;
    }


    public static <T> boolean hasSameContent(T one, T another) {
        int numberOfFields = one.getClass().getDeclaredFields().length;
        if (another.getClass().getDeclaredFields().length != numberOfFields) return false;

        Field[] fields = one.getClass().getDeclaredFields();
        for (int x = 0; x < numberOfFields; x++  ) {
            Field current = fields[x];
            try {
                if (isNot(Objects.equals(current.get(one), current.get(another)))) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new Entity.InternalException("Unable to compare two instances. Instance: " + one +
                        System.lineSeparator() + "Another instance: " + another, e);
            }
        }

        return true;
    }


    public static <T> T getCopyOf(T obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field current : fields) {
            if (current.getType().isPrimitive()) {

            }
        }

        return null;
    }





    private static boolean isNot(boolean statement) {
        return !statement;
    }
}
