package com.github.paniclab.domain;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public interface Entity<T extends Serializable> {

    @SuppressWarnings("unchecked")
    static <ID extends Serializable, TYPE extends Entity<ID>> TYPE getDetached(ID id, Class<TYPE> clazz) {
        if (id == null) throw new NullPointerException("Attempt to create detached instance with id = null.");

        Constructor<TYPE> constructor;
        constructor = (Constructor<TYPE>)Arrays.stream(clazz.getDeclaredConstructors())
                                            .filter(c -> c.getGenericParameterTypes().length == 0)
                                            .findAny()
                                            .orElseThrow(() -> new InternalException("Unable to create instance of class " +
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
            throw new InternalException("Unable to create instance of class " + clazz.getCanonicalName(), e);
        }

        return entity;
    }

    T getId();

    Entity<T> getThis();

    default boolean isTransient() {
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
                                                    .orElseThrow(() -> new InternalException(
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
            throw new InternalException("Unable to create copy. Class: " + getThis().getClass().getCanonicalName() +
                    System.lineSeparator() + "Instance to copy: " + getThis(), e);
        }

        return copy;
    }


    @SuppressWarnings("unchecked")
    default Entity<T> getTransientCopy() {

        Constructor<? extends Entity<T>> constructor =
                (Constructor<? extends Entity<T>>)Arrays.stream(getThis().getClass().getDeclaredConstructors())
                        .filter(c -> c.getGenericParameterTypes().length == 0)
                        .findAny()
                        .orElseThrow(() -> new InternalException(
                                "Entity class must have constructor with no args."
                        ));

        Entity<T> copy;
        try {
            constructor.setAccessible(true);
            copy = constructor.newInstance();

            Field[] fields = getThis().getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.getName().equals("id")) {
                    continue;
                }
                f.setAccessible(true);
                f.set(copy, f.get(getThis()));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalException("Unable to create copy. Class: " + getThis().getClass().getCanonicalName() +
                    System.lineSeparator() + "Instance to copy: " + getThis(), e);
        }

        return copy;
    }


    @SuppressWarnings("unchecked")
    default Entity<T> getEmptyDetached() {
        Entity<T> instance = Entity.getDetached(getThis().getId(), getThis().getClass());
        return getThis().getClass().cast(instance);
    }

    default boolean hasSameContentExceptIdAs(Entity another) {
        int size = getEntityClass().getDeclaredFields().length;
        if (another.getClass().getDeclaredFields().length != size) return false;

        Field[] thisFields = getEntityClass().getDeclaredFields();
        for (int x = 0; x < size; x++  ) {
            Field current = thisFields[x];
            if (current.getName().equals("id")) continue;
            try {
                if (!(Objects.equals(current.get(getThis()), another.getClass().getDeclaredField(current.getName())))) {
                    return false;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new InternalException("Unable to compare two entities. Entity: " + getThis() +
                        System.lineSeparator() + "Another entity: " + another, e);
            }
        }

        return true;
    }

    default boolean hasSameContentAs(Entity another) {
        if (getThis().hasSameContentExceptIdAs(another)) {
            return getThis().getId().equals(another.getId());
        }
        return false;
    }

    class InternalException extends RuntimeException {
        public InternalException() {
        }

        InternalException(String message) {
            super(message);
        }

        InternalException(String message, Throwable cause) {
            super(message, cause);
        }

        InternalException(Throwable cause) {
            super(cause);
        }

        InternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
