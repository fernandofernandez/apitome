package org.apitome.core.model;

import java.util.Objects;

public class TypeKey<T> {

    private final String name;

    private final Class<T> type;

    protected TypeKey(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public static <Result> TypeKey<Result> of(String name, Class<Result> type) {
        return new TypeKey<>(name, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeKey<?> that = (TypeKey<?>) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
