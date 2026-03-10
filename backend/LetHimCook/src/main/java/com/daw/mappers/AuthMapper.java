package com.daw.mappers;

import com.daw.dtos.response.AuthResponseDTO;
import com.daw.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper de MapStruct para convertir la entidad {@link Usuario}
 * en el DTO de respuesta de autenticación {@link AuthResponseDTO}.
 *
 * <p>
 * El campo {@code token} no proviene de la entidad, por lo que
 * se inyecta a través del método {@code default} que añade el token
 * al DTO ya mapeado.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "rol", expression = "java(usuario.getRol().name())")
    @Mapping(target = "token", ignore = true)
    AuthResponseDTO toAuthResponse(Usuario usuario);

    /**
     * Convierte un {@link Usuario} en un {@link AuthResponseDTO}
     * incluyendo el token JWT generado.
     *
     * @param usuario entidad del usuario autenticado
     * @param token   token JWT generado
     * @return DTO de respuesta con los datos del usuario y el token
     */
    default AuthResponseDTO toAuthResponse(Usuario usuario, String token) {
        AuthResponseDTO dto = toAuthResponse(usuario);
        dto.setToken(token);
        return dto;
    }
}
