package br.com.clubedoalbum.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProxyService {

  private static final List<String> BLOCKED_REQUEST_HEADERS = List.of(
      HttpHeaders.HOST,
      HttpHeaders.CONTENT_LENGTH,
      HttpHeaders.TRANSFER_ENCODING,
      HttpHeaders.CONNECTION
  );

  private static final List<String> BLOCKED_RESPONSE_HEADERS = List.of(
      HttpHeaders.CONTENT_LENGTH,
      HttpHeaders.TRANSFER_ENCODING,
      HttpHeaders.CONNECTION,
      HttpHeaders.CONTENT_ENCODING,
      HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
      HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
      HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
      HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
      HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
      HttpHeaders.ACCESS_CONTROL_MAX_AGE
  );

  private final RestClient.Builder restClientBuilder;

  public ProxyService(RestClient.Builder restClientBuilder) {
    this.restClientBuilder = restClientBuilder;
  }

  public ResponseEntity<String> forward(
      HttpMethod method,
      String baseUrl,
      String targetPath,
      String body,
      HttpServletRequest request
  ) {
    URI uri = buildUri(baseUrl, targetPath, request.getQueryString());

    RestClient.RequestBodySpec requestSpec = restClientBuilder.build()
        .method(method)
        .uri(uri)
        .headers(headers -> copyHeaders(request, headers));

    RestClient.RequestHeadersSpec<?> headersSpec = body == null
        ? requestSpec
        : requestSpec.body(body);

    return headersSpec.exchange((clientRequest, clientResponse) -> {
      String responseBody = clientResponse.bodyTo(String.class);
      HttpHeaders responseHeaders = new HttpHeaders();
      copyResponseHeaders(clientResponse.getHeaders(), responseHeaders);
      HttpStatusCode statusCode = clientResponse.getStatusCode();

      return new ResponseEntity<>(responseBody, responseHeaders, statusCode);
    });
  }

  private URI buildUri(String baseUrl, String targetPath, String queryString) {
    String normalizedBaseUrl = baseUrl.endsWith("/")
        ? baseUrl.substring(0, baseUrl.length() - 1)
        : baseUrl;
    String normalizedTargetPath = targetPath.startsWith("/")
        ? targetPath
        : "/" + targetPath;
    String query = queryString == null || queryString.isBlank()
        ? ""
        : "?" + queryString;

    return URI.create(normalizedBaseUrl + normalizedTargetPath + query);
  }

  private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
    Collections.list(request.getHeaderNames()).stream()
        .filter(name -> BLOCKED_REQUEST_HEADERS.stream().noneMatch(name::equalsIgnoreCase))
        .forEach(name -> headers.put(name, Collections.list(request.getHeaders(name))));

    Object userId = request.getAttribute("authenticatedUserId");
    Object userEmail = request.getAttribute("authenticatedUserEmail");

    if (userId != null) {
      headers.set("X-User-Id", userId.toString());
    }

    if (userEmail != null) {
      headers.set("X-User-Email", userEmail.toString());
    }
  }

  private void copyResponseHeaders(HttpHeaders source, HttpHeaders target) {
    source.forEach((name, values) -> {
      if (BLOCKED_RESPONSE_HEADERS.stream().noneMatch(name::equalsIgnoreCase)) {
        target.put(name, values);
      }
    });
  }
}
