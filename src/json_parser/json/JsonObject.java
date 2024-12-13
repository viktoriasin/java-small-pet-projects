package json_parser.json;

import json_parser.exceptions.JsonParserException;

import java.lang.reflect.InvocationTargetException;

public interface JsonObject {
    // Переводит внутреннее состояние в новый объект `targetType`. В качестве `type` может быть пользовательские класс или record.
    // Generic классы, коллекции или массивы передаваться не будут. Кидает исключение, если json не соответствует типу.
    // Этот метод можно вызывать несколько раз подряд, получая новые объекты
    <T> T to(Class<T> targetType) throws JsonParserException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
