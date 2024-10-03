package server.ru.itmo.se.utility;

import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.interaction.ResponseCode;

/**
 * Utility class used for interpreting client requests.
 */
public class RequestHandler {
    /**
     * This field holds a CommandManager which is responsible for all operations with commands.
     */
    private final CommandManager commandManager;

    /**
     * Constructs a RequestHandler with the specified command manager.
     * @param commandManager the specified CommandManager.
     */
    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * This method is used to handle interpret requests from the client.
     * @param request the client's request.
     * @return the according response to the request.
     */
    public Response handle(Request request) {
        commandManager.addToHistory(request.getCommandName());
        ResponseCode responseCode = executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg());
        return new Response(responseCode, ResponseAppender.getAndClear());
    }

    /**
     * This method is used to execute the client's request.
     * @param commandName   the request's command name.
     * @param commandStrArg the request's string argument.
     * @param commandObjArg the request's object argument.
     * @return the according response code.
     */
    private ResponseCode executeCommand(String commandName, String commandStrArg, Object commandObjArg) {
        if(commandManager.commandMap.containsKey(commandName)) {
            if(commandName.equals("server_exit")){
                return (commandManager.commandMap.get("server_exit").apply(commandStrArg, commandObjArg)) ? ResponseCode.SERVER_EXIT : ResponseCode.ERROR;
            } else {
                return (commandManager.commandMap.get(commandName).apply(commandStrArg, commandObjArg)) ? ResponseCode.OK : ResponseCode.ERROR;
            }
        } else if(commandName.isEmpty()) {
            return ResponseCode.ERROR;
        } else {
            CommandManager.noSuchCommand(commandName);
        }
        return ResponseCode.ERROR;
    }
}
