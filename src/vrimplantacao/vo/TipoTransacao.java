package vrimplantacao.vo;

public enum TipoTransacao {

    INCLUSAO(0),
    ALTERACAO(1),
    EXCLUSAO(2),
    ATUALIZACAO(3),
    PAGAMENTO(4),
    ESTORNO(5),
    CANCELAMENTO(6),
    FINALIZACAO(7),
    RECEBIMENTO(8),
    EXPORTACAO(9),
    BAIXA(10),
    INICIO_PROCESSO(11),
    IMPORTACAO(12),
    IMPRESSAO(13),
    INUTILIZACAO(14),
    GERACAO(15),
    BLOQUEIO(16),
    PARAMETRIZACAO(17),
    CONFIGURACAO(18),
    CONFERENCIA(19),
    ENVIO_CARGA(20),
    TRANSMISSAO(21),
    REPROCESSO(22);
    private int id = 0;

    private TipoTransacao(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}
