package magic.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class ConstantGetter {

	public interface EnumLike {}
	
	private static final int PUBLIC_STATIC_FINAL =
			Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

	public static <T extends EnumLike> ImmutableList<T> values(Class<T> clazz) {
		Builder<T> builder = ImmutableList.builder();
		for (Field field : clazz.getFields()) {
			if ((field.getModifiers() & PUBLIC_STATIC_FINAL) == PUBLIC_STATIC_FINAL
					&& clazz.isAssignableFrom(field.getType())) {
				try {
					@SuppressWarnings("unchecked")
					T constant = (T) field.get(null);
					builder.add(constant);
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException(
							"bad constant: " + field.getName(), e);
				}
			}
		}
		return builder.build();
	}

	private ConstantGetter() {}
	
}
