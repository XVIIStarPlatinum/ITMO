/**
 * Utility package for console app.
 */
package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import consoleApp.Main;
import data.Organization;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Class for file management.
 * @author Ariguun Erkevich Bolorbold
 */
public class FileManager {
    /**
     * instance of a Gson object - library import of this jar file was painful.
     */
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor of utility class FileManager.
     *
     * @param ConsoleArg String
     */
    public FileManager(String ConsoleArg) {
        Main.CLI_ARGUMENT = ConsoleArg;
    }

    Hashtable<Integer, Organization> collection;

    /**
     * This method writes collection to a file.
     *
     * @param collection hashtable
     */
    public void writeCollection(TreeMap<Integer, Organization> collection) {
        try {
            OutputStream outputStream = new FileOutputStream(Main.CLI_ARGUMENT);
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            String a = gson.toJson(collection);
            writer.write(a);
            writer.close();
        } catch (IOException e) {
            utility.Console.printError("File cannot be opened");
        }
    }

    /**
     * This method reads collection from a file.
     * @return hashtable
     */
    public Hashtable<Integer, Organization> readCollection() {
        if (Main.CLI_ARGUMENT != null) {
            File file = new File(Main.CLI_ARGUMENT);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                Reader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                final Type collectionType = new TypeToken<Hashtable<Integer, Organization>>() {}.getType();
                final BufferedReader reader = new BufferedReader(inputStreamReader);
                collection = gson.fromJson(reader, collectionType);
                return collection;
            } catch (FileNotFoundException e) {
                utility.Console.printError("File not found");
            } catch (NoSuchElementException e) {
                utility.Console.printError("The file is emptier than the brain of the developer");
            } catch (JsonParseException | NullPointerException e) {
                utility.Console.printError("No collection detected");
            } catch (IllegalStateException e) {
                utility.Console.printError("Unknown error");
                System.exit(0);
            } catch (IOException e) {
                utility.Console.printError("Input/Output operation interrupted");
            }
        } else utility.Console.printError("JSON file not found");
        return new Hashtable<>();
    }



        /**
         * FileManager implementation of general method toString()
         * @return String
         */
        @Override
        public String toString () {
            return "FileManager (utility class for file management)";
        }
    }