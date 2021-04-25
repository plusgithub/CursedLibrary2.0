package com.cursedplanet.cursedlibrary.lib.remain.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.cursedplanet.cursedlibrary.lib.exception.FoException;

/**
 * String implementation for NBTLists
 *
 * @author tr7zw
 *
 */
public class NBTStringList extends NBTList<String> {

	protected NBTStringList(NBTCompound owner, String name, NBTType type, Object list) {
		super(owner, name, type, list);
	}

	@Override
	public String get(int index) {
		try {
			return (String) WrapperReflection.LIST_GET_STRING.run(listObject, index);
		} catch (final Exception ex) {
			throw new FoException(ex);
		}
	}

	@Override
	protected Object asTag(String object) {
		try {
			final Constructor<?> con = WrapperClass.NMS_NBTTAGSTRING.getClazz().getDeclaredConstructor(String.class);
			con.setAccessible(true);
			return con.newInstance(object);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new FoException(e, "Error while wrapping the Object " + object + " to it's NMS object!");
		}
	}

}
