package org.joelson.turf.turfgame.util;

import org.slf4j.Logger;

public class DefaultFeedContentLoggerErrorHandler extends DefaultFeedContentErrorHandler {

    private final Logger logger;

    public DefaultFeedContentLoggerErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void message(String msg) {
        logger.warn(msg);
    }
}
