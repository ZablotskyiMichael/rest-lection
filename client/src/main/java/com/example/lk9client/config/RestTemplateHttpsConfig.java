package com.example.lk9client.config;

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
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;


@Profile("dev")
@Configuration
public class RestTemplateHttpsConfig {

  public static final Duration REST_TEMPLATE_TIMEOUT = Duration.ofSeconds(30);

  @Value("${trust.store}")
  private Resource trustStore;

  @Value("${trust.store.password}")
  private String trustStorePass;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
          throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContext sslContext = new SSLContextBuilder()
            .loadTrustMaterial(trustStore.getURL(), trustStorePass.toCharArray()).build();
    CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLContext(sslContext).build();
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    restTemplateBuilder.requestFactory(() -> requestFactory)
            .setConnectTimeout(REST_TEMPLATE_TIMEOUT)
            .setReadTimeout(REST_TEMPLATE_TIMEOUT);
    return new RestTemplate(requestFactory);
  }
}
