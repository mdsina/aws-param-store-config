package nl.trifork.aws.paramstore;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Recursively retrieves all parameters under the given context / path with decryption from the AWS Parameter Store
 * using the provided SSM client.
 */
public class AwsParamStorePropertySource extends EnumerablePropertySource<AWSSimpleSystemsManagement> {

    private String context;
    private Map<String, Object> properties = new LinkedHashMap<>();

    public AwsParamStorePropertySource(String context, AWSSimpleSystemsManagement ssmClient) {
        super(context, ssmClient);
        this.context = context;
    }

    public void init() {
        GetParametersByPathRequest paramsRequest = new GetParametersByPathRequest()
            .withPath(context)
            .withRecursive(true)
            .withWithDecryption(true);
        getParameters(paramsRequest);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = properties.keySet();
        return strings.toArray(new String[strings.size()]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    private void getParameters(GetParametersByPathRequest paramsRequest) {
        GetParametersByPathResult paramsResult = source.getParametersByPath(paramsRequest);
        for (Parameter parameter : paramsResult.getParameters()) {
            String key = parameter.getName().replace(context, "").replace('/', '.');
            properties.put(key, parameter.getValue());
        }
        if (paramsResult.getNextToken() != null) {
            getParameters(paramsRequest.withNextToken(paramsResult.getNextToken()));
        }
    }

}
