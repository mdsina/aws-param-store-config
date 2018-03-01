package nl.trifork.aws.paramstore;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ConfigurationProperties(AwsParamStoreProperties.CONFIG_PREFIX)
@Validated
public class AwsParamStoreProperties {

    static final String CONFIG_PREFIX = "aws.paramstore";

    /**
     * Prefix indicating first level for every property.
     * Value must start with a forward slash followed by a valid path segment or be empty.
     * Defaults to "/config".
     */
    @NotNull @Pattern(regexp = "(/[a-zA-Z0-9.\\-_]+)*")
    private String prefix = "/config";
    @NotEmpty
    private String defaultContext = "application";
    @NotNull @Pattern(regexp = "[a-zA-Z0-9.\\-_]+")
    private String profileSeparator = "_";

    /** Throw exceptions during config lookup if true, otherwise, log warnings. */
    private boolean failFast = true;

    /** Alternative to spring.application.name to use in looking up values in AWS Parameter Store. */
    private String name;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDefaultContext() {
        return defaultContext;
    }

    public void setDefaultContext(String defaultContext) {
        this.defaultContext = defaultContext;
    }

    public String getProfileSeparator() {
        return profileSeparator;
    }

    public void setProfileSeparator(String profileSeparator) {
        this.profileSeparator = profileSeparator;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
