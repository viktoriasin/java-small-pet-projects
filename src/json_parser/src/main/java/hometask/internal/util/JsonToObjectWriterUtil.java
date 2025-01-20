package hometask.internal.util;

import hometask.exceptions.JsonParserException;
import hometask.internal.handlers.AnnotationHandler;

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
        if (clazz.isPrimitive()) {
            return parsePrimitive(clazz, data);
        } else if (Number.class.isAssignableFrom(clazz)) {
            return parseWrapperNumber(clazz, data);
        } else if (clazz == String.class) {
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

    private Object parseWrapperNumber(Class<?> clazz, Object data) {
        if (Short.class.isAssignableFrom(clazz)) {
            return Short.parseShort(data.toString());
        } else if (Byte.class.isAssignableFrom(clazz)) {
            return Byte.parseByte(data.toString());
        } else if (Long.class.isAssignableFrom(clazz)) {
            return Long.parseLong(data.toString());
        } else if (Float.class.isAssignableFrom(clazz)) {
            return Float.parseFloat(data.toString());
        } else if (Double.class.isAssignableFrom(clazz)) {
            return Double.parseDouble(data.toString());
        } else if (Character.class.isAssignableFrom(clazz)) {
            return data.toString().toCharArray()[0];
        } else {
            return data;
        }
    }

    private Object parsePrimitive(Class<?> clazz, Object data) {
        if (clazz == short.class) {
            return (short) ((int) data);
        } else if (clazz == byte.class) {
            return (byte) ((int) data);
        } else if (clazz == long.class) {
            return (long) ((int) data);
        } else if (clazz == float.class) {
            return data;
        } else if (clazz == double.class) {
            return data;
        } else if (clazz == char.class) {
            return data.toString().toCharArray()[0];
        } else  {
            // clazz == boolean.class || clazz == int.class
            return data;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parseObject(Class<T> clazz, Object dataSrc) throws JsonParserException {
        if (Map.class.isAssignableFrom(dataSrc.getClass())) { // Если нам передали по факту int, который положили в object, тогда во внутреннем представлении он будет в виде значении а не мапы
            Map<String, Object> data = (Map<String, Object>) dataSrc;
            Field[] fields = clazz.getDeclaredFields();
            try {
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
                        if (value instanceof ObjectArrayHolder arrayHolder) {
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
            } catch (Exception e) {
                throw new JsonParserException(e.toString());
            }
        } else {
            return (T) dataSrc;
        }
    }


    private <T> void setPrimitiveArrayFieldField(Field field, Object instance, ObjectArrayHolder arrayHolder, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // TODO: разделить setter и создание массива
        if (Modifier.isPublic(field.getModifiers())) {
            Method setterMethod = null;
            String fieldName = field.getName();
            PrimitiveArrayHolder primitiveArray = getPrimitiveArray(arrayHolder);
            setterMethod = clazz.getMethod(getMethodName(fieldName), field.getType());
            setterMethod.invoke(instance, primitiveArray.getArray()); // TODO: check fot nulls in getArray?
        }
    } // todo добавить случай, если private поля

    private static String getMethodName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public <T> PrimitiveArrayHolder getPrimitiveArray(ObjectArrayHolder arrayHolder) {
        PrimitiveArrayHolder primitiveArrayHolder = new PrimitiveArrayHolder();
        if (arrayHolder.primitiveType == int.class) {
            int[] result = new int[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (int) arrayHolder.array[i];
            }
            primitiveArrayHolder.setIntArray(result);
        } else if (arrayHolder.primitiveType == byte.class) {
            byte[] result = new byte[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (byte) arrayHolder.array[i];
            }
            primitiveArrayHolder.setByteArray(result);
        } else if (arrayHolder.primitiveType == long.class) {
            long[] result = new long[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (long) arrayHolder.array[i];
            }
            primitiveArrayHolder.setLongArray(result);
        } else if (arrayHolder.primitiveType == double.class) {
            double[] result = new double[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (double) arrayHolder.array[i];
            }
            primitiveArrayHolder.setDoubleArray(result);
        } else if (arrayHolder.primitiveType == char.class) {
            char[] result = new char[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (char) arrayHolder.array[i];
            }
            primitiveArrayHolder.setCharArray(result);
        } else if (arrayHolder.primitiveType == short.class) {
            short[] result = new short[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (short) arrayHolder.array[i];
            }
            primitiveArrayHolder.setShortArray(result);
        } else if (arrayHolder.primitiveType == boolean.class) {
            boolean[] result = new boolean[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (boolean) arrayHolder.array[i];
            }
            primitiveArrayHolder.setBooleanArray(result);
        } else if (arrayHolder.primitiveType == float.class) {
            float[] result = new float[arrayHolder.array.length];
            for (int i = 0; i < arrayHolder.array.length; i++) {
                result[i] = (float) arrayHolder.array[i];
            }
            primitiveArrayHolder.setFloatArray(result);
        }
        return primitiveArrayHolder;
    }


    public <T> T parseRecord(Class<T> clazz, Map<String, Object> data) throws JsonParserException {
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
                try {
                    if (comp.getGenericType() instanceof ParameterizedType) {
                        value = parse(fieldType, fieldValue, ((ParameterizedType) comp.getGenericType()).getActualTypeArguments());
                    } else {
                        value = parse(fieldType, fieldValue, null);
                    }
                } catch (Exception e) {
                    throw new JsonParserException(e.toString());
                }

                if (value instanceof ObjectArrayHolder arrayHolder) {
                    PrimitiveArrayHolder primitiveArrayHolder = getPrimitiveArray(arrayHolder);
                    fieldValues[i] = primitiveArrayHolder.getArray();
                } else {
                    fieldValues[i] = value;
                }
            }
        }
        try {
            return (T) canonicalConstructorOfRecord(clazz).newInstance(fieldValues);
        } catch (Exception e) {
            throw new JsonParserException(e.toString());
        }
    }

    public Object parseArray(Class<?> arrayElementType, Object[] arrayData, ParameterizedType genericType) throws Exception {
        ObjectArrayHolder arrayHolder = new ObjectArrayHolder();
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
