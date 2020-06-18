/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class TPASistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    public String pais;
    public String uf;
    public String loja;

    @Override
    public String getSistema() {
        return "TPASistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MARGEM,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATACADO
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "m1.codigogrupoproduto codM1, "
                    + "m1.descricao descM1, "
                    + "m2.codigosubgrupoproduto codM2, "
                    + "m2.descricao descM2, "
                    + "1 codM3, m2.Descricao descM3 "
                    + "from GrupoProduto m1 "
                    + "left join SubGrupoProduto m2 on m2.CodigoGrupoProduto = m1.CodigoGrupoProduto "
                    + "order by codM1, codM2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codM1"));
                    imp.setMerc1Descricao(rst.getString("descM1"));
                    imp.setMerc2ID(rst.getString("codM2"));
                    imp.setMerc2Descricao(rst.getString("descM2"));
                    imp.setMerc3ID(rst.getString("codM3"));
                    imp.setMerc3Descricao(rst.getString("descM3"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from RC003EST"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CodigoProduto"));
                    imp.setEan(rst.getString("NumeroCodigoBarraProduto"));
                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("NumeroCodigoBarraProduto")));
                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem(
                                "P".equals(bal.getPesavel())
                                        ? "KG"
                                        : "UN"
                        );
                        imp.setValidade(bal.getValidade());
                    } else {
                        imp.setTipoEmbalagem(rst.getString("TipoEmbalagem"));
                        imp.setValidade(rst.getInt("PrazoValidade"));

                    }
                    imp.setQtdEmbalagem(rst.getInt("QuantidadeProduto"));
                    imp.setDescricaoCompleta(rst.getString("Descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("CodigoGrupoProduto"));
                    imp.setCodMercadologico2(rst.getString("CodigoSubGrupoProduto"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("MargemLucroTeorica"));
                    imp.setNcm(rst.getString("CodigoNcm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("Situacao")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rst.getDate("DataCadastro"));
                    imp.setPesoBruto(rst.getDouble("PesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("PesoLiquido"));
                    imp.setPiscofinsCstDebito(rst.getString("SituacaoTributariaPIS"));
                    imp.setPiscofinsCstCredito(rst.getString("SituacaoTributariaPISEnt"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NaturezaReceitaPisCofins"));
                    imp.setIcmsDebitoId(rst.getString("CodigoGrupoFiscal"));
                    imp.setIcmsCreditoId(rst.getString("CodigoGrupoFiscal"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	preco.codigoproduto id_produto,\n"
                        + "        preco.preconormal precovenda,\n"
                        + "        preco.quantidadeiniciopromocao qtdembalagem,\n"
                        + "        preco.precopromocao precoatacado\n"
                        + "from\n"
                        + "	precovendaproduto preco\n"
                        + "where\n"
                        + "	preco.CodigoFilial = '" + getLojaOrigem() + "'\n"
                        + "	and preco.CodigoCondicaoPagamento = 1\n"
                        + "	and preco.SiglaUnidade in ('UN', 'KG')\n"
                        + "	and preco.datainiciopromocao <= getdate() and preco.datafimpromocao >= getdate() + 360"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(String.format("999%06d", Utils.stringToInt(imp.getImportId())));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.CUSTO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH data AS ( "
                        + "select c.CodigoProduto, MAX(DataMovimento) as data "
                        + "from CustoProduto as c where c.CodigoFilial in (" + getLojaOrigem() + ") group by c.CodigoProduto) "
                        + "select c.CodigoProduto, c.ValorCustoReal, c.DataMovimento "
                        + "from CustoProduto c "
                        + "inner join data dt on dt.CodigoProduto = c.CodigoProduto and dt.data = c.DataMovimento "
                        + "where c.CodigoFilial = " + getLojaOrigem() + " "
                        + "order by c.CodigoProduto"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CodigoProduto"));
                        imp.setCustoComImposto(rst.getDouble("ValorCustoReal"));
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        result.add(imp);
                    }
                }
                return result;
            }
        }

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH data AS ( "
                        + "select e.CodigoProduto, MAX(AlteracaoDataHora) as data "
                        + "from EstoqueProduto as e where e.CodigoFilial in (" + getLojaOrigem() + ") group by e.CodigoProduto) "
                        + "select e.CodigoProduto, e.QuantidadeSaldoEstoque, e.AlteracaoDataHora "
                        + "from EstoqueProduto e "
                        + "inner join data dt on dt.CodigoProduto = e.CodigoProduto and dt.data = e.AlteracaoDataHora "
                        + "where e.CodigoFilial = " + getLojaOrigem() + " "
                        + "order by e.CodigoProduto"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("CodigoProduto"));
                        imp.setEstoque(rst.getDouble("QuantidadeSaldoEstoque"));
                        result.add(imp);
                    }
                }
                return result;
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
                    + "	codifabric id,\n"
                    + "	forc35raza razao,\n"
                    + "	forc10apel fantasia,\n"
                    + "	forc15cgc cnpj,\n"
                    + "	forc19insc ie_rg,\n"
                    + "	forc35ende endereco,\n"
                    + "	forc10comp complemento,\n"
                    + "	forc20bair bairro,\n"
                    + "	forccdibge ibge_municipio,\n"
                    + "	forc20cida municipio,\n"
                    + "	forc02esta uf,\n"
                    + "	forc08cep cep,\n"
                    + "	forc25fone telefone,\n"
                    + "	forddtinic datacadastro,\n"
                    + "	observacoe observacao,"
                    + " forc20cont contato\n"
                    + "from rc008for"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }

                    /*try (Statement stm1 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst1 = stm1.executeQuery(
                                "select \n"
                                + "CodigoPessoa, NumeroTelefone \n"
                                + "from TelefonePessoa\n"
                                + "where CodigoPessoa = " + imp.getImportId()
                        )) {
                            while (rst1.next()) {
                                imp.addContato(
                                        "TELEFONE",
                                        rst1.getString("NumeroTelefone"),
                                        null,
                                        TipoContato.COMERCIAL,
                                        null
                                );
                            }
                        }
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "CodigoPessoa, EnderecoEletronicoPessoa \n"
                                + "from EnderecoEletronicoPessoa\n"
                                + "where EnderecoEletronicoPessoa <> ''"
                                + "and CodigoPessoa = " + imp.getImportId()
                        )) {
                            while (rst2.next()) {
                                imp.addContato(
                                        "EMAIL",
                                        null,
                                        null,
                                        TipoContato.NFE,
                                        rst2.getString("EnderecoEletronicoPessoa").toLowerCase()
                                );
                            }
                        }
                    }*/
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
                    "select "
                    + "CodigoPessoa, "
                    + "CodigoProduto, "
                    + "Referencia, "
                    + "AlteracaoDataHora,"
                    + "SiglaUnidade "
                    + "from ProdutoFornecedor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("CodigoPessoa"));
                    imp.setIdProduto(rst.getString("CodigoProduto"));
                    imp.setCodigoExterno(rst.getString("Referencia"));
                    imp.setDataAlteracao(rst.getDate("AlteracaoDataHora"));

                    if ((rst.getString("SiglaUnidade") != null)
                            && (!rst.getString("SiglaUnidade").trim().isEmpty())) {

                        int qtdEmbalagem = Integer.parseInt(Utils.formataNumero(rst.getString("SiglaUnidade")));

                        if (qtdEmbalagem > 0) {
                            imp.setQtdEmbalagem(qtdEmbalagem);
                        }
                    }

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

            /*HashSet<String> movimento = new HashSet<>();
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "            mv.codigopessoalancamento\n"
                    + "        from\n"
                    + "            movimentofinanceiro mv\n"
                    + "        where\n"
                    + "            mv.statuslancamento = 'G' \n"
                    + "            and mv.tipolancamento = 'C'"
            )) {
                while (rst.next()) {
                    movimento.add(rst.getString("codigopessoalancamento"));
                }
            }*/

            try (ResultSet rst = stm.executeQuery(
                    "select * from rc042cli"
            )) {
                while (rst.next()) {

                    /*if (!"C".equals(rst.getString("codigotipopessoa"))
                            && !movimento.contains(rst.getString("CodigoPessoa"))) {
                        continue;
                    }*/

                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("CodigoPessoa"));
                    imp.setRazao(rst.getString("RazaoSocial"));
                    imp.setFantasia(rst.getString("NomeFantasia"));
                    imp.setDataCadastro(rst.getDate("DataNascimento"));

                    if ((rst.getString("Sexo") != null)
                            && (!rst.getString("Sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("Sexo").trim())) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    }

                    if ((rst.getString("EstadoCivil") != null)
                            && (!rst.getString("EstadoCivil").trim().isEmpty())) {
                        if (null != rst.getString("EstadoCivil").trim()) {
                            switch (rst.getString("EstadoCivil").trim()) {
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "D":
                                    imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                    break;
                                case "V":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                                    break;
                            }
                        }
                    }

                    imp.setObservacao(rst.getString("Observacoes"));
                    imp.setObservacao2(rst.getString("Contato"));
                    imp.setDataCadastro(rst.getDate("DataCadastro"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setMunicipioIBGE(rst.getInt("CodigoIBGEMunicipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("SiglaUF"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setCnpj(rst.getString("Cnpj"));
                    imp.setInscricaoestadual(rst.getString("InscricaoEstadual"));
                    if (imp.getInscricaoestadual() == null || imp.getInscricaoestadual().trim().equals("")) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    }
                    imp.setOrgaoemissor(rst.getString("OrgaoExp"));
                    imp.setValorLimite(rst.getDouble("LimiteCredito"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);

                    try (Statement stm1 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst1 = stm1.executeQuery(
                                "select \n"
                                + "CodigoPessoa, NumeroTelefone \n"
                                + "from TelefonePessoa\n"
                                + "where CodigoPessoa = " + imp.getId()
                        )) {
                            while (rst1.next()) {
                                imp.addContato(
                                        "TELEFONE",
                                        rst1.getString("NumeroTelefone"),
                                        null,
                                        null,
                                        null
                                );
                            }
                        }
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "CodigoPessoa, EnderecoEletronicoPessoa \n"
                                + "from EnderecoEletronicoPessoa\n"
                                + "where EnderecoEletronicoPessoa <> ''"
                                + "and CodigoPessoa = " + imp.getId()
                        )) {
                            while (rst2.next()) {
                                imp.addContato(
                                        "EMAIL",
                                        null,
                                        null,
                                        null,
                                        rst2.getString("EnderecoEletronicoPessoa").toLowerCase()
                                );
                            }
                        }
                    }
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "CodigoGrupoFiscal as codigo, \n"
                    + "Descricao as descricao \n"
                    + "from GrupoFiscal\n"
                    + "where CodigoFilial = '" + loja + "'\n"
                    + "and SiglaPais = '" + pais + "'\n"
                    + "and SiglaUF = '" + uf + "'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
            return result;
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from rc002loj"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("CodigoFilial"), rst.getString("NomeFantasia"))
                    );
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
                    "select\n"
                    + "	mv.codigofilial,\n"
                    + "	mv.nsufinanceiro id,\n"
                    + "	mv.dataemissao,\n"
                    + "	mv.valorvencimento valor,\n"
                    + "	mv.historicolivre observacao,\n"
                    + "	mv.codigopessoalancamento idcliente,\n"
                    + "	mv.datavencimento,\n"
                    + "	mv.parcela\n"
                    + "from\n"
                    + "	movimentofinanceiro mv\n"
                    + "where\n"
                    + "	mv.codigofilial = '" + getLojaOrigem() + "'\n"
                    + "	and mv.statuslancamento = 'G' \n"
                    + "	and mv.tipolancamento = 'C' \n"
                    + "order by\n"
                    + "	mv.codigofilial,\n"
                    + "	mv.nsufinanceiro"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("codigofilial") + "-" + rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setParcela(rst.getInt("parcela"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.codigofilial,\n"
                    + "	c.codigoinstituicaofinanceira,\n"
                    + "	c.numeroagencia,\n"
                    + "	c.numerocontacorrente,\n"
                    + "	c.numerocheque,\n"
                    + "	c.cnpjcpfemitente,\n"
                    + "	c.dataemissao,\n"
                    + "	c.datacheque datacheque,\n"
                    + "	c.datalancamento,\n"
                    + "	c.valornominal\n"
                    + "from\n"
                    + "	chequerecebido c\n"
                    + "where\n"
                    + "	c.codigofilial = '" + getLojaOrigem() + "'\n"
                    + "order by\n"
                    + "	1,2,3,4,5"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(String.format(
                            "%s-%s-%s-%s-%s",
                            rst.getString("codigofilial"),
                            rst.getString("codigoinstituicaofinanceira"),
                            rst.getString("numeroagencia"),
                            rst.getString("numerocontacorrente"),
                            rst.getString("numerocheque")
                    ));
                    imp.setBanco(Utils.stringToInt(rst.getString("codigoinstituicaofinanceira")));
                    imp.setAgencia(rst.getString("numeroagencia"));
                    imp.setConta(rst.getString("numerocontacorrente"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setCpf(rst.getString("cnpjcpfemitente"));
                    imp.setDataDeposito(rst.getDate("datalancamento"));
                    imp.setValor(rst.getDouble("valornominal"));
                    imp.setSituacaoCheque(SituacaoCheque.BAIXADO);

                    result.add(imp);
                }
            }
        }

        return result;
    }

}
