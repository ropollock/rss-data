package org.russpollock.rss.repository;

import org.russpollock.rss.RssEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

public final class ElasticSearchClient {
    private Client client;
    private static final Logger logger = LogManager.getLogger(ElasticSearchClient.class.getName());
    private static final RssEnvironment ENV = RssEnvironment.getInstance();

    public Client getClient() {
        return client;
    }

    public ElasticSearchClient() {
        client = connect();
    }

    private static class LazyHolder {
        private static final ElasticSearchClient INSTANCE = new ElasticSearchClient();
    }

    public static ElasticSearchClient getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static String getClusterName() throws NullPointerException {
        final String clusterName = ENV.getEnv(ENV.getESClusterNameProp());
        if(clusterName == null) {
            throw new NullPointerException(
                    String.format("Environment variable null: %s", ENV.getESClusterNameProp()));
        }
        return clusterName;
    }

    /**
     * getTransportAddresses
     *
     * Gets transport addresses from environment variable as a comma separated List.
     *
     * @return List<String>
     * @throws NullPointerException
     */
    private static List<String> getTransportAddresses() throws NullPointerException {
        final String transportAddresses = ENV.getEnv(ENV.getESTransportAddressesProp());
        if(transportAddresses == null ) {
            throw new NullPointerException(
                    String.format("Environment variable null: %s", ENV.getESTransportAddressesProp()));
        }
        return Arrays.asList(transportAddresses.split("\\s*,\\s*"));
    }

    /**
     * connect
     *
     * Prepares a PreBuiltTransportClient and joins the ES cluster.
     * Uses environment variables `cluster.name` an `transportAddresses`
     *
     * @return Client
     */
    private static Client connect() {
        logger.info("Joining ES cluster name: " + ElasticSearchClient.getClusterName());
        Settings settings = Settings.builder()
                .put("cluster.name", ElasticSearchClient.getClusterName()).build();
        List<String> transportAddrs = ElasticSearchClient.getTransportAddresses();
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        for(String addr : transportAddrs) {
            try {
                logger.info("Adding transport address: " + addr);
                transportClient.addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(addr), 9300));
            } catch(Exception e) {
                logger.error("Unable to add transport address:" + addr, e);
            }
        }
        return transportClient;
    }
}
