/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class AtmaDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(AtmaDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "Atma";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
        }));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "ID_EMP as codigo, \n"
                    + "FANTASIA as nome \n"
                    + "from dbo.CG_EMP \n"
                    + "order by ID_EMP"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ID_DPTO as merc1,\n"
                    + "	DESCRICAO as merc1_desc\n"
                    + "from dbo.EQ_DPTO where TIPO = 'DEPTO'\n"
                    + "order by ID_DPTO"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_desc"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ID_DPTO_P as merc1,\n"
                    + "	ID_DPTO as merc2,\n"
                    + "	DESCRICAO as merc2_desc\n"
                    + "from dbo.EQ_DPTO where TIPO = 'GRUPO'\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_desc")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	(select ID_DPTO_P \n"
                    + "	   from dbo.EQ_DPTO \n"
                    + "	  where TIPO = 'GRUPO'\n"
                    + "	    and ID_DPTO = m.ID_DPTO_P\n"
                    + "	) as merc1,\n"
                    + "	m.ID_DPTO_P as merc2,\n"
                    + "	m.ID_DPTO as merc3,	\n"
                    + "	m.DESCRICAO as merc3_desc\n"
                    + "from dbo.EQ_DPTO m\n"
                    + "where TIPO = 'SUBGRUPO'\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_desc")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " pro.ID_PROD as id,\n"
                    + " pro.CD_AUXILIAR as ean,\n"
                    + " pro.BALANCA as balanca,\n"
                    + " pro.BALANCADIAS as validade,\n"
                    + " pro.DESCRICAO as descricaocompleta,\n"
                    + " pro.DESCRICAOR as descricaoreduzida,\n"
                    + " unv.SIGLA as tipoembalagem,\n"
                    + " unc.SIGLA as tipoembalagem_cotacao,\n"
                    + " pro.ID_DPTO_D as merc1,\n"
                    + " pro.ID_DPTO_G as merc2,\n"
                    + " pro.ID_DPTO_SG as merc3,"
                    + " ncm.CODIGO as ncm,\n"
                    + " ces.CD_CEST as cest,\n"
                    + " pro.PESO as pesobruto,\n"
                    + " pro.PESO_L peseoliquido,\n"
                    + " pro.DT_CAD as datacadastro,\n"
                    + " pre.per_lucro_projetado as margem,\n"
                    + " pre.VR_CUSTO_REPOSICAO as custocomimposto,\n"
                    + " pre.VR_CUSTO_AQUISICAO as custosemimposto,\n"
                    + " pre.VR_VENDA_ATUAL as precovenda,\n"
                    + " est.QTDE as estoque,\n"
                    + " est.QTDE_MAXIMA as estoquemaximo,\n"
                    + " est.QTDE_MINIMA as estoqueminimo,\n"
                    + " sit.DESCRICAO as situacaocadastro,\n"
                    + " tri.ID_GRADE_TRIB as idTrib,\n"
                    + " tri.DESCRICAO as tributacao\n"
                    + "from dbo.EQ_PROD pro\n"
                    + "left join dbo.TB_UNID unv on unv.ID_UNID = pro.ID_UNID_V\n"
                    + "left join dbo.TB_UNID unc on unc.ID_UNID = pro.ID_UNID_C\n"
                    + "left join dbo.TB_NCM ncm on ncm.ID_NCM = pro.ID_NCM\n"
                    + "left join dbo.TB_CEST as ces on ces.ID_CEST = ncm.ID_CEST\n"
                    + "left join dbo.EQ_PROD_QTDE est on est.ID_PROD = pro.ID_PROD\n"
                    + "	 and est.ID_EMP = " + getLojaOrigem() + "\n"
                    + "	 and est.ID_TIPO_ESTOQUE = 1\n"
                    + "left join dbo.EQ_PROD_COM pre on pre.ID_PROD = pro.ID_PROD \n"
                    + "	 and pre.ID_EMP = " + getLojaOrigem() + "\n"
                    + "left join dbo.TB_TIPO_SITUACAO sit on sit.ID_TIPO_SITUACAO = pre.ID_TIPO_SITUACAO\n"
                    + "left join dbo.FS_GRADE_TRIB as tri on tri.ID_GRADE_TRIB = pre.ID_GRADE_TRIB"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca("SIM".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagem_cotacao"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("peseoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setSituacaoCadastro("ATIVO".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    if ((rst.getString("tributacao") != null)
                            && (!rst.getString("tributacao").trim().isEmpty())) {

                        if (rst.getString("tributacao").contains("PIS COFINS")) {
                            imp.setPiscofinsCstDebito(1);
                            imp.setPiscofinsCstCredito(50);
                        } else if (rst.getString("tributacao").contains("ALIQUOTA ZERO")) {
                            imp.setPiscofinsCstDebito(6);
                            imp.setPiscofinsCstCredito(73);
                        } else if (rst.getString("tributacao").contains("ALIQ. ZERO")) {
                            imp.setPiscofinsCstDebito(6);
                            imp.setPiscofinsCstCredito(73);
                        } else if (rst.getString("tributacao").contains("MONO")) {
                            imp.setPiscofinsCstDebito(4);
                            imp.setPiscofinsCstCredito(70);
                        } else if (rst.getString("tributacao").contains("SUBST")) {
                            imp.setPiscofinsCstDebito(5);
                            imp.setPiscofinsCstCredito(75);
                        } else if (rst.getString("tributacao").contains("ISENTO")) {
                            imp.setPiscofinsCstDebito(7);
                            imp.setPiscofinsCstCredito(71);
                        } else {
                            imp.setPiscofinsCstDebito(7);
                            imp.setPiscofinsCstCredito(71);
                        }
                    } else {
                        imp.setPiscofinsCstDebito(7);
                        imp.setPiscofinsCstCredito(71);
                    }

                    imp.setIcmsDebitoId(rst.getString("idTrib"));
                    imp.setIcmsCreditoId(rst.getString("idTrib"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.TIPO_EMBALAGEM_PRODUTO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " a.ID_PROD as id_produto,\n"
                        + " b.FATOR as qtd,\n"
                        + " 'CX' as tipoembalagem\n"
                        + "from dbo.EQ_PROD_FATORCX a \n"
                        + "inner join dbo.TB_FATORCX b on b.ID_FATORCX = a.ID_FATORCX\n"
                        + "where b.FATOR > 1\n"
                        + "and a.OPERACAO = 'E'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtd"));
                        imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagem"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        
        if (opt == OpcaoProduto.QTD_EMBALAGEM_COTACAO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " a.ID_PROD as id_produto,\n"
                        + " b.FATOR as qtd,\n"
                        + " 'CX' as tipoembalagem\n"
                        + "from dbo.EQ_PROD_FATORCX a \n"
                        + "inner join dbo.TB_FATORCX b on b.ID_FATORCX = a.ID_FATORCX\n"
                        + "where b.FATOR > 1\n"
                        + "and a.OPERACAO = 'E'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtd"));
                        imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagem"));
                        result.add(imp);
                    }
                }
            }
            return result;            
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ean.ID_PROD as id,\n"
                    + "	ean.EAN13 as codigobarras\n"
                    + "from EQ_PROD_EAN ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pes.ID_PES as id,\n"
                    + "	sit.DESCRICAO as situacaocadastro,\n"
                    + "	ent.DESCRICAO as tipofornecedor,\n"
                    + "	pes.CPF as cpf,\n"
                    + "	pes.RG as rg,\n"
                    + "	pes.CNPJ as cnpj,\n"
                    + "	pes.INSC as inscricaoestaual,\n"
                    + "	pes.TIPO_FJ as tipoinscricao,\n"
                    + "	pes.RAZAO as razao,\n"
                    + "	pes.FANTASIA as fantasia,\n"
                    + "	ende.N_ENDERECO as endereco,\n"
                    + "	ende.N_NRO_ENDERECO as numero,\n"
                    + "	ende.N_COMPLEMENTO as complemento,\n"
                    + "	ende.N_BAIRRO as bairro,\n"
                    + "	ende.N_CEP as cep,\n"
                    + "	mun.ID_IBGE as municipioIbge,\n"
                    + "	mun.MUNICIPIO as municipio,\n"
                    + "	uf.ID_IBGE as ufIbge,\n"
                    + "	uf.UF as uf,\n"
                    + "	ende.N_FONE as telefone,\n"
                    + "	ende.N_FAX as fax,\n"
                    + "	ende.N_CELULAR as celular,\n"
                    + "	ende.C_ENDERECO as enderecocob,\n"
                    + "	ende.C_NRO_ENDERECO as numerocob,\n"
                    + "	ende.C_BAIRRO as bairrocob,\n"
                    + "	ende.C_CEP as cepcob,\n"
                    + "	munc.ID_IBGE as municipioIbgeCob,\n"
                    + "	munc.MUNICIPIO as municpioCob,\n"
                    + "	ufc.ID_IBGE as ufIbgeCob,\n"
                    + "	ufc.UF as ufCob,\n"
                    + "	ende.C_FONE as telefonecob,\n"
                    + "	ende.C_FAX as faxcob,\n"
                    + "	ende.C_CELULAR as celularcob,\n"
                    + "	ende.E_ENDERECO as enderecoent,\n"
                    + "	ende.E_NRO_ENDERECO as numeroent,\n"
                    + "	ende.E_COMPLEMENTO as complementoent,\n"
                    + "	ende.E_BAIRRO as bairroent,\n"
                    + "	ende.E_CEP as cepent,\n"
                    + "	mune.ID_IBGE as municipioIbgeEnt,\n"
                    + "	mune.MUNICIPIO as municpioEnt,\n"
                    + "	ufe.ID_IBGE as ufIbgeEnt,\n"
                    + "	ufe.UF as ufEnt,\n"
                    + "	ende.E_FONE as telefoneent,\n"
                    + "	ende.E_FAX as faxent,\n"
                    + "	ende.E_CELULAR as celularent,\n"
                    + "	ende.N_PONTO_REF as pontoreferencia,\n"
                    + "	ende.C_PONTO_REF as pontoreferenciacob,\n"
                    + "	ende.E_PONTO_REF as pontoreferenciaent,\n"
                    + "	pes.EMAIL as email,\n"
                    + "	pes.EMAIL_XML as emailXml,\n"
                    + "	pes.EMAIL_FINANCEIRO as emailfinanceiro,\n"
                    + "	pes.ESTADO_CIVIL as estadocivil,\n"
                    + "	pes.VR_SALARIO as salario,\n"
                    + "	pes.VR_RENDA as renda,\n"
                    + "	pes.DT_CAD as datacadastro,\n"
                    + "	pes.DT_NASC as datanascimento,\n"
                    + "	pes.DT_ADMISSAO as dataadmissao,\n"
                    + "	pes.DIAS_VISITA as diasvisita,\n"
                    + "	pes.ENTREGA_PRAZO as prazoentrega,\n"
                    + "	pes.TRABALHO_LOCAL as empresa,\n"
                    + "	pes.TRABALHO_FONE as telefonetrabalho,\n"
                    + "	pes.PAI as nomepai,\n"
                    + "	pes.MAE as nomemae,\n"
                    + "	pes.CARGO as cargo,\n"
                    + "	pes.SEXO as sexo,\n"
                    + "	pes.OBS as observacao\n"
                    + "from dbo.FN_PES pes\n"
                    + "left join dbo.TB_TIPO_SITUACAO sit on sit.ID_TIPO_SITUACAO = pes.ID_TIPO_SITUACAO\n"
                    + "left join dbo.TB_TIPO_ENTIDADE ent on ent.ID_TIPO_ENTIDADE = pes.ID_TIPO_ENTIDADE\n"
                    + "left join dbo.FN_PES_END ende on ende.ID_PES = pes.ID_PES\n"
                    + "left join dbo.TB_MUNICIPIO mun on mun.ID_MUNICIPIO = ende.ID_N_MUNICIPIO\n"
                    + "left join dbo.TB_UF as uf on uf.ID_UF = mun.ID_UF\n"
                    + "left join dbo.TB_MUNICIPIO munc on munc.ID_MUNICIPIO = ende.ID_C_MUNICIPIO\n"
                    + "left join dbo.TB_UF ufc on ufc.ID_UF = munc.ID_UF\n"
                    + "left join dbo.TB_MUNICIPIO mune on mune.ID_MUNICIPIO = ende.ID_C_MUNICIPIO\n"
                    + "left join dbo.TB_UF ufe on ufc.ID_UF = mune.ID_UF\n"
                    + "where pes.ID_TIPO_ENTIDADE is not null\n"
                    + "order by pes.ID_PES"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setIe_rg(rst.getString("rg"));
                    } else {
                        imp.setIe_rg(rst.getString("inscricaoestaual"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioIbge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_uf(rst.getInt("ufIbge"));
                    imp.setCob_endereco(rst.getString("enderecocob"));
                    imp.setCob_numero(rst.getString("numerocob"));
                    imp.setCob_cep(rst.getString("cepcob"));
                    imp.setCob_bairro(rst.getString("bairrocob"));
                    imp.setCob_municipio(rst.getString("municpioCob"));
                    imp.setCob_ibge_municipio(rst.getInt("municipioIbgeCob"));
                    imp.setCob_uf(rst.getString("ufCob"));
                    imp.setCob_ibge_uf(rst.getInt("ufIbgeCob"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));
                    imp.setPrazoPedido(rst.getInt("prazoentrega"));
                    imp.setPrazoVisita(rst.getInt("diasvisita"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ("ATIVO".equals(rst.getString("situacaocadastro"))) {
                        imp.setAtivo(true);
                    } else if ("INATIVO".equals(rst.getString("situacaocadastro"))) {
                        imp.setAtivo(false);
                    } else if ("BLOQUEADO".equals(rst.getString("situacaocadastro"))) {
                        imp.setBloqueado(true);
                        imp.setAtivo(true);
                    } else {
                        imp.setBloqueado(false);
                        imp.setAtivo(true);
                    }

                    if ((rst.getString("tipofornecedor") != null)
                            && (!rst.getString("tipofornecedor").trim().isEmpty())) {

                        if (rst.getString("tipofornecedor").contains("INDUSTRIA")) {
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                        } else if (rst.getString("tipofornecedor").contains("ATACADISTA\\VAREJISTA")) {
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                        } else if (rst.getString("tipofornecedor").contains("PRODUTOR RURAL")) {
                            imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                        } else {
                            imp.setTipoFornecedor(TipoFornecedor.ATACADO);
                        }
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {

                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {

                        imp.addCelular("CELULAR", rst.getString("celular"));
                    }
                    if ((rst.getString("telefonecob") != null)
                            && (!rst.getString("telefonecob").trim().isEmpty())) {

                        imp.addTelefone("TELEFONE COB", rst.getString("telefonecob"));
                    }
                    if ((rst.getString("faxcob") != null)
                            && (!rst.getString("faxcob").trim().isEmpty())) {

                        imp.addTelefone("FAX COB", rst.getString("faxcob"));
                    }
                    if ((rst.getString("celularcob") != null)
                            && (!rst.getString("celularcob").trim().isEmpty())) {

                        imp.addCelular("CELULAR COB", rst.getString("celularcob"));
                    }
                    if ((rst.getString("telefoneent") != null)
                            && (!rst.getString("telefoneent").trim().isEmpty())) {

                        imp.addTelefone("TELEFONE ENT", rst.getString("telefoneent"));
                    }
                    if ((rst.getString("faxent") != null)
                            && (!rst.getString("faxent").trim().isEmpty())) {

                        imp.addTelefone("FAX ENT", rst.getString("faxent"));
                    }
                    if ((rst.getString("celularent") != null)
                            && (!rst.getString("celularent").trim().isEmpty())) {

                        imp.addCelular("CELULAR ENT", rst.getString("celularent"));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {

                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.COMERCIAL);
                    }
                    if ((rst.getString("emailXml") != null)
                            && (!rst.getString("emailXml").trim().isEmpty())) {

                        imp.addEmail("EMAIL XML", rst.getString("emailXml").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("emailfinanceiro") != null)
                            && (!rst.getString("emailfinanceiro").trim().isEmpty())) {

                        imp.addEmail("EMAIL FINANCEIRO", rst.getString("emailfinanceiro").toLowerCase(), TipoContato.FINANCEIRO);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.ID_PROD as idproduto,\n"
                    + "	pf.ID_PES as idfornecedor,\n"
                    + "	pf.CODIGO as codigoexterno,\n"
                    + " cx2.FATOR as qtd\n"
                    + "from dbo.EQ_PROD_REF pf\n"
                    + "left join dbo.EQ_PROD_FATORCX cx on cx.ID_PROD = pf.ID_PROD\n"
                    + "inner join dbo.TB_FATORCX cx2 on cx2.ID_FATORCX = cx.ID_FATORCX\n"
                    + "and cx.OPERACAO = 'E'"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtd"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pes.ID_PES as id,\n"
                    + "	sit.DESCRICAO as situacaocadastro,\n"
                    + "	ent.DESCRICAO as tipofornecedor,\n"
                    + "	pes.CPF as cpf,\n"
                    + "	pes.RG as rg,\n"
                    + "	pes.CNPJ as cnpj,\n"
                    + "	pes.INSC as inscricaoestaual,\n"
                    + "	pes.TIPO_FJ as tipoinscricao,\n"
                    + "	pes.RAZAO as razao,\n"
                    + "	pes.FANTASIA as fantasia,\n"
                    + "	ende.N_ENDERECO as endereco,\n"
                    + "	ende.N_NRO_ENDERECO as numero,\n"
                    + "	ende.N_COMPLEMENTO as complemento,\n"
                    + "	ende.N_BAIRRO as bairro,\n"
                    + "	ende.N_CEP as cep,\n"
                    + "	mun.ID_IBGE as municipioIbge,\n"
                    + "	mun.MUNICIPIO as municipio,\n"
                    + "	uf.ID_IBGE as ufIbge,\n"
                    + "	uf.UF as uf,\n"
                    + "	ende.N_FONE as telefone,\n"
                    + "	ende.N_FAX as fax,\n"
                    + "	ende.N_CELULAR as celular,\n"
                    + "	ende.C_ENDERECO as enderecocob,\n"
                    + "	ende.C_NRO_ENDERECO as numerocob,\n"
                    + "	ende.C_BAIRRO as bairrocob,\n"
                    + "	ende.C_CEP as cepcob,\n"
                    + "	munc.ID_IBGE as municipioIbgeCob,\n"
                    + "	munc.MUNICIPIO as municpioCob,\n"
                    + "	ufc.ID_IBGE as ufIbgeCob,\n"
                    + "	ufc.UF as ufCob,\n"
                    + "	ende.C_FONE as telefonecob,\n"
                    + "	ende.C_FAX as faxcob,\n"
                    + "	ende.C_CELULAR as celularcob,\n"
                    + "	ende.E_ENDERECO as enderecoent,\n"
                    + "	ende.E_NRO_ENDERECO as numeroent,\n"
                    + "	ende.E_COMPLEMENTO as complementoent,\n"
                    + "	ende.E_BAIRRO as bairroent,\n"
                    + "	ende.E_CEP as cepent,\n"
                    + "	mune.ID_IBGE as municipioIbgeEnt,\n"
                    + "	mune.MUNICIPIO as municpioEnt,\n"
                    + "	ufe.ID_IBGE as ufIbgeEnt,\n"
                    + "	ufe.UF as ufEnt,\n"
                    + "	ende.E_FONE as telefoneent,\n"
                    + "	ende.E_FAX as faxent,\n"
                    + "	ende.E_CELULAR as celularent,\n"
                    + "	ende.N_PONTO_REF as pontoreferencia,\n"
                    + "	ende.C_PONTO_REF as pontoreferenciacob,\n"
                    + "	ende.E_PONTO_REF as pontoreferenciaent,\n"
                    + "	pes.EMAIL as email,\n"
                    + "	pes.EMAIL_XML as emailXml,\n"
                    + "	pes.EMAIL_FINANCEIRO as emailfinanceiro,\n"
                    + "	pes.ESTADO_CIVIL as estadocivil,\n"
                    + "	pes.VR_SALARIO as salario,\n"
                    + "	pes.VR_RENDA as renda,\n"
                    + "	pes.DT_CAD as datacadastro,\n"
                    + "	pes.DT_NASC as datanascimento,\n"
                    + "	pes.DT_ADMISSAO as dataadmissao,\n"
                    + "	pes.DIAS_VISITA as diasvisita,\n"
                    + "	pes.ENTREGA_PRAZO as prazoentrega,\n"
                    + "	pes.TRABALHO_LOCAL as empresa,\n"
                    + "	pes.TRABALHO_FONE as telefonetrabalho,\n"
                    + "	pes.PAI as nomepai,\n"
                    + "	pes.MAE as nomemae,\n"
                    + "	pes.CARGO as cargo,\n"
                    + "	pes.SEXO as sexo,\n"
                    + "	pes.OBS as observacao\n"
                    + "from dbo.FN_PES pes\n"
                    + "left join dbo.TB_TIPO_SITUACAO sit on sit.ID_TIPO_SITUACAO = pes.ID_TIPO_SITUACAO\n"
                    + "left join dbo.TB_TIPO_ENTIDADE ent on ent.ID_TIPO_ENTIDADE = pes.ID_TIPO_ENTIDADE\n"
                    + "left join dbo.FN_PES_END ende on ende.ID_PES = pes.ID_PES\n"
                    + "left join dbo.TB_MUNICIPIO mun on mun.ID_MUNICIPIO = ende.ID_N_MUNICIPIO\n"
                    + "left join dbo.TB_UF as uf on uf.ID_UF = mun.ID_UF\n"
                    + "left join dbo.TB_MUNICIPIO munc on munc.ID_MUNICIPIO = ende.ID_C_MUNICIPIO\n"
                    + "left join dbo.TB_UF ufc on ufc.ID_UF = munc.ID_UF\n"
                    + "left join dbo.TB_MUNICIPIO mune on mune.ID_MUNICIPIO = ende.ID_C_MUNICIPIO\n"
                    + "left join dbo.TB_UF ufe on ufc.ID_UF = mune.ID_UF\n"
                    + "where pes.ID_TIPO_ENTIDADE is null\n"
                    + "order by pes.ID_PES"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cpf"));
                    } else {
                        imp.setCnpj(rst.getString("cnpj"));
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("inscricaoestaual"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioIbge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setUfIBGE(rst.getInt("ufIbge"));
                    imp.setCobrancaEndereco(rst.getString("enderecocob"));
                    imp.setCobrancaNumero(rst.getString("numerocob"));
                    imp.setCobrancaCep(rst.getString("cepcob"));
                    imp.setCobrancaBairro(rst.getString("bairrocob"));
                    imp.setCobrancaMunicipio(rst.getString("municpioCob"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("municipioIbgeCob"));
                    imp.setCobrancaUf(rst.getString("ufCob"));
                    imp.setCobrancaUfIBGE(rst.getInt("ufIbgeCob"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEmpresaTelefone(rst.getString("telefonetrabalho"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email") == null ? "" : rst.getString("email").toLowerCase());

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {

                        if ("FEMININO".equals(rst.getString("sexo"))) {
                            imp.setSexo(TipoSexo.FEMININO);
                        } else {
                            imp.setSexo(TipoSexo.MASCULINO);
                        }
                    }

                    if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {

                        if (rst.getString("estadocivil").contains("SOLTE")) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else if (rst.getString("estadocivil").contains("CASA")) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if (rst.getString("estadocivil").contains("VIU")) {
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                        } else if (rst.getString("estadocivil").contains("DIVOR")) {
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                        } else if (rst.getString("estadocivil").contains("OUTRO")) {
                            imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        }
                    }

                    if ("ATIVO".equals(rst.getString("situacaocadastro"))) {
                        imp.setAtivo(true);
                    } else if ("INATIVO".equals(rst.getString("situacaocadastro"))) {
                        imp.setAtivo(false);
                    } else if ("BLOQUEADO".equals(rst.getString("situacaocadastro"))) {
                        imp.setBloqueado(true);
                        imp.setAtivo(true);
                    } else {
                        imp.setBloqueado(false);
                        imp.setAtivo(true);
                    }

                    if ((rst.getString("telefonecob") != null)
                            && (!rst.getString("telefonecob").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE COB",
                                rst.getString("telefonecob"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("faxcob") != null)
                            && (!rst.getString("faxcob").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAX COB",
                                rst.getString("faxcob"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("celularcob") != null)
                            && (!rst.getString("celularcob").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "CELULAR COB",
                                rst.getString("celularcob"),
                                null,
                                null
                        );
                    }

                    if ((rst.getString("telefoneent") != null)
                            && (!rst.getString("telefoneent").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "TELEFONE ENT",
                                rst.getString("telefoneent"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("faxent") != null)
                            && (!rst.getString("faxent").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "FAX ENT",
                                rst.getString("faxent"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("celularent") != null)
                            && (!rst.getString("celularent").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "CELULAR ENT",
                                rst.getString("celularent"),
                                null,
                                null
                        );
                    }

                    if ((rst.getString("emailXml") != null)
                            && (!rst.getString("emailXml").trim().isEmpty())) {
                        imp.addContato(
                                "7",
                                "EMAIL XML",
                                null,
                                null,
                                rst.getString("emailXml").toLowerCase()
                        );
                    }
                    if ((rst.getString("emailfinanceiro") != null)
                            && (!rst.getString("emailfinanceiro").trim().isEmpty())) {
                        imp.addContato(
                                "7",
                                "EMAIL FINANCEIRO",
                                null,
                                null,
                                rst.getString("emailfinanceiro").toLowerCase()
                        );
                    }

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	rec.ID_TITULO as id,\n"
                    + "	rec.ID_TIPO_SITUACAO as situacaoconta,\n"
                    + "	sit.DESCRICAO as descsituacaoconta,\n"
                    + "	rec.ID_PDV as ecf,\n"
                    + "	rec.ID_PES as idcliente,\n"
                    + "	rec.PARCELA_NRO as numeroparcela,\n"
                    + "	rec.NRO_DCTO as numerocumpom,\n"
                    + "	rec.DT_EMISSAO as dataemissao,\n"
                    + "	rec.DT_VENCIMENTO as datavenvimento,\n"
                    + "	rec.VR_TITULO as valor,\n"
                    + "	rec.VR_DESCONTO as desconto\n"
                    + "from dbo.FN_TITULO rec\n"
                    + "inner join dbo.TB_TIPO_SITUACAO sit on sit.ID_TIPO_SITUACAO = rec.ID_TIPO_SITUACAO\n"
                    + "where rec.TIPO = 'RECEBER'\n"
                    + "and sit.DESCRICAO like '%PENDENTE%'\n"
                    + "and rec.ID_EMP = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocumpom"));
                    imp.setParcela(rst.getInt("numeroparcela"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavenvimento"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	ID_GRADE_TRIB as id, \n"
                    + "	DESCRICAO as descricao \n"
                    + "from dbo.FS_GRADE_TRIB \n"
                    + "where ATIVO = 'SIM'"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"),
                            rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    
    
/* vendas */    
    private java.util.Date dataInicioVenda;
    private java.util.Date dataTerminoVenda;

    public void setDataInicioVenda(java.util.Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(java.util.Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        public VendaIterator(String idLojaCliente, java.util.Date dataInicio, java.util.Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	ven.ID_CUPOM as id,\n"
                    + "	ven.ID_EMP as idloja,\n"
                    + "	ven.NRO_ECF as ecf,\n"
                    + "	ven.ID_CLI as idcliente,\n"
                    + "	ven.DT_VENDA as datavenda,\n"
                    + "	ven.HR_VENDA as horainicio,\n"
                    + "	ven.HR_FINAL as horafinal,\n"
                    + "	ven.NRO_COO as numerocupom,\n"
                    + "	ven.NRO_SERIE as numeroserie,\n"
                    + "	ven.VR_TROCO as troco,\n"
                    + "	ven.VR_DESCONTO as desconto,\n"
                    + "	ven.VR_ACRESCIMO as acrescimo,\n"
                    + "	ven.VR_TOTAL as valortotal,\n"
                    + " ven.ADC_NOME as nomecliente,\n"
                    + " ven.CNC as cancelado\n"
                    + "from dbo.VND_CUPOM ven\n"
                    + "where ven.ID_EMP = " + idLojaCliente + "\n"
                    + "and ven.DT_VENDA between '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "and ven.CNC = 'NAO'\n"
                    + "and ven.STATUS_NFC = 'ENVIADO'";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setCancelado(false);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("datavenda"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        next.setNomeCliente(rst.getString("nomecliente"));

                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " " + rst.getString("horafinal");

                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setCancelado("SIM".equals(rst.getString("cancelado").trim()));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "II":
                    cst = 40;
                    aliq = 0;
                    break;
                case "FF":
                    cst = 60;
                    aliq = 0;
                    break;
                case "T08":
                    cst = 0;
                    aliq = 7;
                    break;
                case "T09":
                    cst = 0;
                    aliq = 9;
                    break;
                case "T07":
                    cst = 0;
                    aliq = 12;
                    break;
                case "T10":
                    cst = 0;
                    aliq = 12;
                case "T11":
                    cst = 0;
                    aliq = 17;
                    break;
                case "T12":
                    cst = 0;
                    aliq = 19;
                    break;
                case "T13":
                    cst = 0;
                    aliq = 25;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, java.util.Date dataInicio, java.util.Date dataTermino) throws Exception {
            this.sql
                    = "select  \n"
                    + "	pro.ID_CUPOM_PROD as id,\n"
                    + "	pro.ID_CUPOM as idvenda,\n"
                    + "	pro.ID_PROD as idproduto,\n"
                    + "	pro.SEQUENCIA as sequencia,\n"
                    + "	pro.HR_VENDA as horavenda,\n"
                    + "	pro.DT_VENDA as datavenda,\n"
                    + "	pro.QTDE as qtdproduto,\n"
                    + "	pro.EAN13 as codigobarras,\n"
                    + "	pro.ALIQ_TP as trib,\n"
                    + "	pro.ALIQ_TT as codtrib,\n"
                    + "	pro.PER_ICMS as aliquota,\n"
                    + "	pro.VR_VENDA as precovenda,\n"
                    + "	pro.VR_TOTAL as valortotal,\n"
                    + "	pro.VR_DESCONTO as valordesconto,\n"
                    + "	pro.VR_ACRESCIMO as valoracrescimo,\n"
                    + " pro.CNC as cancelado\n"
                    + "from dbo.VND_CUPOM_PROD pro\n"
                    + "where pro.ID_EMP = " + idLojaCliente + "\n"
                    + "and pro.DT_VENDA between '" + dataInicio + "' and '" + dataTermino + "'\n"
                    + "and pro.ID_CUPOM "
                    + "in (select ID_CUPOM "
                    + "      from VND_CUPOM "
                    + "     where ID_EMP = " + idLojaCliente + "\n"
                    + "       and DT_VENDA between '" + dataInicio + "' and '" + dataTermino + "'"
                    + "       and CNC = 'NAO'"
                    + "       and STATUS_NFC = 'ENVIADO')";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String id = rst.getString("id");
                        String idVenda = rst.getString("idvenda");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setQuantidade(rst.getDouble("qtdproduto"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("valordesconto"));
                        next.setValorAcrescimo(rst.getDouble("valoracrescimo"));
                        next.setCancelado("SIM".equals(rst.getString("cancelado").trim()));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setSequencia(rst.getInt("sequencia"));

                        String strTrib = "";
                        if ((rst.getString("codtrib") != null) &&
                                (!rst.getString("codtrib").trim().isEmpty())) {
                            strTrib = rst.getString("codtrib").trim();
                        }
                        
                        String trib = strTrib;
                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }
    }
    
}
