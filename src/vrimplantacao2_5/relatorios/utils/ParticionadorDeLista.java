/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.relatorios.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Desenvolvimento
 */
public class ParticionadorDeLista {
    
        public List[] particionar(List<?> lista, int qtdItens) {
        // obtém o tamanho da lista
        int tamanhoTotal = lista.size();

        // Calcula o número total de partições `particoes` de tamanho `qtdItens` cada
        int qtdParticao = tamanhoTotal / qtdItens;
        if (tamanhoTotal % qtdItens != 0) {
            qtdParticao++;
        }

        // cria `n` (qtdParticao) listas vazias e inicializa-as usando `List.subList()`
        List<?>[] particoes = new ArrayList[qtdParticao];
        for (int i = 0; i < qtdParticao; i++) {
            //vai adicionando itens a nova lista (sublist) calculando com base nas particoes
            int doIndex = i * qtdItens;
            int paraIndex = (i * qtdItens + qtdItens < tamanhoTotal) ? (i * qtdItens + qtdItens) : tamanhoTotal;

            particoes[i] = new ArrayList(lista.subList(doIndex, paraIndex));
        }

        // retorna as listas
        return particoes;
    }
}
