package org.github.lorisdemicheli.inventory.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

public class ReflectionUtils {

	/**
	 * Get class for current version
	 * 
	 * @param className
	 * @return Class for current version
	 */
	public static Class<?> getMVClass(String className) {
		try {
			return Class.forName("net.minecraft.server."
					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Get class for current version of craftbukkit
	 * 
	 * @param className
	 * @return Class for current version
	 */
	public static Class<?> getCBVClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit."
					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static boolean parameterClass(Parameter[] parameter, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameter.length != parameters.length) {
				return false;
			}
			if (!parameter[i].getType().isInstance(parameters[i])) {
				if (parameter[i].getType().isPrimitive()) {
					if (!isValidPrimitive(parameter[i], parameters[i])) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isValidPrimitive(Parameter p, Object o) {
		if ((p.getType().equals(boolean.class) && o.getClass().equals(Boolean.class))
				|| (p.getType().equals(byte.class) && o.getClass().equals(Byte.class))
				|| (p.getType().equals(char.class) && o.getClass().equals(Character.class))
				|| (p.getType().equals(short.class) && o.getClass().equals(Short.class))
				|| (p.getType().equals(long.class) && o.getClass().equals(Long.class))
				|| (p.getType().equals(float.class) && o.getClass().equals(Float.class))
				|| (p.getType().equals(double.class) && o.getClass().equals(Double.class))
				|| (p.getType().equals(int.class) && o.getClass().equals(Integer.class))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Object... parameters)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (parameterClass(constructor.getParameters(), parameters)) {
				return (T) constructor.newInstance(parameters);
			}
		}
		return null;
	}

	public static Object callMethod(Object obj, String methodName, Object... parameters) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Method> methods = Arrays.asList(obj.getClass().getMethods()).stream()
				.filter(m -> m.getName().endsWith(methodName)).collect(Collectors.toList());
		for (Method m : methods) {
			if (parameterClass(m.getParameters(), parameters)) {
				return m.invoke(obj, parameters);
			}
		}
		return null;
	}

	public static Object callStaticMethod(Class<?> clazz, String methodName, Object... parameters)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		List<Method> methods = Arrays.asList(clazz.getMethods()).stream().filter(m -> m.getName().endsWith(methodName))
				.collect(Collectors.toList());
		for (Method m : methods) {
			if (parameterClass(m.getParameters(), parameters)) {
				return m.invoke(null, parameters);
			}
		}
		return null;
	}

	public static Object fieldValue(Object obj, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field f = obj.getClass().getField(fieldName);
		f.setAccessible(true);
		return f.get(obj);
	}
}
