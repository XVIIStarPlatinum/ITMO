package server.ru.itmo.se.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import common.ru.itmo.se.data.MusicBand;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Utility class used for I/O operations with a file. Uses reflection and Gson for serializing the collection.
 */
public class FileManager {
    /**
     * This field holds the name of the file that this class works with.
     */
    private final String fileName;
    /**
     * This field holds a Gson instance, via which serialization/deserialization of a collection occurs.
     * It converts a collection into a readable format and works with LocalDateTime and null types.
     */
    private Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls().create();
    /**
     * This field stores the collection that is going to be serialized/deserialized.
     */
    private LinkedList<MusicBand> collection = new LinkedList<>();

    /**
     * Constructs a FileManager with the specified file name.
     *
     * @param fileName file name passed from the console argument.
     */
    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    /**
     * This method serializes the collection and writes it into a file.
     *
     * @param collection the collection from the application.
     */
    void writeCollection(Collection<?> collection) {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
            String json = gson.toJson(collection);
            writer.write(json);
            writer.close();
        } catch (FileNotFoundException e) {
            ResponseAppender.appendError("The file was not found.");
        } catch (IOException e) {
            ResponseAppender.appendError("File cannot be opened.");
        }
    }

    /**
     * This method reads the file's content and deserializes it into the app.
     *
     * @return deserialized collection.
     */
    LinkedList<MusicBand> readCollection() {
        if (fileName != null) {
            try (FileReader fileReader = new FileReader(fileName, StandardCharsets.UTF_8)) {
                final Type collectionType = new TypeToken<LinkedList<MusicBand>>() {}.getType();
                collection = gson.fromJson(fileReader, collectionType);
                return collection;
            } catch (FileNotFoundException e) {
                ResponseAppender.appendError("File not found.");
            } catch (NoSuchElementException e) {
                ResponseAppender.appendError("The file is empty.");
            } catch (JsonParseException | NullPointerException e) {
                ResponseAppender.appendError("No collection detected.");
            } catch (IllegalStateException e) {
                ResponseAppender.appendError("Unknown error. Stopping the session...");
                System.exit(0);
            } catch (IOException e) {
                ResponseAppender.appendError("I/O operation interrupted.");
            }
        } else {
            ResponseAppender.appendError("JSON file not found.");
        }
        return new LinkedList<>();
    }

    /**
     * This method is a custom implementation of the toString() method in FileManager.
     * @return information about this class.
     */
    @Override
    public String toString() {
        return "FileManager (utility class for file management)";
    }
}
