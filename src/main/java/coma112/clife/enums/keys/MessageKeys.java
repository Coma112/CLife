package coma112.clife.enums.keys;

import coma112.clife.CLife;
import coma112.clife.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum MessageKeys {
    RELOAD("messages.reload"),
    HELP("messages.help"),
    ALREADY_IN_MATCH("messages.already-in-match"),
    NOT_IN_MATCH("messages.not-in-match"),
    TARGET_NOT_IN_MATCH("messages.target-not-in-match"),
    CANT_BE_NULL("messages.cant-be-null"),
    SUCCESSFUL_RADIUS("messages.successful-radius"),
    SUCCESSFUL_CENTER("messages.successful-center"),
    RTP_DISABLED("messages.rtp-disabled"),
    SUCCESSFUL_SETCOLOR("messages.successful-setcolor"),
    SUCCESSFUL_SETLOBBY("messages.successful-setlobby"),
    NO_MATCH_FOUND("messages.no-match-found"),
    NO_LOBBY("messages.no-lobby"),
    IN_QUEUE("messages.in-queue"),
    QUEUE_UPDATE_LEFT("messages.queue-update-left"),
    QUEUE_UPDATE_JOIN("messages.queue-update-join"),
    REMOVE_FROM_QUEUE("messages.removed-from-queue"),
    NOT_IN_QUEUE("messages.not-in-queue"),
    MAX_QUEUE("messages.queue-max"),
    SUCCESSFUL_ADD_PLAYER("messages.successful-add-time-player"),
    SUCCESSFUL_ADD_TARGET("messages.successful-add-time-target"),
    SUCCESSFUL_REMOVE_PLAYER("messages.successful-remove-time-player"),
    SUCCESSFUL_REMOVE_TARGET("messages.successful-remove-time-target"),
    SUCCESSFUL_STOPALL("messages.successful-stopall"),
    NOT_ENOUGH_PLAYERS("messages.not-enough-players");

    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(CLife.getInstance().getLanguage().getString(path));
    }

    public List<String> getMessages() {
        return CLife.getInstance().getLanguage().getList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();
    }

}
