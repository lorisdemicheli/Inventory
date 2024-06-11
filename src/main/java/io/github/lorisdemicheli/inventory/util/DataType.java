package io.github.lorisdemicheli.inventory.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataType.PrimitivePersistentDataType;

import io.github.lorisdemicheli.inventory.data.AutoPersistentDataType;


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
		for(Field field : ReflectionUtils.getAllField(DataType.class)) {
			PersistentDataType<?, ?> baseType = (PersistentDataType<?, ?>) ReflectionUtils.fieldValue(field,null);
			if (baseType.getComplexType().equals(type.getClass())) {
				return (PersistentDataType<?, T>) baseType;
			}
		}
		return new AutoPersistentDataType<T>((Class<T>)type.getClass());
	}
}