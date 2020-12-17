package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class CorrecaoImpostosSuperControle_SuperServerDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        return "SuperServer" + ("".equals(complemento) ? "" : " - " + complemento);
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_CONSUMIDOR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id, \n"
                    + "(descricaoLoja + ' - ' + cnpjLoja) as descricao\n"
                    + "from MultiLoja.Loja\n"
                    + "where fkCliente = 1\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("id"), rst.getString("descricao"))
                    );
                }
            }
        }
        return result;
    }    
    
    /* O TRECHO ABAIXO FOI DESENVOLVIDO PARA FAZER O ACERTO FISCAL DOS PRODUTOS, FOI FORNECIDO UMA PLANILHA POR PARTE DO CLIENTE 
        ESSA PLANILHA SALVEI NO BANCO DE DADOS 
    
    ASS.: LUCAS SANTOS
    
    */
    
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.NCM) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, \n"
                        + "ncm \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setNcm(rst.getString("ncm"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.CEST) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cest \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setCest(rst.getString("cest"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.PIS_COFINS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cstpiscofinsentrada, \n"
                        + "cstpiscofinssaida \n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                        imp.setPiscofinsCstDebito(rst.getInt("cstpiscofinssaida"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.NATUREZA_RECEITA) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cstpiscofinsentrada, \n"
                        + "cstpiscofinssaida, \n"
                        + "naturezadereceita\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                        imp.setPiscofinsCstDebito(rst.getInt("cstpiscofinssaida"));
                        imp.setPiscofinsNaturezaReceita(rst.getString("naturezadereceita"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmsentrada, \n"
                        + "reducaoentrada, \n"
                        + "icmssaida, \n"
                        + "reducaosaida \n"        
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));

                        /* icms débito */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) == 0)) {
                            imp.setIcmsCstSaida(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) > 0)) {
                            imp.setIcmsCstSaida(20);
                        } else {
                            imp.setIcmsCstSaida(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqSaida(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaida(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));

                        /* icms débito fora estado */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) == 0)) {
                            imp.setIcmsCstSaidaForaEstado(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) > 0)) {
                            imp.setIcmsCstSaidaForaEstado(20);
                        } else {
                            imp.setIcmsCstSaidaForaEstado(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqSaidaForaEstado(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstado(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));

                        /* icms débito fora estado nf */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) == 0)) {
                            imp.setIcmsCstSaidaForaEstadoNF(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) > 0)) {
                            imp.setIcmsCstSaidaForaEstadoNF(20);
                        } else {
                            imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqSaidaForaEstadoNF(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstadoNF(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));

                        /* icms crédito */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")) == 0)) {
                            imp.setIcmsCstEntrada(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")) > 0)) {
                            imp.setIcmsCstEntrada(20);
                        } else {
                            imp.setIcmsCstEntrada(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqEntrada(Double.parseDouble(rst.getString("icmsentrada").replace(",", ".")));
                        imp.setIcmsReducaoEntrada(Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")));
                        
                        /* icms crédito fora estado */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")) == 0)) {
                            imp.setIcmsCstEntradaForaEstado(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")) > 0)) {
                            imp.setIcmsCstEntradaForaEstado(20);
                        } else {
                            imp.setIcmsCstEntradaForaEstado(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqEntradaForaEstado(Double.parseDouble(rst.getString("icmsentrada").replace(",", ".")));
                        imp.setIcmsReducaoEntradaForaEstado(Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")));                        
                        
                        /* icms consumidor */
                        if ((rst.getInt("cst") == 20) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) == 0)) {
                            imp.setIcmsCstConsumidor(0);
                        } else if ((rst.getInt("cst") == 0) && (Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")) > 0)) {
                            imp.setIcmsCstConsumidor(20);
                        } else {
                            imp.setIcmsCstConsumidor(rst.getInt("cst"));
                        }
                        imp.setIcmsAliqConsumidor(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoConsumidor(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        if (opt == OpcaoProduto.ICMS_SAIDA) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmssaida, \n"
                        + "reducaosaida\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstSaida(rst.getInt("cst"));
                        imp.setIcmsAliqSaida(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));
                        imp.setIcmsReducaoSaida(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));
                        
                        imp.setIcmsCstSaidaForaEstado(rst.getInt("cst"));
                        imp.setIcmsAliqSaidaForaEstado(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstado(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst"));
                        imp.setIcmsAliqSaidaForaEstadoNF(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstadoNF(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        imp.setIcmsCstConsumidor(rst.getInt("cst"));
                        imp.setIcmsAliqConsumidor(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoConsumidor(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_SAIDA_FORA_ESTADO) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmssaida, \n"
                        + "reducaosaida\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstSaidaForaEstado(rst.getInt("cst"));
                        imp.setIcmsAliqSaidaForaEstado(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstado(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_SAIDA_NF) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmssaida, \n"
                        + "reducaosaida\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst"));
                        imp.setIcmsAliqSaidaForaEstadoNF(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoSaidaForaEstadoNF(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_CONSUMIDOR) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmssaida, \n"
                        + "reducaosaida\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstConsumidor(rst.getInt("cst"));
                        imp.setIcmsAliqConsumidor(Double.parseDouble(rst.getString("icmssaida").replace(",", ".")));
                        imp.setIcmsReducaoConsumidor(Double.parseDouble(rst.getString("reducaosaida").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_ENTRADA) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmsentrada, \n"
                        + "reducaoentrada\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstEntrada(rst.getInt("cst"));
                        imp.setIcmsAliqEntrada(Double.parseDouble(rst.getString("icmsentrada").replace(",", ".")));
                        imp.setIcmsReducaoEntrada(Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")));
                        
                        imp.setIcmsCstEntradaForaEstado(rst.getInt("cst"));
                        imp.setIcmsAliqEntradaForaEstado(Double.parseDouble(rst.getString("icmsentrada").replace(",", ".")));
                        imp.setIcmsReducaoEntradaForaEstado(Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "barras, "
                        + "cst, \n"
                        + "icmsentrada, \n"
                        + "reducaoentrada\n"
                        + "from implantacao.produtoscorrecaofiscal "
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("barras"));
                        
                        imp.setIcmsCstEntradaForaEstado(rst.getInt("cst"));
                        imp.setIcmsAliqEntradaForaEstado(Double.parseDouble(rst.getString("icmsentrada").replace(",", ".")));
                        imp.setIcmsReducaoEntradaForaEstado(Double.parseDouble(rst.getString("reducaoentrada").replace(",", ".")));                        
                        
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        
        return null;
    }
    
}
