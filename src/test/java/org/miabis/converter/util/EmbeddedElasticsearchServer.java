package org.miabis.converter.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

public class EmbeddedElasticsearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticsearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        Settings settings = Settings.settingsBuilder()
                .put("http.enabled", "false")
                .put("cluster", "test")
                .put("path.home", dataDirectory)
                .put("transport.tcp.port", 9300).build();

        node = nodeBuilder()
                .settings(settings)
                .node().start();
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}