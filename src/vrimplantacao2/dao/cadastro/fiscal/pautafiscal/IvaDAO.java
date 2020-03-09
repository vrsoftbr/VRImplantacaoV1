package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;

public class IvaDAO {

    private Map<Integer, Integer> empresaUf;
    private int getUfEmpresa(int idLoja) throws Exception {
        if (empresaUf == null) {
            try (Statement st = Conexao.createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	e.id,\n" +
                        "	f.id_estado\n" +
                        "from\n" +
                        "	loja e\n" +
                        "	join fornecedor f on\n" +
                        "		e.id_fornecedor = f.id"
                )) {
                    empresaUf = new HashMap<>();
                    while (rs.next()) {
                        empresaUf.put(
                                rs.getInt("id"),
                                rs.getInt("id_estado")
                        );
                    }
                }
            }
        }
        return empresaUf.get(idLoja);
    }
    
    public IvaVO calcular(int idLoja, int idProduto, double custoComImposto, double valorIpi, int idEstadoDestino) throws Exception {
        IvaVO oIva = new IvaVO();
        oIva.idAliquotaCredito = -1;
        int sessaoUf = getUfEmpresa(idLoja);

        String sql = "select\n" +
            "	pauta.tipoiva,\n" +
            "	pauta.iva,\n" +
            "	pauta.ivaajustado,\n" +
            "	aliquotacredito.porcentagem as porcentagemcredito,\n" +
            "	aliquotacredito.reduzido as reduzidocredito,\n" +
            "	aliquotadebito.porcentagem as porcentagemdebito,\n" +
            "	aliquotadebito.reduzido as reduzidodebito,\n" +
            "	pauta.id_aliquotacredito,\n" +
            "	pauta.id_aliquotadebitoforaestado,\n" +
            "	pauta.icmsrecolhidoantecipadamente,\n" +
            "	pauta.id_aliquotadebito,\n" +
            "	aliquotaforaestado.porcentagem as porcentagemforaestado,\n" +
            "	aliquotaforaestado.reduzido as reduzidoforaestado,\n" +
            "	aliquotaforaestado.porcentagemfinal as porcentagemfinalforaestado,\n" +
            "	aliquotacredito.porcentagemfinal as porcentagemfinalcredito\n" +
            "from\n" +
            "	produto\n" +
            "inner join produtoaliquota as pa on\n" +
            "	pa.id_produto = produto.id\n" +
            "	and pa.id_estado = " + sessaoUf + "\n" +
            "inner join pautafiscal as pauta on\n" +
            "	pauta.ncm1 = produto.ncm1\n" +
            "	and pauta.ncm2 = produto.ncm2\n" +
            "	and pauta.ncm3 = produto.ncm3\n" +
            "	and pauta.excecao = pa.excecao\n" +
            "	and pauta.id_estado = " + sessaoUf + "\n" +
            "inner join aliquota as aliquotacredito on\n" +
            "	aliquotacredito.id = pauta.id_aliquotacredito\n" +
            "inner join aliquota as aliquotadebito on\n" +
            "	aliquotadebito.id = pauta.id_aliquotadebito\n" +
            "inner join aliquota as aliquotaforaestado on\n" +
            "	aliquotaforaestado.id = pauta.id_aliquotadebitoforaestado\n" +
            "where\n" +
            "	produto.id = " + idProduto;
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                if (!rst.next()) {
                    return oIva;
                } else {
                    oIva.tipoIva = rst.getInt("tipoiva");

                    if (idEstadoDestino == sessaoUf) {
                        oIva.idAliquotaCredito = rst.getInt("id_aliquotacredito");
                    } else {
                        oIva.idAliquotaCredito = rst.getInt("id_aliquotadebitoforaestado");
                    }

                    oIva.idAliquotaDebito = rst.getInt("id_aliquotadebito");

                    if (idEstadoDestino == sessaoUf) {
                        oIva.iva = rst.getDouble("iva");
                    } else {
                        oIva.iva = rst.getDouble("ivaajustado");
                    }

                    if (!rst.getBoolean("icmsrecolhidoantecipadamente")) {
                        if (rst.getInt("tipoiva") == TipoPercentualValor.PERCENTUAL.getId()) {
                            oIva.valorBaseCalculo = (custoComImposto * (1 + (oIva.iva / 100))) * (1 - (rst.getDouble("reduzidodebito") / 100));

                        } else if (rst.getInt("tipoiva") == TipoPercentualValor.VALOR.getId()) {
                            oIva.valorBaseCalculo = oIva.iva;
                        }

                        double valorIcmsDebito = oIva.valorBaseCalculo * (rst.getDouble("porcentagemdebito") / 100);
                        double valorIcmsCredito = 0;

                        if (idEstadoDestino == sessaoUf || rst.getDouble("porcentagemfinalcredito") < rst.getDouble("porcentagemfinalforaestado")) {
                            valorIcmsCredito = (custoComImposto - valorIpi) * (1 - (rst.getDouble("reduzidocredito") / 100)) * (rst.getDouble("porcentagemcredito") / 100);

                        } else {
                            valorIcmsCredito = (custoComImposto - valorIpi) * (1 - (rst.getDouble("reduzidoforaestado") / 100)) * (rst.getDouble("porcentagemforaestado") / 100);
                        }

                        oIva.valorIcms = valorIcmsDebito - valorIcmsCredito;

                    } else {
                        if (rst.getInt("tipoiva") == TipoPercentualValor.PERCENTUAL.getId()) {
                            oIva.valorBaseCalculo = (custoComImposto) * (1 - (rst.getDouble("reduzidodebito") / 100));

                        } else if (rst.getInt("tipoiva") == TipoPercentualValor.VALOR.getId()) {
                            oIva.valorBaseCalculo = oIva.iva;
                        }

                        oIva.valorIcms = oIva.valorBaseCalculo * (rst.getDouble("porcentagemdebito") / 100);
                    }

                    if (oIva.valorIcms < 0) {
                        oIva.valorIcms = 0;
                    }

                    stm.close();

                    return oIva;
                }
            }
        }
    }
    
}
