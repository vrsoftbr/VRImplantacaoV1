/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SambaNetV2DAO extends InterfaceDAO implements MapaTributoProvider {

    public String complemento;
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    @Override
    public String getSistema() {
        if ((complemento != null) && (!complemento.trim().isEmpty())) {
            return "SambaNet" + " - " + complemento;
        } else {
            return "SambaNet";
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODLOJA id,\n"
                    + "	descricao\n"
                  + "from\n"
                    + "	LOJA\n"
                  + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	CODALIQ as id, \n"
                    + "	coalesce(VALORTRIB, 0) as valor,\n"
                    + "	coalesce(ALIQUOTA, 0) as aliquota,\n"
                    + "	coalesce(REDUCAO, 0) as reducao,\n"
                    + "	DESCRICAO as descricao\n"
                    + "from dbo.ALIQUOTA_ICMS"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            0,
                            rst.getDouble("aliquota"),
                            rst.getDouble("reduzido")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.CODPROD as idproduto,\n"
                    + "	p.CODPROD_SAMBANET as idproduto_sambanet,\n"
                    + "	p.BARRA as ean,\n"
                    + "	p.UNIDADE as tipoembalagem,\n"
                    + "	coalesce(p.CODSETOR, 0) as balanca,\n"
                    + "	p.VALIDADE as validade,\n"
                    + "	p.CODCRECEITA as merc1,\n"
                    + "	p.CODGRUPO as merc2,\n"
                    + "	p.DESCRICAO as descricaocompleta,\n"
                    + "	p.DESC_PDV as descricaoreduzida,\n"
                    + "	p.ESTOQUE as estoque,\n"
                    + "	p.ESTOQUE_MIN as estoque_minimo,\n"
                    + "	p.ESTOQUE_MAX as estoque_maximo,\n"
                    + "	p.PRECO_CUST as custo,\n"
                    + "	p.PRECO_UNIT as precovenda,\n"
                    + "	p.PESO_BRUTO as pesobruto,\n"
                    + "	p.PESO_LIQ as pesoliquido,\n"
                    + "	p.ATIVO as situacaocadastro,\n"
                    + "	coalesce(p.DESATIVACOMPRA, 0) as descontinuado,\n"
                    + "	p.CODNCM as ncm,\n"
                    + "	p.CODCEST as cest,\n"
                    + "	p.CST_PISSAIDA,\n"
                    + "	p.CST_PISENTRADA,\n"
                    + "	p.CST_COFINSSAIDA,\n"
                    + "	p.CST_COFINSENTRADA,\n"
                    + "	p.NAT_REC as naturezareceita,\n"
                    + "	p.CODTRIB as cst,\n"
                    + "	p.CODTRIB_ENT as cst_entrada,\n"
                    + "	p.CODALIQ as aliquota,\n"
                    + "	p.CODALIQ_NF as aliquota_nf,\n"
                    + "	p.ALIQICMS_INTER as aliquota_inter,\n"
                    + "	p.ALIQICMSDESONERADO as aliquota_deso,\n"
                    + "	p.ALIQSUBTRIB as aliquota_sub,\n"
                    + "	p.ALIQUOTA_IBPT as aliquota_ibpt,\n"
                    + "	p.ALIQUOTA_IBPTEST as aliquota_ibptest,\n"
                    + "	p.ALIQUOTA_IBPTMUN as aliquota_ibptmun,\n"
                    + "	p.PER_REDUC as reducao,\n"
                    + "	p.PER_REDUC_ENT as reducao_entrada\n"
                    + "from PRODUTOS p\n"
                    + "order by CODPROD"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto_sambanet"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescontinuado(rst.getInt("descontinuado") != 0);
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_PISSAIDA"));
                    imp.setPiscofinsCstCredito(rst.getString("CST_PISENTRADA"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsCreditoId(rst.getString("aliquota"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODPROD, "
                    + "BARRA "
                    + "from ALTERNATIVO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD"));
                    imp.setEan(rst.getString("BARRA"));
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
                    "select  \n"
                    + "	c.CODCLIE as id,\n"
                    + "	c.RAZAO as razao,\n"
                    + "	c.CNPJ_CPF as cnpj,\n"
                    + "	c.IE as inscricaoestadual,\n"
                    + "	c.INSCRITOMUNICIPIO as inscricaomunicipal,\n"
                    + "	c.RG as rg,\n"
                    + "	c.FANTASIA as fantasia,\n"
                    + "	c.ENDERECO as endereco,\n"
                    + "	c.BAIRRO as bairro,\n"
                    + "	c.CIDADE as municipio,\n"
                    + "	c.ESTADO as uf,\n"
                    + "	c.NUMERO as numero,\n"
                    + "	c.COMPLEMENTO as complemento,\n"
                    + " c.CEP as cep,\n"
                    + "	c.END_COB as endereco_cobranca,\n" + "	c.BAIRRO_COB as bairro_cobranca,\n"
                    + "	c.CID_COB as municipio_cobranca,\n"
                    + "	c.EST_COB as uf_cobranca,\n"
                    + "	c.NUM_COB as numero_cobranca,\n"
                    + "	c.COMP_COB as complemento_cobranca,\n"
                    + " c.CEP_COB as cep_cobranca,\n"
                    + "	c.TELEFONE as telefone,\n"
                    + "	c.FAX as fax,\n"
                    + "	c.FONE1 as telefone1,\n"
                    + "	c.FONE2 as telefone2,\n"
                    + "	c.FONE_EMP as telefone_empresa,\n"
                    + "	c.EMAIL as email,\n"
                    + "	c.EMAILNFE as emailNF,\n"
                    + "	c.CELULAR as celular,\n"
                    + "	c.DTCAD as datacadastro,\n"
                    + "	c.OBS1 as observacao1,\n"
                    + "	c.OBS2 as observacao2,\n"
                    + "	c.DTANIVER as datanascimento,\n"
                    + "	c.LIMITECRED as valorlimite,\n"
                    + "	c.EMPRESA as empresa,\n"
                    + "	c.CARGO as funcao,\n"
                    + "	c.RENDA as salario,\n"
                    + "	c.ATIVO as situacaocadastro,\n"
                    + "	c.NOMECONJUGE as nomeconjuge,\n"
                    + "	c.NOMEPAI as nomepai,\n"
                    + "	c.NOMEMAE as nomemae,\n"
                    + "	c.CPF_CONJUGE as cpf_conjuge,\n"
                    + "	c.RG_CONJUGE as rg_conjuge\n"
                    + "from CLIENTES c\n"
                    + "order by CODCLIE"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("funcao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setAtivo("S".equals(rst.getString("situacaocadastro")));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setCpfConjuge(rst.getString("cpf_conjuge"));
                    imp.setObservacao(rst.getString("observacao1"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    result.add(imp);
                }
            }
        }

        return result;
    }
}
