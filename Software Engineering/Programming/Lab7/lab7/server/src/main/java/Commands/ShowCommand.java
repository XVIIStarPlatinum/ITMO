package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;
import data.MusicBandRaw;

public class ShowCommand implements Command{
    private CommandHandler commandHandler;

    public ShowCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("show", this);
    }

    @Override
    public Response execute(User user, String arguments, MusicBandRaw objectArg) {
        return commandHandler.show(user, arguments, objectArg);
    }
}
