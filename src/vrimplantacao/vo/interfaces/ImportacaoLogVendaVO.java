package vrimplantacao.vo.interfaces;

import java.util.ArrayList;
import java.util.List;

public class ImportacaoLogVendaVO {

    public int idLoja = 0;
    public int numeroCupom = 0;
    public int ecf = 0;
    public String data = "";
    public int matricula = 0;
    public String horaInicio = "";
    public String horaTermino = "";
    public boolean cancelado = false;
    public double subtotalImpressora = 0;
    public boolean canceladoEmVenda = false;
    public int contadorDoc = 0;
    public long cpf = 0;
    public double valorDesconto = 0;
    public double valorAcrescimo = 0;
    public String numeroSerie = "";
    public int mfadicional = 0;
    public String modeloImpressora = "";
    public int numeroUsuario = 1;
    public String nomeCliente = "";
    public String enderecoCliente = "";
    public int idFinalizadora = 1;
    public int id = 0;
    public List<ImportacaoLogVendaItemVO> vLogVendaItem = new ArrayList();
    public int pagina = 0;
    public double valorTotal = 0;
    
    public int idClientePreferencial = -2;
    public String chavenfce;
    public String xml;
    public int matriculacancelamento = 0;
    public int id_tipocancelamento = 0;
    public String chavecfe;

    public double getTotalCancelado() {
        double cancel = 0;
        for (ImportacaoLogVendaItemVO item: vLogVendaItem) {
            cancel += item.valorCancelado;
        }
        return cancel;
    }

    public double getTotalAcrescimo() {
        double acrescimo = 0;
        for (ImportacaoLogVendaItemVO item: vLogVendaItem) {
            acrescimo += item.valorAcrescimo;
        }
        return acrescimo;
    }

    public double getTotalDesconto() {
        double desconto = 0;
        for (ImportacaoLogVendaItemVO item: vLogVendaItem) {
            desconto += item.valorDesconto;
        }
        return desconto;
    }

    public double getTotalItens() {
        double totalItem = 0;
        for (ImportacaoLogVendaItemVO item: vLogVendaItem) {
            totalItem += item.valorTotal;
        }
        return totalItem;
    }
    
    
}