package Modules;

import data.*;
import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;
import Network.Response;
import Network.User;

import java.util.LinkedList;

public class CommandHandler {
    private CollectionService collectionService;
    private static LinkedList<String> commandHistory = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
    }

    public Response help(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            String message =
                    """
                            Список доступных команд:
                            ================================================================================================
                            help - справка по командам
                            info - вывод данных о коллекции (тип, дата инициализации, количество элементов)
                            show - вывести все элементы коллекции
                            add <el> - добавить элемент в коллекцию
                            update <id> <el> - обновить id элемента на заданный
                            remove_by_id <id> - удалить элемент по id
                            clear - очистить всю коллекцию
                            executeScript <path> - исполнить скрипт
                            exit - завершить работу клиентского приложения
                            remove_by_index <index> - удалить из коллекции по индексу списка
                            shuffle - перемешать элементы коллекции в случайном порядке
                            history - вывести последние 10 команд
                            group_counting_by_establishment_date - сгруппировать элементы коллекции по значению поля establishmentDate, вывести количество элементов в каждой группе
                            filter_less_than_number_of_participants <numberOfParticipants> - вывести все элементы, значение поля numberOfParticipants которых меньше заданного
                            print_field_descending_establishment_date : вывести значения поля establishmentDate всех элементов в порядке убывания
                            ================================================================================================
                            """;
            return new Response(message, "");
        }
    }

    public Response info(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var message = collectionService.info();
            return new Response(message, "");
        }
    }

    public synchronized Response show(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var collection = collectionService.show();

            if (collection.isEmpty()) {
                return new Response("В коллекции пока нету ни одного элемента", "");
            } else {
                return new Response("Коллекция успешно распечатана", collection.toString());
            }
        }
    }

    public synchronized Response add(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            LinkedList<MusicBand> collection;
            try {
                collection = collectionService.add(objArgument);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("Элемент успешно добавлен", collection.toString());
        }
    }

    public synchronized Response update(User user, String strArgument, MusicBandRaw objArgument) {
        if (strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            try {
                int current_id = Integer.parseInt(strArgument);
                if (current_id > 0) {
                    if (CollectionService.collection.stream().anyMatch(musicBand -> musicBand.getId() == current_id)) {
                        if (CollectionService.collection.stream().anyMatch(musicBand-> musicBand.getId() == current_id
                                && musicBand.getOwner().equals(user.getUsername()))) {
                            var collection = collectionService.update(user, current_id, objArgument);
                            return new Response("Элемент c id " + current_id + " успешно обновлён", collection.toString());
                        }
                        return new Response("Вы не можете изменить этот объект", "");
                    }
                    return new Response("Элемента с таким id не существует", "");
                } else {
                    return new Response("id не может быть отрицательным", "");
                }
            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response removeById(User user, String strArgument, MusicBandRaw objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            try {
                int id = Integer.parseInt(strArgument);

                if (id > 0) {
                    if (CollectionService.collection.stream().anyMatch(musicBand -> musicBand.getId() == id)) {
                        if (CollectionService.collection.stream().anyMatch(musicBand -> musicBand.getId() == id
                                && musicBand.getOwner().equals(user.getUsername()))) {

                            var collection = collectionService.removeById(user, id);
                            return new Response("Элемент с id " + id + " успешно удалён", collection.toString());
                        }
                        return new Response("Вы не можете удалить этот объект", "");
                    }
                    return new Response("Элемента с таким id не существует", "");
                }
                return new Response("id не может быть отрицательным", "");

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response clear(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            LinkedList<MusicBand> collection;
            try {
                collection = collectionService.clear(user);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("Коллекция успешно очищена.", collection.toString());
        }
    }

    public synchronized Response shuffle(User user, String strArgument, MusicBandRaw objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды.", "");
        } else {
            var collection = collectionService.shuffle();
            return new Response("Элементы успешно разбросаны повсюду.", collection.toString());
        }
    }
    public Response history(User user, String strArgument, MusicBandRaw objArgument) {
        StringBuilder historyList = new StringBuilder();
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            for (String command : commandHistory) {
                historyList.append(command).append("\n");
            }
        }
        return new Response("Последние 10 команд, введённые пользователем: \n" + historyList, "");
    }

    public synchronized Response removeByIndex(User user, String strArgument, MusicBandRaw objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            try {
                int index = Integer.parseInt(strArgument);

                if (index >= 0) {
                    if (CollectionService.collection.stream().anyMatch(musicBand -> musicBand.getId() == collectionService.getByIndex(index).getId())) {
                        if (CollectionService.collection.stream().anyMatch(musicBand -> musicBand.getId() == collectionService.getByIndex(index).getId()
                                && musicBand.getOwner().equals(user.getUsername()))) {

                            var collection = collectionService.removeByIndex(user, index);
                            return new Response("Элемент с индексом " + index + " успешно удалён.", collection.toString());
                        }
                        return new Response("Вы не можете удалить этот объект", "");
                    }
                    return new Response("Элемента с таким индексом не существует", "");
                }
                return new Response("Индекс не может быть отрицательным", "");

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response groupCountingByEstablishmentDate(User user, String strArgument, MusicBandRaw objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                var dates = collectionService.groupCountingByEstablishmentDate();
                if(!dates.isEmpty()) {
                    return new Response("Коллекция группированный по дату основания:\n" + dates, "");
                } else {
                    return new Response("В списке нечего группировать.", "");
                }
            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            }
        }
    }

    public synchronized Response printFieldDescendingEstablishmentDate(User user, String strArgument, MusicBandRaw objArgument) {
        if(!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            var collection = collectionService.printFieldDescendingEstablishmentDate();
            if (collection.isEmpty()) {
                return new Response("В списке нечего ранжировать.", "");
            } else {
                return new Response(collection.toString(), "");
            }
        }
    }
    public synchronized Response filterLessThanNumberOfParticipants(User user, String strArgument, MusicBandRaw objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                var collection = collectionService.filterLessThanNumberOfParticipants(Long.valueOf(strArgument));
                return new Response("Коллекция отсортирована по именам ТС", collection.toString());
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized static void addCommand(String command) {
        if (commandHistory.size() == 10) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}
