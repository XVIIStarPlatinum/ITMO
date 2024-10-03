package Modules;

import data.MusicBandRaw;
import Commands.Command;
import Network.Response;
import Network.User;

import java.util.HashMap;

public class ConsoleApp {
    // хэшмапа команд. Ключ - имя команды; Значение - класс-оболочка команды
    public static HashMap<String, Command> commandList = new HashMap<>();
    private Command help;
    private Command info;
    private Command show;
    private Command add;
    private Command update;
    private Command removeById;
    private Command clear;
    private Command removeByIndex;
    private Command shuffle;
    private Command history;
    private Command printFieldDescendingEstablishmentDate;
    private Command groupCountingByEstablishmentDate;
    private Command filterLessThanNumberOfParticipants;

    public ConsoleApp(Command help, Command info, Command show, Command add, Command update, Command removeById,
                      Command clear, Command removeByIndex, Command shuffle, Command history, Command printFieldDescendingEstablishmentDate,
                      Command groupCountingByEstablishmentDate, Command filterLessThanNumberOfParticipants) {
        this.help = help;
        this.info = info;
        this.show = show;
        this.add = add;
        this.update = update;
        this.removeById = removeById;
        this.clear = clear;
        this.removeByIndex = removeByIndex;
        this.shuffle = shuffle;
        this.history = history;
        this.printFieldDescendingEstablishmentDate = printFieldDescendingEstablishmentDate;
        this.groupCountingByEstablishmentDate = groupCountingByEstablishmentDate;
        this.filterLessThanNumberOfParticipants = filterLessThanNumberOfParticipants;
    }

    public Response help(User user, String arguments, MusicBandRaw objectArg) {
        return help.execute(user, arguments, objectArg);
    }

    public Response info(User user, String arguments, MusicBandRaw objectArg) {
        return info.execute(user, arguments, objectArg);
    }

    public Response show(User user, String arguments, MusicBandRaw objectArg) {
        return show.execute(user, arguments, objectArg);
    }

    public Response add(User user, String arguments, MusicBandRaw objectArg) {
        return add.execute(user, arguments, objectArg);
    }

    public Response update(User user, String arguments, MusicBandRaw objectArg) {
        return update.execute(user, arguments, objectArg);
    }

    public Response removeById(User user, String arguments, MusicBandRaw objectArg) {
        return removeById.execute(user, arguments, objectArg);
    }

    public Response clear(User user, String arguments, MusicBandRaw objectArg) {
        return clear.execute(user, arguments, objectArg);
    }

    public Response removeByIndex(User user, String arguments, MusicBandRaw objectArg) {
        return removeByIndex.execute(user, arguments, objectArg);
    }

    public Response shuffle(User user, String arguments, MusicBandRaw objectArg) {
        return shuffle.execute(user, arguments, objectArg);
    }

    public Response history(User user, String arguments, MusicBandRaw objectArg) {
        return history.execute(user, arguments, objectArg);
    }

    public Response removeAllByType(User user, String arguments, MusicBandRaw objectArg) {
        return printFieldDescendingEstablishmentDate.execute(user, arguments, objectArg);
    }

    public Response countGreaterThanEnginePower(User user, String arguments, MusicBandRaw objectArg) {
        return groupCountingByEstablishmentDate.execute(user, arguments, objectArg);
    }

    public Response filterStartsWithName(User user, String arguments, MusicBandRaw objectArg) {
        return filterLessThanNumberOfParticipants.execute(user, arguments, objectArg);
    }
}
