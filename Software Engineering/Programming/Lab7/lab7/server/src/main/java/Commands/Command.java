package Commands;

import Network.Response;
import Network.User;
import data.MusicBandRaw;

public interface Command {
    Response execute(User user, String strArgument, MusicBandRaw objArgument);
}
