package com.github.lorisdemicheli.inventory.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataType.PrimitivePersistentDataType;

import com.github.lorisdemicheli.inventory.entity.AutoPersistentDataType;

public class DataType {

	public static final PersistentDataType<Byte, Byte> BYTE = PrimitivePersistentDataType.BYTE;
	public static final PersistentDataType<Short, Short> SHORT = PrimitivePersistentDataType.SHORT;
	public static final PersistentDataType<Integer, Integer> INTEGER = PrimitivePersistentDataType.INTEGER;
	public static final PersistentDataType<Long, Long> LONG = PrimitivePersistentDataType.LONG;
	public static final PersistentDataType<Float, Float> FLOAT = PrimitivePersistentDataType.FLOAT;
	public static final PersistentDataType<Double, Double> DOUBLE = PrimitivePersistentDataType.DOUBLE;

	public static final PersistentDataType<String, String> STRING = PrimitivePersistentDataType.STRING;

	public static final PersistentDataType<byte[], byte[]> BYTE_ARRAY = PrimitivePersistentDataType.BYTE_ARRAY;
	public static final PersistentDataType<int[], int[]> INTEGER_ARRAY = PrimitivePersistentDataType.INTEGER_ARRAY;
	public static final PersistentDataType<long[], long[]> LONG_ARRAY = PrimitivePersistentDataType.LONG_ARRAY;

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> PersistentDataType<?, T> getType(T type) {
		for (Field f : DataType.class.getFields()) {
			if (Modifier.isPublic(f.getModifiers())) {
				try {
					PersistentDataType<?, ?> typeint = (PersistentDataType<?, ?>) f.get(null);
					if (typeint.getComplexType().getTypeName().equals(type.getClass().getCanonicalName())) {
						return (PersistentDataType<?, T>) typeint;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {	}
			}
		}
		return new AutoPersistentDataType<T>((Class<T>)type.getClass());
	}
}