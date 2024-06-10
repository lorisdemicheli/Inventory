package io.github.lorisdemicheli.inventory.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

public class ReflectionUtils {
	
	
	/*
	 * 
	 * GENERAL
	 * 
	 * 
	 */
	private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
	static {
		WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(9);
	    WRAPPER_TYPE_MAP.put(Integer.class, int.class);
	    WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
	    WRAPPER_TYPE_MAP.put(Character.class, char.class);
	    WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
	    WRAPPER_TYPE_MAP.put(Double.class, double.class);
	    WRAPPER_TYPE_MAP.put(Float.class, float.class);
	    WRAPPER_TYPE_MAP.put(Long.class, long.class);
	    WRAPPER_TYPE_MAP.put(Short.class, short.class);
	    WRAPPER_TYPE_MAP.put(Void.class, void.class);
	}
	
	private static boolean equalsParameter(Parameter[] parameter, Class<?>[] inputParameter) {
		if(parameter.length != inputParameter.length) {
			return false;
		}
		for(int i = 0; i < parameter.length; i++) {
			Parameter par = parameter[i];
			Class<?> classPar = inputParameter[i];
			if(par.getType().isPrimitive()) {
				classPar = WRAPPER_TYPE_MAP.get(classPar);
			}
			if(classPar == null || !par.getType().isAssignableFrom(classPar)) {
				return false;
			}
		}
		return true;
	}
	
	private static Class<?>[] convertToClass(Object...objects){
		List<Class<?>> parametersClass = Arrays.asList(objects)
				.stream()
				.map(p -> p.getClass())
				.collect(Collectors.toList());
		return parametersClass.toArray(new Class[parametersClass.size()]);
	}
	
	/*
	 * 
	 * FIELD 
	 * 
	 */
	private static Field findField(Class<?> clazz, String fieldName) {
		Field field;
		try {
			field = clazz.getField(fieldName);
		} catch (NoSuchFieldException e) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e1) {
				if(!clazz.getSuperclass().equals(Object.class)) {
					field = findField(clazz.getSuperclass(), fieldName);
				} else {
					throw new RuntimeException(e1);
				}
			}
		}
		return field;
	}
	
	public static Object getFieldValue(Object obj, String fieldName) {
		try {
			Field field = findField(obj.getClass(),fieldName);
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.get(obj);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setFieldValue(Object obj, String fieldName, Object value) {
		try {
			Field field = findField(obj.getClass(),fieldName);
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(obj, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object getStaticFieldValue(Class<?> clazz,String fieldName) {
		try {
			Field field = findField(clazz, fieldName);
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.get(null);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setStaticFieldValue(Class<?> clazz,String fieldName,Object value) {
		try {
			Field field = findField(clazz, fieldName);
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(null, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
	
 	public static List<Field> getAllField(Class<?> clazz) {
 		List<Field> fields = new ArrayList<>();
 		fields.addAll(Arrays.asList(clazz.getFields()));
 		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
 		return fields;
 	}
 	
 	public static Object fieldValue(Field field,Object instance) {
 		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
 	}
	
	/*
	 * 
	 * METHOD
	 * 
	 */	
	private static List<Method> getAllMethod(Class<?> clazz,List<Method> methods){
		if(methods == null) {
			methods = new ArrayList<>();
		}
		methods.addAll(Arrays.asList(clazz.getMethods()));
		methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		if(!clazz.isInterface() && !clazz.getSuperclass().equals(Object.class)) {
			methods.addAll(getAllMethod(clazz.getSuperclass(),methods));
		} 
		return methods;
	}
	
	private static Method getMethodParameters(Class<?> clazz,String methodName,Object...parameters) {
		List<Method> methods = getAllMethod(clazz,null);
		methods = methods.stream()
				.filter(m->m.getName().equals(methodName))
				.collect(Collectors.toList());
		for(Method method : methods) {
			if(equalsParameter(method.getParameters(),convertToClass(parameters))) {
				return method;
			}
		}
		throw new RuntimeException("No such method");
	}
	
	public static Object callMethod(Object instance,String methodName,Object...parameters) {
		Method method = getMethodParameters(instance.getClass(),methodName,parameters);
		if(!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			return method.invoke(instance, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}	
	}
	
	public static Object callStaticMethod(Class<?> clazz,String methodName,Object...parameters) {
		Method method = getMethodParameters(clazz,methodName,parameters);
		if(!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			return method.invoke(null, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}	
	}
	
	/*
	 * 
	 * CONSTRUCTOR
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> getConstructor(Class<T> clazz,Object...parameters){
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if(equalsParameter(constructor.getParameters(), convertToClass(parameters))) {
				return (Constructor<T>) constructor;
			}
		}
		throw new RuntimeException("No such constructor");
	}
	
	
	public static <T> T newInstance(Class<T> clazz,Object...parameters) {
		Constructor<T> constructor = getConstructor(clazz, parameters);
		try {
			return constructor.newInstance(parameters);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 
	 * MINECRAFT
	 * 
	 */
	
	public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	
	public static Class<?> getServerVersionClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Class<?> getCraftBukkitVersionClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
