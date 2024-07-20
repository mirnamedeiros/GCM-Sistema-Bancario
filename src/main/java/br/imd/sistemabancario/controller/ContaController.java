package br.imd.sistemabancario.controller;

import br.imd.sistemabancario.controller.dto.ContaDTO;
import br.imd.sistemabancario.controller.dto.JurosDTO;
import br.imd.sistemabancario.controller.dto.TranferenciaDTO;
import br.imd.sistemabancario.controller.dto.ValorDTO;
import br.imd.sistemabancario.exception.NotFoundException;
import br.imd.sistemabancario.service.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/banco")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/contas")
    public void cadastrarConta(@RequestBody ContaDTO contaDTO) {
        contaService.cadastrarConta(contaDTO.numero(), contaDTO.tipo(), contaDTO.saldo());
    }

    @GetMapping("/contas/{numero}")
    public ContaDTO consultarDados(@PathVariable int numero) {
        return contaService.consultarDados(numero);
    }

    @GetMapping("/contas/{numero}/saldo")
    public ContaDTO consultarSaldo(@PathVariable int numero) {
        return contaService.consultarSaldo(numero)
                .map(saldo -> new ContaDTO(numero, null, saldo))
                .orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/contas/{numero}/creditar")
    public void creditarConta(@PathVariable int numero, @RequestBody ValorDTO valorDTO) {
        contaService.creditarConta(numero, valorDTO.valor());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/contas/{numero}/debitar")
    public void debitarConta(@PathVariable int numero, @RequestBody ValorDTO valorDTO) {
        contaService.debitarConta(numero, valorDTO.valor());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/contas/transferir")
    public void transferir(@RequestBody TranferenciaDTO tranferenciaDTO) {
        contaService.transferir(tranferenciaDTO.from(), tranferenciaDTO.to(), tranferenciaDTO.amount());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/contas/rendimento")
    public void aplicarRendimento(@RequestBody JurosDTO jurosDTO) {
        contaService.contabilizarJuros(jurosDTO.taxa());
    }
}
