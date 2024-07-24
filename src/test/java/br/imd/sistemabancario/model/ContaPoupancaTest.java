/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package br.imd.sistemabancario.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

public class ContaPoupancaTest {
    
    public ContaPoupancaTest() {
    }
    
    private ContaPoupanca conta;
    
    @BeforeEach
    public void setUp() {
        conta = new ContaPoupanca(1, 100);
    }
    
    @Test
    @DisplayName("Render porcentagem de juros")
    public void RenderJurosTest(){
        conta.renderJuros(50);
        assertEquals(conta.getSaldo(), 150);
    }
}
