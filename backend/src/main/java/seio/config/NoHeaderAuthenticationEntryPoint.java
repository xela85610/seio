package seio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public class NoHeaderAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String,Object> body = Map.of(
                "status", 401,
                "error", "Identifiants incorrects",
                "message", "Identifiants incorrects",
                "path", request.getRequestURI(),
                "timestamp", Instant.now().toString()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}
