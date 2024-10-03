package server.ru.itmo.se.utility;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Supporting utility class via which Gson can work with LocalDateTime data type.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    /**
     * This field holds the format with which a LocalDateTime object will be parsed.
     */
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ROOT);

    /**
     * This method is a custom implementation of a deserializing method for LocalDateTime.
     *
     * @param jsonElement                JSON element which contains a LocalDateTime object.
     * @param type                       which the element will be deserialized to.
     * @param jsonDeserializationContext context for deserialization that is passed to a custom deserialize implementation.
     * @return deserialized LocalDateTime object.
     */
    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return LocalDate.parse(jsonElement.getAsString(), dateTimeFormatter).atStartOfDay();
    }

    /**
     * This method is a custom implementation of a serializing method for LocalDateTime.
     * @param localDateTime            LocalDateTime object that's going to be serialized.
     * @param type                     which the element will be serialized to.
     * @param jsonSerializationContext context for deserialization that is passed to a custom serialize implementation.
     * @return serialized JSON element.
     */
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTimeFormatter.format(localDateTime));
    }
}
