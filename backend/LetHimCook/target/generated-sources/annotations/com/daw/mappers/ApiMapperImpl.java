package com.daw.mappers;

import com.daw.dtos.request.ApiRequestDTO;
import com.daw.dtos.response.ApiResponseDTO;
import com.daw.entities.Api;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:10:23+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
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

    @Override
    public List<ApiResponseDTO> toListDTO(List<Api> list) {
        if ( list == null ) {
            return null;
        }

        List<ApiResponseDTO> list1 = new ArrayList<ApiResponseDTO>( list.size() );
        for ( Api api : list ) {
            list1.add( toResponseDTO( api ) );
        }

        return list1;
    }
}
