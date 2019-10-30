/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.gui.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class AtmaDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Atma";
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
                    + "pro.ID_PROD as id,\n"
                    + "pro.CD_AUXILIAR as ean,\n"
                    + "pro.BALANCA as balanca,\n"
                    + "pro.BALANCADIAS as validade,\n"
                    + "pro.DESCRICAO as descricaocompleta,\n"
                    + "pro.DESCRICAOR as descricaoreduzida,\n"
                    + "unv.SIGLA as tipoembalagem,\n"
                    + "unc.SIGLA as tipoembalagem_cotacao,\n"
                    + "ncm.CODIGO as ncm,\n"
                    + "ces.CD_CEST as cest,\n"
                    + "pro.PESO as pesobruto,\n"
                    + "pro.PESO_L peseoliquido,\n"
                    + "pro.DT_CAD as datacadastro,\n"
                    + "pre.PER_LUCRO_EFETIVO as margem,\n"
                    + "pre.VR_CUSTO_REPOSICAO as custocomimposto,\n"
                    + "pre.VR_CUSTO_AQUISICAO as custosemimposto,\n"
                    + "pre.VR_VENDA_ATUAL as precovenda,\n"
                    + "est.QTDE as estoque,\n"
                    + "est.QTDE_MAXIMA as estoquemaximo,\n"
                    + "est.QTDE_MINIMA as estoqueminimo\n"
                    + "from dbo.EQ_PROD pro\n"
                    + "left join dbo.TB_UNID unv on unv.ID_UNID = pro.ID_UNID_V\n"
                    + "left join dbo.TB_UNID unc on unc.ID_UNID = pro.ID_UNID_C\n"
                    + "left join dbo.TB_NCM ncm on ncm.ID_NCM = pro.ID_NCM\n"
                    + "left join dbo.TB_CEST as ces on ces.ID_CEST = ncm.ID_CEST\n"
                    + "left join dbo.EQ_PROD_QTDE est on est.ID_PROD = pro.ID_PROD\n"
                    + "	 and est.ID_EMP = 1\n"
                    + "	 and est.ID_TIPO_ESTOQUE = 1\n"
                    + "left join dbo.EQ_PROD_COM pre on pre.ID_PROD = pro.ID_PROD \n"
                    + "	 and pre.ID_EMP = " + getLojaOrigem()
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pes.ID_PES as id,\n"
                    + "	sit.DESCRICAO as situacaocadastro,\n"
                    + "	ent.DESCRICAO as tipo,\n"
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
                    imp.setCob_complemento(rst.getString("complementocob"));
                    imp.setCob_cep(rst.getString("cepcob"));
                    imp.setCob_bairro(rst.getString("bairrocob"));
                    imp.setCob_municipio(rst.getString("municpioCob"));
                    imp.setCob_ibge_municipio(rst.getInt("municipioIbgeCob"));
                    imp.setCob_uf(rst.getString("ufCob"));
                    imp.setCob_ibge_uf(rst.getInt("ufIbgeCob"));                    
                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ID_PROD as idproduto,\n"
                    + "	ID_PES as idfornecedor,\n"
                    + "	CODIGO as codigoexterno\n"
                    + "from dbo.EQ_PROD_REF"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
}
