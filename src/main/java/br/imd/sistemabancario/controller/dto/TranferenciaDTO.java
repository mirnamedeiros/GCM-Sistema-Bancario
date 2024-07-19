package br.imd.sistemabancario.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TranferenciaDTO(Integer from, Integer to, Double amount) {
}
