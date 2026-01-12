package com.talentstream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * AwsSecretProvider: tries legacy util first; if a secretName is provided,
 * attempts to fetch using AWS SDK. If no secretName and legacy is not usable,
 * returns null (tolerant for local/dev).
 */
@Component
public class AwsSecretProvider {

    private static final Logger log = LoggerFactory.getLogger(AwsSecretProvider.class);

    public String getSecretString(String secretName, String region) {
        // 1) Try legacy util first (unchanged)
        try {
            String legacy = AwsSecretsManagerUtil.getSecret();
            if (legacy != null && isValidJson(legacy)) {
                JSONObject j = new JSONObject(legacy);
                if (j.has("AWS_ACCESS_KEY_ID") || j.has("S3_VIDEO_BUCKET_NAME") || j.has("AWS_REGION")) {
                    log.info("Using secret from legacy AwsSecretsManagerUtil.getSecret()");
                    return legacy;
                }
            }
        } catch (Exception ex) {
            log.debug("Legacy AwsSecretsManagerUtil.getSecret() not usable: {}", ex.getMessage());
        }

        // 2) If a secret name is provided, fetch from Secrets Manager using SDK
        if (secretName == null || secretName.isBlank()) {
            // no secretName provided and legacy did not return usable JSON -> return null (caller should handle)
            log.warn("No secret name provided and legacy AwsSecretsManagerUtil didn't return usable JSON. " +
                     "Secrets will not be loaded from AWS Secrets Manager in this runtime.");
            return null;
        }

        // attempt to fetch the secret using SDK v2 - this will require valid credentials (role or env)
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region == null || region.isBlank() ? Region.AP_SOUTH_1 : Region.of(region))
                .build()) {

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();
            if (secretString != null && isValidJson(secretString)) {
                log.info("Fetched secret '{}' from AWS Secrets Manager", secretName);
                return secretString;
            } else {
                log.warn("Fetched secret '{}' but payload was empty or not JSON", secretName);
                return null;
            }
        } catch (Exception e) {
            String msg = "Failed to fetch secret '" + secretName + "' from AWS Secrets Manager: " + e.getMessage();
            log.error(msg, e);
            // propagate exception only when a secretName was explicitly provided (production should see the error)
            throw new RuntimeException(msg, e);
        }
    }

    private boolean isValidJson(String s) {
        if (s == null || s.isBlank()) return false;
        try {
            new JSONObject(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
