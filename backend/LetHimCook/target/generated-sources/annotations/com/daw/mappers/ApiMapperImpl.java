package com.daw.mappers;

import com.daw.dtos.request.ApiRequestDTO;
import com.daw.dtos.response.ApiResponseDTO;
import com.daw.entities.Api;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:24:37+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ApiMapperImpl implements ApiMapper {

    @Override
    public Api toEntity(ApiRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Api api = new Api();

        api.setApiKey( dto.getApiKey() );
        api.setEndpointUrl( dto.getEndpointUrl() );
        api.setNombreServicio( dto.getNombreServicio() );

        return api;
    }

    @Override
    public ApiResponseDTO toResponseDTO(Api entity) {
        if ( entity == null ) {
            return null;
        }

        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();

        apiResponseDTO.setApiKey( entity.getApiKey() );
        apiResponseDTO.setEndpointUrl( entity.getEndpointUrl() );
        apiResponseDTO.setId( entity.getId() );
        apiResponseDTO.setNombreServicio( entity.getNombreServicio() );

        return apiResponseDTO;
    }
}
