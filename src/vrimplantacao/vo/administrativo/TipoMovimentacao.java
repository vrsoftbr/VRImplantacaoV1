package vrimplantacao.vo.administrativo;

public enum TipoMovimentacao {

    ACERTO(1),
    TRANSFERENCIA(2),
    DEVOLUCAO(3),
    VENDA(4),
    ENTRADA(5),
    ESTORNO_TRANSFERENCIA(6),
    ESTORNO_DEVOLUCAO(7),
    ESTORNO_VENDA(8),
    ESTORNO_ENTRADA(9),
    ACERTO_NOTA_ENTRADA(10),
    CONSUMO(11),
    BALANCO_ZERA_ESTOQUE(12),
    IMPORTACAO_BALANCO(13),
    PERDA_ENTRADA(14),
    IMPORTACAO_PRODUTO(15),
    ESTORNO_PERDA(16),
    ACERTO_ENTRADA_PERDA(17),
    ENTRADA_TROCA(18),
    ACERTO_INVENTARIO(19),
    PERDA(20),
    ZERA_ESTOQUE(21),
    SAIDA_TROCA(22),
    PRODUCAO(23),
    ESTORNO_PRODUCAO(24),
    TRANSFERENCIA_INTERNA(25),
    ESTORNO_PERDA_ENTRADA(26),
    TRANSFORMADO(27),
    ESTORNO_TRANSFORMADO(28),
    QUEBRA(29),
    ENTRADA_CESTA_BASICA(30),
    SAIDA_CESTA_BASICA(31),
    PERDA_VENDA(32),
    SAIDA(33),
    PERDA_NATURAL(34);
    private int id = 0;

    private TipoMovimentacao(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}