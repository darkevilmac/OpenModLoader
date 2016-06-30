package xyz.openmodloader.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A modified version of the DynamicEnumTest posted by badtrash on niceideas.ch
 * http://niceideas.ch/roller2/badtrash/resource/DynamicEnumTest.java
 * 
 * Changes include updating the code to run in newer dev environments, and to
 * have extensive documentation.
 */
public class EnumUtils {

    /**
     * Instance of Sun's reflection factory. Used to invoke the new field and
     * constructor accessor.
     */
    private static Object reflectionFactory = null;

    /**
     * Access to sun.reflect.ReflectionFactory#newConstructorAccessor
     */
    private static Method newConstructorAccessor = null;

    /**
     * Access to sun.reflect.ConstructorAccessor#newInstance
     */
    private static Method newInstance = null;

    /**
     * Access to sun.reflect.ReflectionFactory#newFieldAccessor
     */
    private static Method newFieldAccessor = null;

    /**
     * Access to sun.reflect.FieldAccessor#set
     */
    private static Method fieldAccessorSet = null;

    /**
     * Sets the value of a constant field to a new one.
     * 
     * @param field The field to set the value of.
     * @param target The instance of the object to set the field on. Can be null
     *        to specify a static field.
     * @param value The value to set for the field.
     */
    private static void setConstantValue(Field field, Object target, Object value) throws Exception {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
        fieldAccessorSet.invoke(fieldAccessor, target, value);
    }

    /**
     * Sets the value of a constant field to null.
     * 
     * @param clazz The class to set the field in.
     * @param fieldName The name of the field to set to null.
     */
    private static void setNull(Class<?> clazz, String fieldName) throws Exception {
        for (Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                field.setAccessible(true);
                setConstantValue(field, clazz, null);
                break;
            }
        }
    }

    /**
     * Wipes the constants cache from an enum. This sets the
     * enumConstantDirectory and enumConstants (IBM) field to null.
     */
    private static void wipeConstantsCache(Class<?> enumClass) throws Exception {
        setNull(enumClass, "enumConstantDirectory");
        setNull(enumClass, "enumConstants");
    }

    /**
     * Gets access to the constructor of an Enum class.
     * 
     * @param enumClass The enum class to get constructor access for.
     * @param additionalParameterTypes An array of classes that represent the
     *        additional parameters in the constructor.
     * @return Access to the constructor of the Enum class.
     */
    private static Object getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
    }

    /**
     * Creates a constant field in an enum class.
     * 
     * @param enumClass The class to create the new field in.
     * @param enumName The name of the enum field to create. Should be all upper
     *        cased and unique.
     * @param ordinal The position of the new enum field.
     * @param additionalTypes An array of classes that represent the additional
     *        parameters in the constructor.
     * @param additionalValues The values for every parameter in the enum
     *        constructor.
     * @return An object that represents the newly created enum field.
     */
    private static <T extends Enum<?>> T makeEnum(Class<T> enumClass, String enumName, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
        Object[] params = new Object[additionalValues.length + 2];
        params[0] = enumName;
        params[1] = ordinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        return enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), new Object[] { params }));
    }

    /**
     * Adds a new constant field to an Enum.
     * 
     * @param enumClass The class to add the new field to.
     * @param enumName The name of the new field. Should be all upper cased and
     *        unique.
     * @param paramTypes An array of classes that represents the additional
     *        parameters in the constructor.
     * @param paramValues The values for every parameter in the enum
     *        constructor.
     * @return An object that represents the newly created enum field.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T addEnum(Class<T> enumClass, String enumName, final Class<?>[] paramTypes, Object[] paramValues) {
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new RuntimeException("Class" + enumClass.getName() + " is not a valid Enum!");
        }

        Field valuesField = null;
        Field[] fields = enumClass.getDeclaredFields();

        for (Field field : fields) {
            final String name = field.getName();
            if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) {
                valuesField = field;
                break;
            }
        }

        if (valuesField == null) {
            throw new RuntimeException("Could not find the $VALUES or ENUM$VALUES field in " + enumClass.getName() + ". Things will not work correctly!");
        }

        valuesField.setAccessible(true);

        try {
            T[] previousValues = (T[]) valuesField.get(enumClass);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            T newValue = makeEnum(enumClass, enumName, values.size(), paramTypes, paramValues);
            values.add(newValue);
            setConstantValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumClass, 0)));
            wipeConstantsCache(enumClass);

            return newValue;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static {
        try {
            final Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
            reflectionFactory = getReflectionFactory.invoke(null);
            newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
            newInstance = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
            newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
            fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("EnumUtils could not be properly initialized. Things will not work as expected!", e);
        }
    }
}