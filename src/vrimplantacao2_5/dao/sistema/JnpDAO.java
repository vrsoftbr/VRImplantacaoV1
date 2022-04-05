/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2_5.vo.sistema.JnpVO;

/**
 *
 * @author Michael
 */
public class JnpDAO extends InterfaceDAO implements MapaTributoProvider {

    public JnpVO jnpVO = null;
    private final String SISTEMA = "JNP";

    @Override
    public String getSistema() {
        return SISTEMA;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	s.CNPJ cnpj,\n"
                    + "	s.RAZAOSOCIAL razao \n"
                    + "FROM\n"
                    + "	SUP999 s "
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("razao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	s.FANTASIA descricao\n"
                    + "FROM\n"
                    + "	SUP999 s "
            )) {
                while (rst.next()) {
                    result.add(rst.getString("descricao"));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	s.SUP002 id,\n"
                    + "	s.SIGLA, \n"
                    + "	s.DESCRICAO DESCRICAO\n"
                    + "FROM\n"
                    + "	SUP002 s"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	s3.SUP004 m1,\n"
                    + "	s3.DESCRICAO m1desc,\n"
                    + "	s2.SUP005 m2,\n"
                    + "	s2.DESCRICAO m2desc,\n"
                    + "	s.SUP006 m3,\n"
                    + "	s.DESCRICAO m3desc\n"
                    + "FROM \n"
                    + "	SUP006 s \n"
                    + "JOIN SUP005 s2 ON s.SUP005 = s2.SUP005 \n"
                    + "JOIN SUP004 s3 ON s2.SUP004 = s3.SUP004 \n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	p.SUP001 id,\n"
                    + "	p.DESCRICAO descricaoCompleta,\n"
                    + "	p.DESCRICAO_REDUZIDA descricaoReduzida,\n"
                    + "	p.EAN ean,\n"
                    + "	uni.DESCRICAO unidade,\n"
                    + "	p.SUP010 cod_fornecedor,\n"
                    + "	p.SUP004 m1id,\n"
                    + "	p.SUP005 m2id,\n"
                    + "	p.SUP006 m3id,\n"
                    + "	vlr.CUSTO custo,\n"
                    + "	vlr.PROMOCAO desconto,\n"
                    + "	vlr.VENDA valor,\n"
                    + "	vlr.MARGEM margem,\n"
                    + "	vlr.SALDO estoque,\n"
                    + "	vlr.SALDO_MINIMO estoque_minimo,\n"
                    + "	vlr.SALDO_MAXIMO estoque_maximo,\n"
                    + "	p.SUP002 id_icms,\n"
                    + "	p.SITUACAOTRIB,\n"
                    + "	CASE \n"
                    + "	WHEN p.ATIVO = 'S'\n"
                    + "	THEN 1\n"
                    + "	ELSE 0\n"
                    + "	END ativo,\n"
                    + "	t.PERCENTUAL icms,\n"
                    + "	p.NCM numero_ncm,\n"
                    + "	p.CODIGO_CEST cest,\n"
                    + "	pis.PERPIS pis,\n"
                    + "	pis.PERPIS aliq_pis,\n"
                    + "	al.CODIGO cofins,\n"
                    + "	pis.PERCOFINS aliq_cofins,\n"
                    + "	al.CODIGO CST_PIS_ENTRADA,\n"
                    + "	al.CODIGO CST_COFINS_ENTRADA\n"
                    + "FROM\n"
                    + "	SUP001 p\n"
                    + "LEFT JOIN SUP009 uni ON\n"
                    + "	uni.SUP009 = p.SUP009_VENDA\n"
                    + "LEFT JOIN SUP008 vlr ON\n"
                    + "	vlr.SUP001 = p.SUP001 \n"
                    + "LEFT JOIN SUP098 al ON\n"
                    + "	al.SUP098 = p.SUP098 \n"
                    + "LEFT JOIN SUP002 t ON\n"
                    + "	t.SUP002 = p.SUP002 \n"
                    + "LEFT JOIN SUP090 pis ON\n"
                    + "	pis.SUP090 = p.SUP090"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    imp.setCodMercadologico1(rs.getString("m1id"));
                    imp.setCodMercadologico2(rs.getString("m2id"));
                    imp.setCodMercadologico3(rs.getString("m3id"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setFornecedorFabricante(rs.getString("cod_fornecedor"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setPrecovenda(rs.getDouble("valor"));

                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setNcm(rs.getString("numero_ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsDebitoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms"));
                    imp.setIcmsCreditoId(rs.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms"));
                    imp.setIcmsConsumidorId(rs.getString("id_icms"));

                    imp.setIcmsAliqEntrada(rs.getDouble("icms"));

                    imp.setPiscofinsCstDebito(rs.getInt("CST_PIS_ENTRADA"));
                    imp.setPiscofinsCstCredito(rs.getInt("CST_PIS_ENTRADA"));

                    int codigoProduto = Utils.stringToInt(rs.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT \n"
                    + "  s.SUP001 id,\n"
                    + "  s.EAN ean,\n"
                    + "  uni.DESCRICAO unidade\n"
                    + " FROM SUP001 s \n"
                    + " LEFT JOIN SUP009 uni ON\n"
                    + "	uni.SUP009 = s.SUP009_VENDA\n"
                    + " WHERE s.EAN <> ''\n"
                    + " AND s.EAN IS NOT NULL "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	c.SUP024 id,\n"
                    + "	c.RAZAOSOCIAL nome,\n"
                    + "	c.ENDERECO endereco,\n"
                    + "	c.NUMERO numero,\n"
                    + "	c.BAIRRO bairro,\n"
                    + "	c.CEP cep,\n"
                    + "	s.NOME municipio,\n"
                    + "	s.UF uf,\n"
                    + "	c.CNPJ_CPF cpfcnpj,\n"
                    + "	c.INSCRICAO inscestrg,\n"
                    + "	c.TELEFONE1 telefone,\n"
                    + "	c.CELULAR celular,\n"
                    + "	c.OBSERVACAO obs,\n"
                    + "	c.DTCADASTRO dtcadastro,\n"
                    + "	c.DTNASCIMENTO dtnasc,\n"
                    + "	c.ESTADO_CIVIL estadoCivil,\n"
                    + "	c.COMPLEMENTO complemento,\n"
                    + "	c.CONJUJE nomeconjuge,\n"
                    + "	c.NOME_PAI nomepai,\n"
                    + "	c.NOME_MAE nomemae,\n"
                    + "	CASE\n"
                    + "		WHEN c.ATIVO LIKE 'N'\n"
                    + " 	THEN 0\n"
                    + "		ELSE 1\n"
                    + "	END ativo,\n"
                    + "	c.DTALTERACAO \n"
                    + "FROM\n"
                    + "	SUP024 c\n"
                    + "JOIN SUP118 s ON s.SUP118 = c.SUP118 "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscestrg"));
                    imp.setComplemento(rst.getString("complemento"));

                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setEstadoCivil(rst.getString("estadoCivil"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	r.SUP026 id,\n"
                    + "	r.SUP025 idcliente,\n"
                    + "	r.SUP999 empresa,\n"
                    + "	r.CUPOM numerodocumento,\n"
                    + "	r.PDV ecf,\n"
                    + "	r.DATA dataemissao,\n"
                    + "	r.VALOR valor,\n"
                    + "	r.DATAVENC datavencimento\n"
                    + "FROM\n"
                    + "	SUP026 r\n"
                    + "WHERE\n"
                    + "	r.PENDENTE NOT LIKE 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	vsc.CONVENIO id,\n"
                    + "	vsc.NOME_CONVENIADO nome,\n"
                    + "	vsc.SITUACAO bloqueado,\n"
                    + "	s.CPF cnpj,\n"
                    + "	vsc.SITUACAO status, \n"
                    + "	vsc.LIMITE convenioLimite\n"
                    + "FROM\n"
                    + "	VW_SOCIM_CONVENIADOS vsc\n"
                    + "	JOIN SUP025 s ON vsc.CODIGO_CLIENTE = s.SUP025 "
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("nome"));
                    imp.setIdEmpresa(rst.getString("id_empresa"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setSituacaoCadastro("0".equals(rst.getString("status")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setConvenioLimite(rst.getDouble("convenioLimite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
