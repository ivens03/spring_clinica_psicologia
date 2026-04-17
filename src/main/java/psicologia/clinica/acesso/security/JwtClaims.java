package psicologia.clinica.acesso.security;

import java.util.List;

public record JwtClaims(
        String subject,
        String email,
        String nome,
        String clinicaId,
        List<String> roles
) {
}
