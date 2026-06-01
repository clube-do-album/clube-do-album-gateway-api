package br.com.clubedoalbum.gateway.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    if (isPublicRoute(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      writeUnauthorized(response);
      return;
    }

    try {
      AuthenticatedUser authenticatedUser = jwtService.validate(authorization.substring(7));
      request.setAttribute("authenticatedUserId", authenticatedUser.id());
      request.setAttribute("authenticatedUserEmail", authenticatedUser.email());
      filterChain.doFilter(request, response);
    } catch (JwtException | IllegalArgumentException exception) {
      writeUnauthorized(response);
    }
  }

  private boolean isPublicRoute(HttpServletRequest request) {
    String method = request.getMethod();
    String path = request.getRequestURI();

    if (HttpMethod.OPTIONS.matches(method) || "/health".equals(path)) {
      return true;
    }

    if (HttpMethod.POST.matches(method) && ("/auth/login".equals(path) || "/users".equals(path))) {
      return true;
    }

    if (HttpMethod.GET.matches(method)) {
      return path.equals("/albums")
          || path.equals("/albums/search")
          || path.startsWith("/albums/")
          || path.equals("/rankings")
          || path.startsWith("/rankings/")
          || path.equals("/feed")
          || path.startsWith("/feed/albums/")
          || path.startsWith("/ratings/albums/");
    }

    return false;
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{\"message\":\"Unauthorized.\"}");
  }
}
