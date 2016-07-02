package xyz.openmodloader.event.impl;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.OMLClientHandler;
import xyz.openmodloader.event.Event;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.server.OMLServerHandler;

/**
 * Parent class for message related events. All events that fall within this
 * scope should extend this class. They should usually also be added as
 * inner classes, although this is not an absolute requirement.
 */
public abstract class MessageEvent extends Event {

    /**
     * The message that will be displayed in chat.
     */
    private ITextComponent message;

    /**
     * The side that the event is being fired on. Server/Client
     */
    private final Side side;

    public MessageEvent(ITextComponent message, Side side) {
        this.message = message;
        this.side = side;
    }

    /**
     * Fired whenever a chat message is received.
     * Fired from {@link PlayerList#sendChatMsgImpl(ICommandSender, ITextComponent, boolean)}
     * and {@link NetHandlerPlayClient#handleChat(net.minecraft.network.play.server.SPacketChat)}.
     * 
     * This event is cancelable.
     */
    public static class Chat extends MessageEvent {

        /**
         * The player who sent the message. May be null.
         */
        private final ICommandSender sender;

        /**
         * Constructs a new event that is fired when a chat message is
         * displayed.
         *
         * @param message The message that was received.
         * @param sender The player who sent the message
         * @param side The side that the event is being fired on.
         */
        public Chat(ITextComponent message, ICommandSender sender, Side side) {
            super(message, side);
            this.sender = sender;
        }

        /**
         * Gets the player who sent the message.
         * Always null on the client. May also be null
         * on the server, if the message was sent from code.
         * 
         * @return the player who sent the chat message - MAY BE NULL!
         */
        public ICommandSender getSender() {
            return sender;
        }

        /**
         * Hook to make related patches much cleaner.
         * 
         * @param sender The player who sent the message.
         * @param message The message that was received.
         * @param side The side that it was received on.
         * @return The message to actually display.
         */
        public static ITextComponent handle(ITextComponent message, ICommandSender sender) {
            final MessageEvent.Chat event = new MessageEvent.Chat(message, sender, sender == null ? Side.CLIENT : Side.SERVER);
            return OpenModLoader.getEventBus().post(event) ? event.getMessage() : null;
        }
    }

    /**
     * Fired whenever a snackbar is opened.
     * Fired from {@link OMLClientHandler#openSnackbar(ITextComponent)}
     * and {@link OMLServerHandler#openSnackbar(ITextComponent)}.
     * 
     * This event is cancelable.
     */
    public static class Snackbar extends MessageEvent {

        /**
         * Constructs a new event that is fired when a chat message is
         * displayed.
         *
         * @param message The message that was received.
         * @param side The side that the event is being fired on.
         */
        public Snackbar(ITextComponent message, Side side) {
            super(message, side);
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param message The message that was received.
         * @param side The side that it was received on.
         * @return The message to actually display.
         */
        public static ITextComponent handle(ITextComponent message, Side side) {
            final MessageEvent.Snackbar event = new MessageEvent.Snackbar(message, side);
            return OpenModLoader.getEventBus().post(event) ? event.getMessage() : null;
        }
    }

    /**
     * Gets the message that was received.
     *
     * @return The message that was received.
     */
    public ITextComponent getMessage() {
        return message;
    }

    /**
     * Sets the message to display in chat to a new one.
     *
     * @param message The new message to display.
     */
    public void setMessage(ITextComponent message) {
        this.message = message;
    }

    /**
     * Gets the side that the event is being fired on. Allows for easy
     * differentiation between client and server.
     *
     * @return The side where the event was fired.
     */
    public Side getSide() {
        return side;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
