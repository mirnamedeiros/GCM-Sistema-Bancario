package br.imd.sistemabancario.repository;

import br.imd.sistemabancario.model.Conta;
import br.imd.sistemabancario.model.ContaBonus;
import br.imd.sistemabancario.model.ContaPoupanca;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class ContaRepository {

    private Set<Conta> contas;

    public ContaRepository() {
        contas = new HashSet<>();
        contas.add(new Conta(123, 568));
        contas.add(new Conta(456, 59));
        contas.add(new Conta(789, 15));
        contas.add(new ContaPoupanca(159, 1000));
        contas.add(new ContaBonus(444, 65));
        contas.add(new ContaBonus(555, 1900));
    }

    public Conta save(Conta conta) {
        contas.add(conta);
        return conta;
    }

    public boolean existsByNumero(int numero) {
        return contas.stream()
                .anyMatch(conta -> conta.getNumero() == numero);
    }

    public Optional<Conta> findByNumero(int numero) {
        return contas.stream()
                .filter(conta -> conta.getNumero() == numero)
                .findFirst();
    }

    public Set<Conta> findAll() {
        return contas;
    }
}
