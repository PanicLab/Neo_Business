package com.github.paniclab.domain;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public interface Entity<T extends Serializable> {

    @SuppressWarnings("unchecked")
    static <ID extends Serializable, TYPE extends Entity<ID>> TYPE getDetached(ID id, Class<TYPE> clazz) {
        if (id == null) throw new NullPointerException("Attempt to create detached instance with id = null.");

        Constructor<TYPE> constructor;

        constructor = (Constructor<TYPE>)Arrays.stream(clazz.getDeclaredConstructors())
                                            .filter(c -> c.getGenericParameterTypes().length == 0)
                                            .findAny()
                                            .orElseThrow(() -> new RuntimeException("Unable to create instance of class " +
                                                    clazz.getCanonicalName() + ". This class has no appropriate " +
                                                    "constructor with no args."));

        TYPE entity;
        try {
            constructor.setAccessible(true);
            entity = constructor.newInstance();
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |NoSuchFieldException e) {
            throw new RuntimeException("Unable to create instance of class " + clazz.getCanonicalName(), e);
        }

        return entity;
    }

    T getId();

    Entity<T> getThis();

    default boolean isNotPersisted() {
        return !isAlreadyPersisted();
    }

    default boolean isAlreadyPersisted() {
        return getId() != null;
    }

    default Class<? extends Entity> getEntityClass() {
        return getThis().getClass();
    }

    @SuppressWarnings("unchecked")
    default Entity<T> getCopy() {

        Constructor<? extends Entity<T>> constructor =
                            (Constructor<? extends Entity<T>>)Arrays.stream(getThis().getClass().getDeclaredConstructors())
                                                    .filter(c -> c.getGenericParameterTypes().length == 0)
                                                    .findAny()
                                                    .orElseThrow(() -> new Entity.Exception(
                                                            "Entity class must have constructor with no args."
                                                    ));

        Entity<T> copy;
        try {
            constructor.setAccessible(true);
            copy = constructor.newInstance();

            Field[] fields = getThis().getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                f.set(copy, f.get(getThis()));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new Entity.Exception("Unable to create copy. Class: " + getThis().getClass().getCanonicalName() +
                    System.lineSeparator() + "Instance to copy: " + getThis(), e);
        }

        return copy;
    }

    @SuppressWarnings("unchecked")
    default Entity<T> getEmptyDetached() {
        Entity<T> instance = Entity.getDetached(getThis().getId(), getThis().getClass());
        return getThis().getClass().cast(instance);
    }

    class Exception extends RuntimeException {
        public Exception() {
        }

        Exception(String message) {
            super(message);
        }

        Exception(String message, Throwable cause) {
            super(message, cause);
        }

        Exception(Throwable cause) {
            super(cause);
        }

        Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
