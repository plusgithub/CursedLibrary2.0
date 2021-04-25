package com.cursedplanet.cursedlibrary.lib.remain.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.cursedplanet.cursedlibrary.lib.exception.FoException;

/**
 * Integer implementation for NBTLists
 *
 * @author tr7zw
 *
 */
public class NBTIntegerList extends NBTList<Integer> {

	protected NBTIntegerList(NBTCompound owner, String name, NBTType type, Object list) {
		super(owner, name, type, list);
	}

	@Override
	protected Object asTag(Integer object) {
		try {
			final Constructor<?> con = WrapperClass.NMS_NBTTAGINT.getClazz().getDeclaredConstructor(int.class);
			con.setAccessible(true);
			return con.newInstance(object);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new FoException(e, "Error while wrapping the Object " + object + " to it's NMS object!");
		}
	}

	@Override
	public Integer get(int index) {
		try {
			final Object obj = WrapperReflection.LIST_GET.run(listObject, index);
			return Integer.parseInt(obj.toString());
		} catch (final NumberFormatException nf) {
			return 0;
		} catch (final Exception ex) {
			throw new FoException(ex);
		}
	}

}
