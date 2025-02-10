package org.joelson.turf.turfgame.util;

import java.io.IOException;
import java.nio.file.Path;

public interface FeedContentErrorHandler {

    void handleErrorContent(Path path, String content, IOException e);
}
