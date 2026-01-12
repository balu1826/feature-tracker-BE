package com.talentstream.config;

import com.talentstream.AwsSecretProvider;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SecretConfig {

    private static final Logger log = LoggerFactory.getLogger(SecretConfig.class);

    private final AwsSecretProvider secretProvider;

    public SecretConfig(AwsSecretProvider secretProvider) {
        this.secretProvider = secretProvider;
    }

    // safe default: empty string — avoids "Could not resolve placeholder" error
    @Value("${aws.secrets.name:}")
    private String secretName;

    @Value("${aws.region:}")
    private String awsRegion; // optional; if blank, provider defaults to ap-south-1

    @Bean
    public SecretProperties secretProperties() {
        String resolvedSecretName = (secretName == null || secretName.isBlank()) ? System.getenv("AWS_SECRETS_NAME") : secretName;

        if (resolvedSecretName == null || resolvedSecretName.isBlank()) {
            log.info("No aws.secrets.name provided in properties and AWS_SECRETS_NAME not set. " +
                     "Attempting to load secrets via legacy env util (if present).");
        } else {
            log.info("Secret name resolved as '{}' (region='{}')", resolvedSecretName, awsRegion);
        }

        String secretJson = null;
        try {
            secretJson = secretProvider.getSecretString(resolvedSecretName, awsRegion);
        } catch (RuntimeException ex) {
            // If fetching explicitly failed (e.g. credentials/permissions) we rethrow — production should surface this
            throw ex;
        }

        if (secretJson == null || secretJson.isBlank()) {
            log.warn("Secret JSON not available. Returning empty SecretProperties. In production, set AWS_SECRETS_NAME or aws.secrets.name and ensure IAM permissions.");
            // Return empty SecretProperties — services should handle missing values gracefully or fail on use
            SecretProperties empty = new SecretProperties();
            empty.setRegion(awsRegion == null || awsRegion.isBlank() ? "ap-south-1" : awsRegion);
            empty.setBucketName("");
            empty.setAccessKey("");
            empty.setSecretKey("");
            empty.setCloudfrontDomain("");
            empty.setUseCloudFront(false);
            empty.setS3Domain("");
            empty.setCloudfrontUrl(null);
            empty.setSmtpUsername("");
            empty.setSmtpPassword("");
            return empty;
        }

        JSONObject json = new JSONObject(secretJson);

        SecretProperties p = new SecretProperties();
        p.setAccessKey(json.optString("AWS_ACCESS_KEY_ID", ""));
        p.setSecretKey(json.optString("AWS_SECRET_ACCESS_KEY", ""));
        p.setRegion(json.optString("AWS_REGION", awsRegion == null || awsRegion.isBlank() ? "ap-south-1" : awsRegion));
        p.setBucketName(json.optString("S3_VIDEO_BUCKET_NAME", ""));
        p.setCloudfrontDomain(json.optString("CLOUDFRONT_DOMAIN", ""));
        p.setUseCloudFront("true".equalsIgnoreCase(json.optString("USE_CLOUDFRONT", "false")));
        p.setS3Domain(p.getBucketName().isEmpty() ? "" : "https://" + p.getBucketName() + ".s3." + p.getRegion() + ".amazonaws.com");
        String cf = json.optString("CLOUDFRONT_DOMAIN", "");
        p.setCloudfrontUrl(cf.isBlank() ? null : (cf.startsWith("http") ? cf : "https://" + cf));
        p.setSmtpUsername(json.optString("SMTP_USERNAME", ""));
        p.setSmtpPassword(json.optString("SMTP_PASSWORD", ""));

        log.info("SecretProperties loaded: bucket='{}' region='{}' useCF={}", p.getBucketName(), p.getRegion(), p.isUseCloudFront());
        return p;
    }
}
