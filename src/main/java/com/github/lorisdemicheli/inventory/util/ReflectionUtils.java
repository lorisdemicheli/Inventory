package com.github.lorisdemicheli.inventory.util;

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

	public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);

	/**
	 * Get class for current version
	 * 
	 * @param className
	 * @return Class for current version
	 */
	public static Class<?> getMVClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
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
			return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Class<?> classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
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

	public static <T> T newInstanceWithConstructor(Constructor<T> constructor, Object... parameters) {
		try {
			return constructor.newInstance(parameters);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
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
	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		try {
			List<Constructor<?>> constactor = Arrays.asList(clazz.getConstructors());
			for (Constructor<?> c : constactor) {
				if (parameterClass(c.getParameters(), parameters)) {
					return (T) c.newInstance(parameters);
				}
			}
			return null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Constructor<T> constractorValue(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstanceWithArgument(Class<T> clazz, Object... argument) {
		List<Class<?>> parameterTypes = Arrays.asList(argument).stream().map(a -> a.getClass())
				.collect(Collectors.toList());
		try {
			return constractorValue(clazz, parameterTypes.toArray(new Class<?>[parameterTypes.size()]))
					.newInstance(argument);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object callMethod(Object obj, String methodName, Object... parameters) {
		try {
			List<Method> methods = Arrays.asList(obj.getClass().getMethods()).stream()
					.filter(m -> m.getName().endsWith(methodName)).collect(Collectors.toList());
			for (Method m : methods) {
				if (parameterClass(m.getParameters(), parameters)) {
					return m.invoke(obj, parameters);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Object callStaticMethod(Class<?> clazz, String methodName, Object... parameters) {
		try {
			List<Method> methods = Arrays.asList(clazz.getMethods()).stream()
					.filter(m -> m.getName().endsWith(methodName)).collect(Collectors.toList());
			for (Method m : methods) {
				if (parameterClass(m.getParameters(), parameters)) {
					return m.invoke(null, parameters);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Object callStaticMethod(Method method, Object... parameter) {
		try {
			return method.invoke(null, parameter);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Method findStaticMethod(Class<?> clazz, String name, Class<?>... type) {
		try {
			return clazz.getMethod(name, type);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object callMethod(Object instance, Method method, Object... parameter) {
		try {
			method.setAccessible(true);
			return method.invoke(instance, parameter);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... type) {
		try {
			return clazz.getDeclaredMethod(name, type);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object fieldValue(Object obj, String fieldName) {
		try {
			Field f = findField(obj.getClass(),fieldName);
			f.setAccessible(true);
			return f.get(obj);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object staticFieldValue(Class<?> clazz,String fieldName) {
		try {
			Field f = findField(clazz, fieldName);
			f.setAccessible(true);
			return f.get(null);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFieldValue(Object obj, String fieldName, Object value) {
		try {
			Field f = findField(obj.getClass(),fieldName);
			f.setAccessible(true);
			f.set(obj, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field findField(Class<?> clazz, String fieldName) {
		Field f;
		try {
			f = clazz.getField(fieldName);
		} catch (NoSuchFieldException e) {
			try {
				f = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e1) {
				if(!clazz.getSuperclass().equals(Object.class)) {
					f = findField(clazz.getSuperclass(), fieldName);
				} else {
					throw new RuntimeException(e1);
				}
			}
		}
		return f;
	}
}
