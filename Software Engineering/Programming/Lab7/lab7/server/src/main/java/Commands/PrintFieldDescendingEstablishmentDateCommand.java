package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;
import data.MusicBandRaw;

public class PrintFieldDescendingEstablishmentDateCommand implements Command {
    private CommandHandler commandHandler;

    public PrintFieldDescendingEstablishmentDateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("print_field_descending_establishment_date", this);
    }

    @Override
    public Response execute(User user, String arguments, MusicBandRaw objectArg) {
        return commandHandler.printFieldDescendingEstablishmentDate(user, arguments, objectArg);
    }

}
