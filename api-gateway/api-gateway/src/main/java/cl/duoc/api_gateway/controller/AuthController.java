package cl.duoc.api_gateway.controller;

import cl.duoc.api_gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String usuario, @RequestParam String password) {
        if ("medico".equals(usuario) && "admin123".equals(password)) {
            return jwtUtil.generarToken(usuario);
        }
        throw new RuntimeException("Credenciales incorrectas");
    }
}
