package Utils;

import Exceptions.EmptyFieldException;
import Exceptions.NegativeFieldException;
import Network.User;
import data.*;
import static data.MusicGenre.*;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class MusicBandValidator {
    
    private static final DateTimeFormatterBuilder formats = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ofPattern("[uuuu-MM-dd]", Locale.ROOT));

    private static DateTimeFormatter dateTimeFormatter = formats.toFormatter();

    public static MusicBandRaw createElement(User user){
        Scanner InputScanner = PromptScan.getUserScanner();

        System.out.println("Введите имя:");
        String name = askString(InputScanner);

        System.out.println("Введите координату x:");
        float x = askX(InputScanner);

        System.out.println("Введите координату y:");
        float y = askY(InputScanner);

        Coordinates coordinates = new Coordinates(x, y);

        System.out.println("Введите число участников:");
        long numberOfParticipants = askLong(InputScanner);

        System.out.println("Введите дату основания (yyyy-mm-dd):");
        LocalDateTime establishmentDate = askLocalDateTime(InputScanner);

        System.out.print("""
                Введите один из доступных жанров музыки:
                PSYCHEDELIC_ROCK
                PSYCHEDELIC_CLOUD_RAP
                SOUL
                POP
                BRIT_POP
                """);
        MusicGenre musicGenre = askMusicGenre(InputScanner);

        System.out.println("Введите адрес студии:");
        String address = askString(InputScanner);
        Studio studio = new Studio(address);
        return new MusicBandRaw(name, coordinates, numberOfParticipants, establishmentDate, musicGenre, studio, user);
    }

    private static String askString(Scanner InputScanner) {
        while(true) {
            try {
                var name = InputScanner.nextLine();
                if (name.isBlank()){
                    throw new EmptyFieldException("Поле не может быть пустым. Введите его ещё раз: ");
                }
                return name.trim();
            } catch(EmptyFieldException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static float askX(Scanner InputScanner) {
        while(true) {
            try {
                float x = Float.parseFloat(InputScanner.nextLine());
                if(x > -584.0F) {
                    return x;
                } else {
                    throw new NumberFormatException("Неверный формат числа. Введите его повторно:");
                }
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно (x > -584):");
            }
        }
    }

    private static float askY(Scanner InputScanner) {
        while(true) {
            try {
                return Float.parseFloat(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private static long askLong(Scanner InputScanner) {
        while(true) {
            try {
                long num = Long.parseLong(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число должно быть натуральным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static LocalDateTime askLocalDateTime(Scanner InputScanner) {
        while(true) {
            try {

                LocalDateTime localDateTime = LocalDate.parse(InputScanner.nextLine(), dateTimeFormatter).atStartOfDay();
                return localDateTime;
            } catch (DateTimeParseException e) {
                System.out.println("Невозможно распарсить дату.");
            }
        }
    }

    private static MusicGenre askMusicGenre(Scanner InputScanner) {
        while (true){
            try {
                String type = InputScanner.nextLine().toUpperCase();
                MusicGenre musicGenre;
                switch (type){
                    case "PSYCHEDELIC_ROCK":
                        musicGenre = PSYCHEDELIC_ROCK;
                        break;
                    case "PSYCHEDELIC_CLOUD_RAP":
                        musicGenre = PSYCHEDELIC_CLOUD_RAP;
                        break;
                    case "SOUL":
                        musicGenre = SOUL;
                        break;
                    case "POP":
                        musicGenre = POP;
                        break;
                    case "BRIT_POP":
                        musicGenre = BRIT_POP;
                        break;
                    default:
                        throw new EmptyFieldException("Такого типа жанра музыки не существует. " +
                                "Заполните жанр корректно: ");
                }
                return musicGenre;
            } catch (EmptyFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
