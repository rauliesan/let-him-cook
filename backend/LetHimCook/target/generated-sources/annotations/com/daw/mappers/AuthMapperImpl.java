package com.daw.mappers;

import com.daw.dtos.response.AuthResponseDTO;
import com.daw.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:10:23+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public AuthResponseDTO toAuthResponse(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        AuthResponseDTO.AuthResponseDTOBuilder authResponseDTO = AuthResponseDTO.builder();

        authResponseDTO.email( usuario.getEmail() );
        authResponseDTO.nombre( usuario.getNombre() );

        authResponseDTO.rol( usuario.getRol().name() );

        return authResponseDTO.build();
    }
}
