package hometask.json;

import hometask.internal.JsonObjectImpl;
import hometask.internal.JsonParserImpl;

public interface JsonParser {
    // Сериализует объект в json-строку без пробелов. будут передаваться объекты классов только с допустимыми типами полей.
    // Если в каком-то поле null, то его нужно игнорировать при выводе
    static String writeToString(Object value) {
        // TODO
        return JsonParserImpl.writeToString(value);
    }

    // Парсит json-строку во внутреннее представление, которое может быть переведено в конечное следующим методом.
    // Считаем, что json строка всегда синтаксически корректна
    static JsonObject parse(String json) {
        return new JsonObjectImpl(json);
    }
}
