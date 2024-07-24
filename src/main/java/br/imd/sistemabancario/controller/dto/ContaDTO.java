package br.imd.sistemabancario.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContaDTO(Integer numero, Integer tipo, Double saldo) {
}
