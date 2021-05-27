package org.github.lorisdemicheli.inventory.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public abstract class EntitySerializable<C extends Serializable>
		implements PersistentDataType<byte[], C>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntitySerializable() {
	}

	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public abstract Class<C> getComplexType();

	@Override
	public byte[] toPrimitive(C complex, PersistentDataAdapterContext context) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(complex);
			out.close();
		} catch (IOException e) {
		}
		return bos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public C fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
		ByteArrayInputStream bis = new ByteArrayInputStream(primitive);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (C) in.readObject();
		} catch (IOException | ClassNotFoundException ex) {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public PersistentDataType<byte[], C> getPersistentDataType() {
		try {
			return (PersistentDataType<byte[], C>) getComplexType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
}
