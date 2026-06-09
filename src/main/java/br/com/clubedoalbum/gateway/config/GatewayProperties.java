package br.com.clubedoalbum.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record GatewayProperties(
    String identityApiUrl,
    String catalogApiUrl,
    String ratingsApiUrl,
    String rankingApiUrl,
    String feedApiUrl,
    String socialApiUrl
) {}
