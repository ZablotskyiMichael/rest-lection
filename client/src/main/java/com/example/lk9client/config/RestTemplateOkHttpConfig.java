package com.example.lk9client.config;

import okhttp3.OkHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;


@Profile("!dev")
@Configuration
public class RestTemplateOkHttpConfig {

  public static final Duration REST_TEMPLATE_TIMEOUT = Duration.ofSeconds(30);

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(REST_TEMPLATE_TIMEOUT)
            .readTimeout(REST_TEMPLATE_TIMEOUT)
            .build();
    return restTemplateBuilder
            .requestFactory(() -> new OkHttp3ClientHttpRequestFactory(client))
            .build();
  }

}
