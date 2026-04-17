package psicologia.clinica.acesso.security;

import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;

import java.util.ArrayList;
import java.util.List;

public final class PerfilAuthorities {

    private PerfilAuthorities() {
    }

    public static List<String> roles(PerfilRoot perfilRoot, SubPerfil subPerfil) {
        List<String> roles = new ArrayList<>();

        if (perfilRoot != null) {
            roles.add("ROLE_" + perfilRoot.name());
        }

        if (subPerfil != null) {
            roles.add("ROLE_" + subPerfil.name());
        }

        return roles;
    }
}
