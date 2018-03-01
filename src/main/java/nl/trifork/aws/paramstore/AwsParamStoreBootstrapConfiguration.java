package nl.trifork.aws.paramstore;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.trifork.aws.paramstore.AwsParamStoreProperties.CONFIG_PREFIX;

@Configuration
@EnableConfigurationProperties(AwsParamStoreProperties.class)
@ConditionalOnProperty(prefix = CONFIG_PREFIX, name= "enabled", matchIfMissing = true)
public class AwsParamStoreBootstrapConfiguration {

    @Bean
    AwsParamStorePropertySourceLocator awsParamStorePropertySourceLocator(
                                    AWSSimpleSystemsManagement ssmClient, AwsParamStoreProperties properties) {
        return new AwsParamStorePropertySourceLocator(ssmClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    AWSSimpleSystemsManagement ssmClient() {
        return AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }

}
