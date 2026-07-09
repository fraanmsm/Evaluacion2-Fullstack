package cl.duoc.api_gateway.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
            
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().contains("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Acceso denegado: Falta el token JWT");
            return;
        }

        String token = authHeader.substring(7);

        try {
            jwtUtil.validarToken(token);
            chain.doFilter(request, response);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write("Acceso denegado: Token invalido o expirado");
        }
    }
}
