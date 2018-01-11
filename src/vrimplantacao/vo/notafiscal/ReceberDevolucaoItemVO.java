package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ReceberDevolucaoItemVO implements Serializable {

    public double valor = 0;
    public double valorDesconto = 0;
    public double valorJuros = 0;
    public double valorMulta = 0;
    public double valorTotal = 0;
    public String dataBaixa = "";
    public String dataPagamento = "";
    public String observacao = "";
    public int idBanco = 0;
    public String banco = "";
    public String agencia = "";
    public String conta = "";
    public int idTipoRecebimento = 0;
    public String tipoRecebimento = "";
    public int idLojaBaixa = 0;
    public String lojaBaixa = "";
    public boolean conciliado = false;
    public long idPagarFornecedorParcela = 0;
    public long id;

    public ReceberDevolucaoItemVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (ReceberDevolucaoItemVO) in.readObject();
    }
}