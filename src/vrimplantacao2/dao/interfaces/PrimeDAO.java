package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class PrimeDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Prime";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.FORCAR_ATUALIZACAO,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    'S-'||clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    'E-'||clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'E'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }

        }
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    empr_codigo as id,\n"
                    + "    empr_nomereduzido as nome,\n"
                    + "    empr_cnpjcpf as cnpj\n"
                    + "from empresas order by 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("id"),
                                    rst.getString("nome") + "-" + rst.getString("cnpj")
                            )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    p.cadp_codigo as id,\n"
                    + "    p.cadp_balanca as balanca,\n"
                    + "    p.cadp_codigobarra as ean,\n"
                    + "    p.cadp_descricaounmedida as tipoembalagem,\n"
                    + "    p.cadp_situacao as situacaocadastro,\n"
                    + "    p.cadp_descricao as descricaocompleta,\n"
                    + "    p.cadp_descricaoreduzida as descricaoreduzida,\n"
                    + "    p.cadp_codcategoria,\n"
                    + "    p.cadp_categoria,\n"
                    + "    p.cadp_dtcadastro as datacadastro,\n"
                    + "    p.cadp_dtalteracao as dataalteracao,\n"
                    + "    p.cadp_codigoncm as ncm,\n"
                    + "    p.cadp_cest as cest,\n"
                    + "    p.cadp_cstpise as cstpisentrada,\n"
                    + "    p.cadp_cstpiss as cstpissaida,\n"
                    + "    'E-'||pe.cade_codclassificacaoe as icmsentrada,\n"
                    + "    cle.clat_cst as csticmsentrada,\n"
                    + "    cle.clat_icms as aliqicmsentrada,\n"
                    + "    cle.clat_redbcicms as redicmsentrada,\n"
                    + "    'S-'||pe.cade_codclassificacaos as icmssaida,\n"
                    + "    cls.clat_cst as csticmsentrada,\n"
                    + "    cls.clat_icms as aliqicmsentrada,\n"
                    + "    cls.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_estmin as estoqueminimo,\n"
                    + "    pe.cade_estmax as estoquemaximo,\n"
                    + "    pe.cade_qemb as qtdembalagem, \n"
                    + "    pe.cade_margemcontrib as margem,\n"
                    + "    case \n"
                    + "        pe.cade_oferta \n"
                    + "	       when 'S' \n"
                    + "	       then cade_prnormal\n"
                    + "    else cade_prvenda end precovenda,\n"
                    + "    pe.cade_ctnota as custo,\n"
                    + "    pe.cade_estoque2 as estoque \n"
                    + "from cadprod p\n"
                    + "left join cadprodemp pe on pe.cade_codigo = p.cadp_codigo\n"
                    + "	and pe.cade_codempresa = '" + getLojaOrigem() + "'\n"
                    + "left join classtrib cle on cle.clat_codsimb = pe.cade_codclassificacaoe\n"
                    + "	and cle.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cle.clat_es = 'E'\n"
                    + "left join classtrib cls on cls.clat_codsimb = pe.cade_codclassificacaos\n"
                    + "	and cls.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cls.clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(!"N".equals(rst.getString("balanca")));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpissaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisentrada"));

                    imp.setIcmsDebitoId(rst.getString("icmssaida"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());

                    imp.setIcmsCreditoId(rst.getString("icmsentrada"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    codb_codprod as idproduto,\n"
                    + "    codb_codbarra as ean\n"
                    + "from codigosbarra\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.enti_codigo as id,\n"
                    + "	f.enti_razaosocial as razao,\n"
                    + "	f.enti_nome as fantasia,\n"
                    + "	f.enti_cnpjcpf as cnpj,\n"
                    + "	f.enti_inscricaoestadual as ie,\n"
                    + "	f.enti_inscricaomunicipal as im,\n"
                    + "	f.enti_fj as tipopessoa,\n"
                    + "	f.enti_endereco as endereco,\n"
                    + "	f.enti_numero as numero,\n"
                    + "	f.enti_complemento as complemento,\n"
                    + "	f.enti_bairro as bairro,\n"
                    + "	f.enti_municipio as municipio,\n"
                    + "	m.muni_ibge as municipioibge,\n"
                    + "	m.muni_nome as descricaomunicipio,\n"
                    + "	m.muni_uf as descricaouf,\n"
                    + "	f.enti_uf as uf,\n"
                    + "	f.enti_cep as cep,\n"
                    + "	f.enti_fone as telefone,\n"
                    + "	f.enti_email as email,\n"
                    + "	f.enti_fax as fax,\n"
                    + "	f.enti_celular as celular,\n"
                    + "	f.enti_datacadastro as datacadastro\n"
                    + "from entidades f \n"
                    + "left join municipios m on m.muni_codigo = f.enti_codmunicipio\n"
                    + "where enti_tipo like '%F%'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setInsc_municipal(rst.getString("im"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "    (p.pare_protocolo||'-'||pare_chave) as id,\n"
                    + "    p.pare_dtmvto as datamovimento,\n"
                    + "    p.pare_dtemissao as dataemissao,\n"
                    + "    p.pare_dtvcto as datavnecimento,\n"
                    + "    p.pare_parcela as numeroparcela,\n"
                    + "    p.pare_dcto as numerodocumento,\n"
                    + "    p.pare_valor as valor,\n"
                    + "    p.pare_desconto as desconto,\n"
                    + "    p.pare_juros as juros,\n"
                    + "    p.pare_abatimentos as abatimentos,\n"
                    + "    p.pare_acrescimos as acrescimos,\n"
                    + "    p.pare_multa as multa,\n"
                    + "    p.pare_parcelas as totalparcelas,\n"
                    + "    p.pare_codentidade as idfornecedor,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    p.pare_obs as observacao,\n"
                    + "    p.pare_complemento as complementoobs\n"
                    + "from pagrec p\n"
                    + "join entidades f on f.enti_codigo = p.pare_codentidade\n"
                    + "where p.pare_pr = 'P'\n"
                    + "and p.pare_dtbaixa is null\n"
                    + "and p.pare_codempresa = '" + getLojaOrigem() + "'\n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEntrada(rst.getDate("datamovimento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));

                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("datavnecimento"), imp.getValor());
                    parc.setNumeroParcela(rst.getInt("numeroparcela"));
                    parc.setObservacao(imp.getObservacao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.enti_codigo as id,\n"
                    + "	c.enti_razaosocial as razao,\n"
                    + "	c.enti_nome as fantasia,\n"
                    + "	c.enti_cnpjcpf as cnpj,\n"
                    + "	c.enti_inscricaoestadual as ie,\n"
                    + "	c.enti_inscricaomunicipal as im,\n"
                    + "	c.enti_rg as rg,\n"
                    + "	c.enti_orgexp as orgaoemissor,\n"
                    + "	c.enti_fj as tipopessoa,\n"
                    + "	c.enti_endereco as endereco,\n"
                    + "	c.enti_numero as numero,\n"
                    + "	c.enti_complemento as complemento,\n"
                    + "	c.enti_bairro as bairro,\n"
                    + "	c.enti_municipio as municipio,\n"
                    + "	m.muni_ibge as municipioibge,\n"
                    + "	m.muni_nome as descricaomunicipio,\n"
                    + "	m.muni_uf as descricaouf,\n"
                    + "	c.enti_uf as uf,\n"
                    + "	c.enti_cep as cep,\n"
                    + "	c.enti_fone as telefone,\n"
                    + "	c.enti_email as email,\n"
                    + "	c.enti_fax as fax,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_datacadastro as datacadastro,\n"
                    + "	c.enti_sexo as sexo,\n"
                    + "	c.enti_naturalidade as naturalidade,\n"
                    + "	c.enti_nacionalidade as nacionalidade,\n"
                    + "	c.enti_celular as celular,\n"
                    + "	c.enti_estcivil as estadocivil,\n"
                    + "	c.enti_limitecrediario as valorlimite,\n"
                    + "	c.enti_datanasc as datanascimento,\n"
                    + "	c.enti_codsituacao as situacaocliente,\n"
                    + "	s.situ_codigo as codigosituacaocliente,\n"
                    + "	s.situ_descricao as descricaosituacaocliente\n"
                    + "from entidades c \n"
                    + "left join municipios m on m.muni_codigo = c.enti_codmunicipio\n"
                    + "left join situacoes s on s.situ_codigo = c.enti_codsituacao\n"
                    + "where enti_tipo like '%U%'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    
                    if (rst.getString("rg") != null && !rst.getString("rg").trim().isEmpty()) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("ie"));
                    }
                    
                    imp.setInscricaoMunicipal(rst.getString("im"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getString("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    
                    if (rst.getString("sexo") != null && !rst.getString("sexo").trim().isEmpty()) {
                        imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    }

                    if (rst.getString("estadocivil") != null && !rst.getString("estadocivil").trim().isEmpty()) {
                        switch (rst.getString("estadocivil")) {
                            case "S":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "C":
                                imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                break;
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }
                    
                    imp.setBloqueado("Normal".equals(rst.getString("descricaosituacaocliente")) ? false : true);
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "    (r.pare_protocolo||'-'||r.pare_chave) as id,\n"
                    + "    r.pare_dtmvto as datamovimento,\n"
                    + "    r.pare_dtemissao as dataemissao,\n"
                    + "    r.pare_dtvcto as datavnecimento,\n"
                    + "    r.pare_parcela as numeroparcela,\n"
                    + "    r.pare_dcto as numerodocumento,\n"
                    + "    r.pare_valor as valorparcela,\n"
                    + "    r.pare_desconto as desconto,\n"
                    + "    r.pare_juros as juros,\n"
                    + "    r.pare_abatimentos as abatimentos,\n"
                    + "    r.pare_acrescimos as acrescimos,\n"
                    + "    r.pare_multa as multa,\n"
                    + "    r.pare_parcelas as totalparcelas,\n"
                    + "    r.pare_codentidade as idcliente,\n"
                    + "    f.enti_razaosocial as razao,\n"
                    + "    f.enti_nome as fantasia,\n"
                    + "    f.enti_cnpjcpf as cnpj,\n"
                    + "    r.pare_obs as observacao,\n"
                    + "    r.pare_complemento as complementoobs,\n"
                    + "    r.pare_pdv as ecf\n"
                    + "from pagrec r\n"
                    + "join entidades f on f.enti_codigo = r.pare_codentidade\n"
                    + "where r.pare_pr = 'R'\n"
                    + "and r.pare_dtbaixa = '2021-07-28'\n"
                    + "and r.pare_codentidade = 16193\n"
                    + "order by 3"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpjCliente(rst.getString("cnpj"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setParcela(rst.getInt("numeroparcela"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavnecimento"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setObservacao(rst.getString("observacao") + " " + rst.getString("complementoobs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cade_codigo as idproduto, \n"
                    + "	cade_dtoferta as datainicio, \n"
                    + "	cade_dtoferta as datafim, \n"
                    + "	cade_prvenda as precooferta, \n"
                    + "	cade_prnormal as preconormal \n"
                    + "from cadprodemp \n"
                    + "where cade_oferta = 'S' \n"
                    + "and cade_dtoferta >= '2021-08-01' \n"
                    + "and cade_codempresa = '" + getLojaOrigem() + "'"
            )) {

                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));

                    result.add(imp);

                }
            }
        }
        return result;
    }
    
}
