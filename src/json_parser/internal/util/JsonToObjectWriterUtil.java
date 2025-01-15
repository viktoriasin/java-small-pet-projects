package json_parser.internal.util;

import json_parser.internal.handlers.AnnotationHandler;

import java.lang.reflect.*;
import java.util.*;

public class JsonToObjectWriterUtil {

    private final Map<Class<?>, Class<?>> primitiveToWrapperClass = new HashMap<>();

    public JsonToObjectWriterUtil() {
        primitiveToWrapperClass.put(int.class, Integer.class);
        primitiveToWrapperClass.put(char.class, Character.class);
        primitiveToWrapperClass.put(boolean.class, Boolean.class);
        primitiveToWrapperClass.put(byte.class, Byte.class);
        primitiveToWrapperClass.put(long.class, Long.class);
        primitiveToWrapperClass.put(float.class, Float.class);
        primitiveToWrapperClass.put(double.class, Double.class);
        primitiveToWrapperClass.put(void.class, Void.class);
        primitiveToWrapperClass.put(short.class, Short.class);
    }

    @SuppressWarnings("unchecked")
    public Object parse(Class<?> clazz, Object data, Type[] genericTypes) throws Exception {
        if (clazz == short.class || Short.class.isAssignableFrom(clazz)) {
            return (short) ((int) data);
        } else if (clazz == byte.class || Byte.class.isAssignableFrom(clazz)) {
            return (byte) ((int) data);
        } else if (clazz == long.class || Long.class.isAssignableFrom(clazz)) {
            return (long) ((int) data);
        } else if (clazz == float.class || Float.class.isAssignableFrom(clazz)) {
            return (float) ((int) data);
        } else if (clazz == double.class || Double.class.isAssignableFrom(clazz)) {
            return (double) ((int) data);
        } else if (clazz == char.class || Character.class.isAssignableFrom(clazz)) {
            return data.toString().toCharArray()[0];
        } else if (clazz == Boolean.class || clazz == String.class
                || clazz == boolean.class
                || clazz == int.class || Integer.class.isAssignableFrom(clazz)
        ) {
            return data;
        } else if (clazz.isArray()) {
            return parseArray(clazz.getComponentType(), (Object[]) data, null); // по условию задания внутри массивов без дженериков
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return parseCollection(clazz, (Object[]) data, genericTypes);
        } else if (Map.class.isAssignableFrom(clazz)) {
            return parseMap(clazz, (Map<Object, Object>) data, genericTypes);
        } else if (clazz.isRecord()) {
            return parseRecord(clazz, (Map<String, Object>) data);
        } else {
            return parseObject(clazz, data);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parseObject(Class<T> clazz, Object dataSrc) throws Exception {
        if (Map.class.isAssignableFrom(dataSrc.getClass())) { // Если нам передали по факту int, который положили в object, тогда во внутреннем представлении он будет в виде значении а не мапы
            Map<String, Object> data = (Map<String, Object>) dataSrc;
            Field[] fields = clazz.getDeclaredFields();
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : fields) {
                String fieldJsonName = AnnotationHandler.resolveFieldName(field);
                if (data.containsKey(fieldJsonName) && !AnnotationHandler.isFieldIgnored(field)) {
                    Class<?> fieldType = field.getType();

                    Object fieldValue = data.get(fieldJsonName);
                    Type genericType = field.getGenericType();
                    Object value;
                    if (genericType instanceof ParameterizedType) {
                        value = parse(fieldType, fieldValue, ((ParameterizedType) genericType).getActualTypeArguments());
                    } else {
                        value = parse(fieldType, fieldValue, null);
                    }
                    if (value instanceof ArrayHolder arrayHolder) {
                        if (arrayHolder.isPrimitive) {
                            setPrimitiveArrayFieldField(field, instance, arrayHolder, clazz);
                        } else {
                            setObjectField(field, instance, arrayHolder.array, clazz);
                        }
                    } else {
                        setObjectField(field, instance, value, clazz);
                    }
                } // TODO добавить обработку случаев, если объекта по имени нет в мапе и ессли поле помечено аннотацией ignored
            }
            return (T) instance;
        } else {
            return (T) dataSrc;
        }
    }

    private <T> void setPrimitiveArrayFieldField(Field field, Object instance, ArrayHolder arrayHolder, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (Modifier.isPublic(field.getModifiers())) {
            Method setterMethod = null;
            String fieldName = field.getName();
            if (arrayHolder.primitiveType == int.class) {
                int[] result = new int[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (int) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == byte.class) {
                byte[] result = new byte[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (byte) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == long.class) {
                long[] result = new long[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (long) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == double.class) {
                double[] result = new double[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (double) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == char.class) {
                char[] result = new char[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (char) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == short.class) {
                short[] result = new short[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (short) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == boolean.class) {
                boolean[] result = new boolean[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (boolean) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            } else if (arrayHolder.primitiveType == float.class) {
                float[] result = new float[arrayHolder.array.length];
                for (int i = 0; i < arrayHolder.array.length; i++) {
                    result[i] = (float) arrayHolder.array[i];
                }
                setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
                setterMethod.invoke(instance, result);
            }
        }
    } // todo добавить случай, если private поля

    private static String getMethodName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    public <T> T parseRecord(Class<T> clazz, Map<String, Object> data) throws Exception {
        RecordComponent[] components = clazz.getRecordComponents();
        Object[] fieldValues = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            String fieldJsonName = AnnotationHandler.resolveFieldName(comp);
            String recordComponentName = comp.getName();
            if (data.containsKey(fieldJsonName) && !AnnotationHandler.isFieldIgnored(comp)) {
                Class<?> fieldType = comp.getType();
                Object fieldValue = data.get(fieldJsonName);
                Object value;
                if (comp.getGenericType() instanceof ParameterizedType) {
                    value = parse(fieldType, fieldValue, ((ParameterizedType) comp.getGenericType()).getActualTypeArguments());
                } else {
                    value = parse(fieldType, fieldValue, null);
                }
                fieldValues[i] = value;
            }
        }
        return (T) canonicalConstructorOfRecord(clazz).newInstance(fieldValues);
    }

    public Object parseArray(Class<?> arrayElementType, Object[] arrayData, ParameterizedType genericType) throws Exception {
        ArrayHolder arrayHolder = new ArrayHolder();
        Class<?> wrapperClass = null;
        if (arrayElementType.isPrimitive()) {
            wrapperClass = primitiveToWrapperClass.get(arrayElementType);
            arrayHolder.isPrimitive = true;
            arrayHolder.primitiveType = arrayElementType;
        } else {
            wrapperClass = arrayElementType;
        }
        Object[] extractedObject = (Object[]) Array.newInstance(wrapperClass, arrayData.length);
        for (int i = 0; i < arrayData.length; i++) {
            extractedObject[i] = parse(arrayElementType, arrayData[i], null); // по условию задания внутри массивов без дженериков
        }
        arrayHolder.array = extractedObject;
        return arrayHolder;
    }


    public Object parseMap(Class<?> clazz, Map<Object, Object> data, Type[] genericTypes) throws Exception {
        Map<Object, Object> map = new HashMap<>(); // для упрощения берем базовые реализации. В идеале - надо проверять на конкретный тип
        for (Map.Entry<Object, Object> o : data.entrySet()) {
            Object key = parse((Class<?>) genericTypes[0], o.getKey(), null); // по условию задания внутри дженериков без дженериков
            Object value = parse((Class<?>) genericTypes[1], o.getValue(), null); // по условию задания внутри дженериков без дженериков
            map.put(key, value);
        }
        return map;
    }

    public Object parseCollection(Class<?> clazz, Object[] data, Type[] genericTypes) throws Exception {
        Collection<Object> collection;
        if (List.class.isAssignableFrom(clazz)) {
            collection = new ArrayList<>(); // для упрощения берем базовые реализации. В идеале - надо проверять на конкретный тип
        } else if (Set.class.isAssignableFrom(clazz)) {
            collection = new HashSet<>();
        } else {
            collection = new ArrayDeque<>();
        }
        for (Object collectionMember : data) {
            collection.add(parse((Class<?>) genericTypes[0], collectionMember, null)); // по условию задания внутри дженериков без дженериков
        }
        return collection;
    }

    public void setObjectField(Field field, Object instance, Object value, Class<?> clazz) throws Exception {
        if (!Modifier.isPublic(field.getModifiers())) {
            Method setterMethod = null;
            String fieldName = field.getName();
            setterMethod = clazz.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
            setterMethod.invoke(instance, value);
        } else {
            field.set(instance, value);
        }
    }

    public static <T> Constructor<T> canonicalConstructorOfRecord(Class<T> recordClass)
            throws NoSuchMethodException, SecurityException {
        Class<?>[] componentTypes = Arrays.stream(recordClass.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);
        return recordClass.getDeclaredConstructor(componentTypes);
    }
}
