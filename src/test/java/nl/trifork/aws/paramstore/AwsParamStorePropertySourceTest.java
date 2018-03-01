package nl.trifork.aws.paramstore;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AwsParamStorePropertySourceTest {

    private AWSSimpleSystemsManagement ssmClient = mock(AWSSimpleSystemsManagement.class);
    private AwsParamStorePropertySource propertySource =
            new AwsParamStorePropertySource("/config/myservice/", ssmClient);

    @Test
    public void followsNextToken() {
        GetParametersByPathResult firstResult = new GetParametersByPathResult()
            .withNextToken("next")
            .withParameters(
                new Parameter().withName("/config/myservice/key1").withValue("value1"),
                new Parameter().withName("/config/myservice/key2").withValue("value2")
            );

        GetParametersByPathResult nextResult = new GetParametersByPathResult()
            .withParameters(
                new Parameter().withName("/config/myservice/key3").withValue("value3"),
                new Parameter().withName("/config/myservice/key4").withValue("value4")
            );

        when(ssmClient.getParametersByPath(any(GetParametersByPathRequest.class)))
            .thenReturn(firstResult)
            .thenReturn(nextResult);

        propertySource.init();

        assertThat(propertySource.getPropertyNames()).containsExactly("key1", "key2", "key3", "key4");
        assertThat(propertySource.getProperty("key3")).isEqualTo("value3");
    }
}
