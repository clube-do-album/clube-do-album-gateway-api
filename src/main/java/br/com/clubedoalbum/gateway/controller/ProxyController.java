package br.com.clubedoalbum.gateway.controller;

import br.com.clubedoalbum.gateway.config.GatewayProperties;
import br.com.clubedoalbum.gateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

  private final GatewayProperties gatewayProperties;
  private final ProxyService proxyService;

  public ProxyController(GatewayProperties gatewayProperties, ProxyService proxyService) {
    this.gatewayProperties = gatewayProperties;
    this.proxyService = proxyService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<String> login(@RequestBody(required = false) String body, HttpServletRequest request) {
    return forward(HttpMethod.POST, gatewayProperties.identityApiUrl(), "/auth/login", body, request);
  }

  @PostMapping("/users")
  public ResponseEntity<String> createUser(@RequestBody(required = false) String body, HttpServletRequest request) {
    return forward(HttpMethod.POST, gatewayProperties.identityApiUrl(), "/users", body, request);
  }

  @GetMapping({"/users", "/users/{id}"})
  public ResponseEntity<String> getUsers(@PathVariable(required = false) String id, HttpServletRequest request) {
    String targetPath = id == null ? "/users" : "/users/" + id;
    return forward(HttpMethod.GET, gatewayProperties.identityApiUrl(), targetPath, null, request);
  }

  @GetMapping("/albums")
  public ResponseEntity<String> listAlbums(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.catalogApiUrl(), "/albums", null, request);
  }

  @GetMapping("/albums/search")
  public ResponseEntity<String> searchAlbums(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.catalogApiUrl(), "/albums/search", null, request);
  }

  @GetMapping("/albums/{id}")
  public ResponseEntity<String> getAlbumById(@PathVariable String id, HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.catalogApiUrl(), "/albums/" + id, null, request);
  }

  @PostMapping("/albums/import")
  public ResponseEntity<String> importAlbum(@RequestBody(required = false) String body, HttpServletRequest request) {
    return forward(HttpMethod.POST, gatewayProperties.catalogApiUrl(), "/albums/import", body, request);
  }

  @PostMapping("/ratings")
  public ResponseEntity<String> createRating(@RequestBody(required = false) String body, HttpServletRequest request) {
    return forward(HttpMethod.POST, gatewayProperties.ratingsApiUrl(), "/ratings", body, request);
  }

  @GetMapping({"/ratings/albums/{albumId}", "/ratings/users/{userId}", "/ratings/users/{userId}/public"})
  public ResponseEntity<String> getRatings(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.ratingsApiUrl(), request.getRequestURI(), null, request);
  }

  @GetMapping({"/rankings", "/rankings/{albumId}"})
  public ResponseEntity<String> getRankings(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.rankingApiUrl(), request.getRequestURI(), null, request);
  }

  @GetMapping({"/feed", "/feed/users/{userId}", "/feed/albums/{albumId}"})
  public ResponseEntity<String> getFeed(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.feedApiUrl(), request.getRequestURI(), null, request);
  }

  @PostMapping("/follows/{userId}")
  public ResponseEntity<String> followUser(@RequestBody(required = false) String body, HttpServletRequest request) {
    return forward(HttpMethod.POST, gatewayProperties.socialApiUrl(), request.getRequestURI(), body, request);
  }

  @DeleteMapping("/follows/{userId}")
  public ResponseEntity<String> unfollowUser(HttpServletRequest request) {
    return forward(HttpMethod.DELETE, gatewayProperties.socialApiUrl(), request.getRequestURI(), null, request);
  }

  @GetMapping({"/follows/following", "/follows/followers"})
  public ResponseEntity<String> getFollows(HttpServletRequest request) {
    return forward(HttpMethod.GET, gatewayProperties.socialApiUrl(), request.getRequestURI(), null, request);
  }

  private ResponseEntity<String> forward(
      HttpMethod method,
      String baseUrl,
      String targetPath,
      String body,
      HttpServletRequest request
  ) {
    return proxyService.forward(method, baseUrl, targetPath, body, request);
  }

}
