package ch.adv.ui.core.service;

import ch.adv.ui.core.access.GsonProvider;

/**
 * Encapsulates a response to the ADV Lib.
 */
public class ADVResponse {

    private final ProtocolCommand command;
    private final String exceptionMessage;

    private final transient GsonProvider gsonProvider = new GsonProvider();

    public ADVResponse(ProtocolCommand command) {
        this(command, null);
    }

    public ADVResponse(ProtocolCommand command, String exceptionMessage) {
        this.command = command;
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the serialized string representation of this class
     */
    public String toJson() {
        return gsonProvider.getMinifier().toJson(this);
    }
}