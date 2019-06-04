package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;

/**
 * Representa um registro da tabela public.pagarfornecedor.
 * @author Leandro
 */
public class PagarFornecedorVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('pagarfornecedor_id_seq'::regclass),
    private int id_loja;// integer NOT NULL,
    private int id_fornecedor;// integer NOT NULL,
    private int id_tipoentrada = 0;// integer NOT NULL,
    private int numerodocumento;// integer NOT NULL,
    private Date dataentrada;// date NOT NULL,
    private Date dataemissao;// date NOT NULL,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private long id_notadespesa = -1;// bigint,
    private long id_notaentrada = -1;// bigint,
    private long id_transferenciaentrada = -1;// bigint,
    private long id_pagaroutrasdespesas = -1;// bigint,
    private int id_geracaoretencaotributo = -1;// integer,
    private int id_escritasaldo = -1;// integer,

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(int id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public int getId_tipoentrada() {
        return id_tipoentrada;
    }

    public void setId_tipoentrada(int id_tipoentrada) {
        this.id_tipoentrada = id_tipoentrada;
    }

    public int getNumerodocumento() {
        return numerodocumento;
    }

    public void setNumerodocumento(int numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    public Date getDataentrada() {
        return dataentrada;
    }

    public void setDataentrada(Date dataentrada) {
        this.dataentrada = dataentrada;
    }

    public Date getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public long getId_notadespesa() {
        return id_notadespesa;
    }

    public void setId_notadespesa(long id_notadespesa) {
        this.id_notadespesa = id_notadespesa;
    }

    public long getId_notaentrada() {
        return id_notaentrada;
    }

    public void setId_notaentrada(long id_notaentrada) {
        this.id_notaentrada = id_notaentrada;
    }

    public long getId_transferenciaentrada() {
        return id_transferenciaentrada;
    }

    public void setId_transferenciaentrada(long id_transferenciaentrada) {
        this.id_transferenciaentrada = id_transferenciaentrada;
    }

    public long getId_pagaroutrasdespesas() {
        return id_pagaroutrasdespesas;
    }

    public void setId_pagaroutrasdespesas(long id_pagaroutrasdespesas) {
        this.id_pagaroutrasdespesas = id_pagaroutrasdespesas;
    }

    public int getId_geracaoretencaotributo() {
        return id_geracaoretencaotributo;
    }

    public void setId_geracaoretencaotributo(int id_geracaoretencaotributo) {
        this.id_geracaoretencaotributo = id_geracaoretencaotributo;
    }

    public int getId_escritasaldo() {
        return id_escritasaldo;
    }

    public void setId_escritasaldo(int id_escritasaldo) {
        this.id_escritasaldo = id_escritasaldo;
    }
    
}
