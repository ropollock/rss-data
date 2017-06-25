package com.russpollock.rss;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RSSEnvironment {
    private static final Logger LOGGER = LogManager.getLogger(RSSEnvironment.class);
    public static String ENV_PREFIX = "rss-data";
    private HashMap<String, String> vars;

    private String[] props = {
            getESClusterNameProp(),
            getESTransportAddressesProp(),
            getESArticleSearchIndiciesProp(),
            getESArticleWriteIndexProp(),
            getProvisionESWriteIndexProp(),
            getProvisionESDropAndCreateProp()
    };

    public RSSEnvironment() {
        initEnv();
    }

    private void initEnv() {
        LOGGER.info("Loading environment variables");
        vars = new HashMap<>();
        for(String prop : props) {
            final String propValue = System.getenv(prop);
            LOGGER.debug(String.format("Loaded environment variable %s as : %s", prop, propValue));
            vars.put(prop, propValue);
        }
    }

    /**
     * getEnv
     *
     * Gets an environment variable by property name.
     *
     * @param envVar String variable property name
     * @return String
     */
    public String getEnv(final String envVar) {
        return vars.get(envVar);
    }

    public String getESClusterNameProp() {
        return ENV_PREFIX + ".es.clusterName";
    }

    public String getESTransportAddressesProp() {
        return ENV_PREFIX + ".es.transportAddresses";
    }

    public String getESArticleSearchIndiciesProp() {
        return ENV_PREFIX + ".es.articleSearchIndicies";
    }

    public String getESArticleWriteIndexProp() {
        return ENV_PREFIX + ".es.articleWriteIndex";
    }

    public String getProvisionESWriteIndexProp() {
        return ENV_PREFIX + ".provision.es.writeIndex";
    }

    public String getProvisionESDropAndCreateProp() {
        return ENV_PREFIX + ".provision.es.dropAndCreate";
    }

    private static class LazyHolder {
        private static final RSSEnvironment INSTANCE = new RSSEnvironment();
    }

    public static RSSEnvironment getInstance() {
        return LazyHolder.INSTANCE;
    }
}
