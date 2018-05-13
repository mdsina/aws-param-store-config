package nl.trifork.aws.paramstore;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nl.trifork.aws.paramstore.AwsParamStoreProperties.CONFIG_PREFIX;

/**
 * Builds a {@link CompositePropertySource} with various {@link AwsParamStorePropertySource} instances based on
 * active profiles, application name and default context permutations. Mostly copied from Spring Cloud Consul's
 * config support, but without the option to have full config files in a param value (as I don't see a use case
 * for that with the AWS Parameter Store).
 */
public class AwsParamStorePropertySourceLocator implements PropertySourceLocator {

    private AWSSimpleSystemsManagement ssmClient;
    private AwsParamStoreProperties properties;
    private List<String> contexts = new ArrayList<>();

    private Log logger = LogFactory.getLog(getClass());

    public AwsParamStorePropertySourceLocator(AWSSimpleSystemsManagement ssmClient,
                                              AwsParamStoreProperties properties) {
        this.ssmClient = ssmClient;
        this.properties = properties;
    }

    public List<String> getContexts() {
        return contexts;
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return null;
        }

        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

        String appName = properties.getName();

        if (appName == null) {
            appName = env.getProperty("spring.application.name");
        }

        List<String> profiles = Arrays.asList(env.getActiveProfiles());

        String prefix = env.getProperty(CONFIG_PREFIX + ".prefix", properties.getPrefix());
        this.properties.setPrefix(prefix);

        String defaultPropContext = env.getProperty(CONFIG_PREFIX + ".defaultContext", properties.getDefaultContext());
        this.properties.setDefaultContext(defaultPropContext);

        String defaultContext = prefix + "/" + defaultPropContext;
        contexts.add(defaultContext + "/");
        addProfiles(contexts, defaultContext, profiles);

        if (appName != null) {
            String baseContext = prefix + "/" + appName;
            contexts.add(baseContext + "/");
            addProfiles(contexts, baseContext, profiles);
        }

        Collections.reverse(contexts);

        CompositePropertySource composite = new CompositePropertySource("aws-param-store");

        for (String propertySourceContext : contexts) {
            try {
                composite.addPropertySource(create(propertySourceContext));
            } catch (Exception e) {
                if (this.properties.isFailFast()) {
                    logger.error("Fail fast is set and there was an error reading configuration from AWS Parameter Store:\n"
                        + e.getMessage());
                    ReflectionUtils.rethrowRuntimeException(e);
                } else {
                    logger.warn("Unable to load AWS config from " + propertySourceContext, e);
                }
            }
        }

        return composite;
    }

    private AwsParamStorePropertySource create(String context) {
        AwsParamStorePropertySource propertySource = new AwsParamStorePropertySource(context, this.ssmClient);
        propertySource.init();
        return propertySource;
    }

    private void addProfiles(List<String> contexts, String baseContext, List<String> profiles) {
        for (String profile : profiles) {
            contexts.add(baseContext + this.properties.getProfileSeparator() + profile + "/");
        }
    }

}
