package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;
import data.MusicBandRaw;

public class HelpCommand implements Command {
    CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("help", this);
    }

    @Override
    public Response execute(User user, String arguments, MusicBandRaw objectArg) {
        return commandHandler.help(user, arguments, objectArg);
    }
}
