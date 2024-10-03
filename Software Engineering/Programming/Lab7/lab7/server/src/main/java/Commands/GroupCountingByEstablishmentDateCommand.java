package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;
import data.MusicBandRaw;

public class GroupCountingByEstablishmentDateCommand implements Command {
    private CommandHandler commandHandler;

    public GroupCountingByEstablishmentDateCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("countGreaterThanEnginePower", this);
    }

    @Override
    public Response execute(User user, String arguments, MusicBandRaw objectArg) {
        return commandHandler.groupCountingByEstablishmentDate(user, arguments, objectArg);
    }
}
