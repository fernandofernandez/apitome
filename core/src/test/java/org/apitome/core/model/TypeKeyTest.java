package org.apitome.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TypeKeyTest {

    private Map<TypeKey<?>, Object> map;

    @BeforeEach
    public void setup() {
        this.map = new HashMap<>();
    }

    @Test
    public void testMapAccess() {
        TypeKey<Integer> key1 = TypeKey.of("value1", Integer.class);
        TypeKey<Integer> key2 = TypeKey.of("value2", Integer.class);
        map.put(key1, new Integer(3));
        map.put(key2, new Integer(5));

        TypeKey<Integer> test = TypeKey.of("value1", Integer.class);
        Integer result = (Integer) map.get(test);
        assertEquals(3, result.intValue());
        test = TypeKey.of("value2", Integer.class);
        result = (Integer) map.get(test);
        assertEquals(5, result.intValue());
        TypeKey<Boolean> anotherTest = TypeKey.of("value1", Boolean.class);
        assertNull(map.get(anotherTest));
    }

}
