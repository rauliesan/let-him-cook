package com.daw.mappers;

import com.daw.dtos.request.IaModeloRequestDTO;
import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.entities.IaModelo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IaModeloMapper {

    @Mapping(target = "api.id", source = "apiId")
    IaModelo toEntity(IaModeloRequestDTO dto);

    @Mapping(target = "apiId", source = "api.id")
    @Mapping(target = "apiNombreServicio", source = "api.nombreServicio")
    @Mapping(target = "apiEndpointUrl", source = "api.endpointUrl")
    @Mapping(target = "apiKey", source = "api.apiKey") // Expuesto para Angular
    IaModeloResponseDTO toResponseDTO(IaModelo entity);
}