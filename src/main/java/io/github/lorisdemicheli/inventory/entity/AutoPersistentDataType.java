package io.github.lorisdemicheli.inventory.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class AutoPersistentDataType<T> implements PersistentDataType<byte[], T>{
	
	private Class<T> clazz;
	
	public AutoPersistentDataType(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public Class<T> getComplexType(){
		return clazz;
	}

	@Override
	public byte[] toPrimitive(T complex, PersistentDataAdapterContext context) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(complex);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to transform to primitive",e);
		}
		return bos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
		ByteArrayInputStream bis = new ByteArrayInputStream(primitive);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (T) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Unable to transform from primitive",e);
		}
	}
}
