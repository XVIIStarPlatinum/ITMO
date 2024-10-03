package Commands;

import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;
import Network.User;
import data.MusicBandRaw;

public class FilterLessThanNumberOfParticipantsCommand implements Command{
    private CommandHandler commandHandler;

    public FilterLessThanNumberOfParticipantsCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("filter_less_than_number_of_participants", this);
    }

    @Override
    public Response execute(User user, String arguments, MusicBandRaw objectArg) {
        return commandHandler.filterLessThanNumberOfParticipants(user, arguments, objectArg);
    }
}
