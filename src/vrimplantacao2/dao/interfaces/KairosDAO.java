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
public class KairosDAO extends InterfaceDAO implements MapaTributoProvider {

    public String pais;
    public String uf;
    public String loja;

    @Override
    public String getSistema() {
        return "Kairos";
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
                    "select\n" +
                    "    codb.numerocodigobarraproduto,\n" +
                    "    p.codigoproduto,\n" +
                    "    p.codigoncm,\n" +
                    "    p.codigogrupoproduto,\n" +
                    "    p.codigosubgrupoproduto,\n" +
                    "    p.descricao,\n" +
                    "    p.descricaotecnica,\n" +
                    "    p.observacoes,\n" +
                    "    p.siglaunidade,\n" +
                    "    p.pesobruto,\n" +
                    "    p.pesoliquido,\n" +
                    "    p.prazovalidade,\n" +
                    "    p.datacadastro,\n" +
                    "	 preco.preconormal precovenda,\n" +
                    "	 preco.precopromocao atacpreco,\n" +
                    "	 preco.quantidadeiniciopromocao atacqtd,\n" +
                    "    p.margemlucroteorica,\n" +
                    "    p.situacao,\n" +
                    "    p.classificacaofiscal,\n" +
                    "    p.situacaotributariapisent,\n" +
                    "    p.situacaotributariapis,\n" +
                    "    p.situacaotributariacofinsent,\n" +
                    "    p.situacaotributariacofins,\n" +
                    "    p.naturezareceitapiscofins,\n" +
                    "    codb.siglaunidade tipoembalagem,\n" +
                    "    codb.quantidadeproduto,\n" +
                    "    gfp.codigogrupofiscal,\n" +
                    "    gf.descricao,\n" +
                    "    p.codigoespecificadorst as cest\n" +
                    "from\n" +
                    "    produto p\n" +
                    "	join Filial fl on\n" +
                    "		fl.codigofilial = '" + getLojaOrigem() + "'\n" +
                    "	join Municipio mun on\n" +
                    "		fl.codigomunicipio = mun.codigomunicipio\n" +
                    "    left join codigobarraproduto codb on\n" +
                    "        codb.codigoproduto = p.codigoproduto\n" +
                    "    left join grupofiscalproduto gfp on\n" +
                    "        gfp.codigoproduto = p.codigoproduto\n" +
                    "        and gfp.siglapais = mun.SiglaPais\n" +
                    "        and gfp.siglauf = mun.SiglaUF \n" +
                    "        and gfp.codigofilial = fl.codigofilial\n" +
                    "    left join grupofiscal gf on\n" +
                    "        gf.codigogrupofiscal = gfp.codigogrupofiscal\n" +
                    "        and gf.siglauf = mun.SiglaUF\n" +
                    "        and gf.siglapais = mun.SiglaPais\n" +
                    "        and gf.codigofilial = fl.codigofilial\n" +
                    "	left join precovendaproduto preco on\n" +
                    "		preco.CodigoFilial = fl.CodigoFilial\n" +
                    "		and preco.codigoproduto = p.codigoproduto\n" +
                    "		and preco.CodigoCondicaoPagamento = 1\n" +
                    "		and preco.SiglaUnidade = p.siglaunidade\n" +
                    "order by\n" +
                    "    p.codigoproduto, codb.numerocodigobarraproduto desc"
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
                                "P".equals(bal.getPesavel()) ?
                                "KG" :
                                "UN"
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
                        "select\n" +
                        "	preco.codigoproduto id_produto,\n" +
                        "        preco.preconormal precovenda,\n" +
                        "        preco.quantidadeiniciopromocao qtdembalagem,\n" +
                        "        preco.precopromocao precoatacado\n" +
                        "from\n" +
                        "	precovendaproduto preco\n" +
                        "where\n" +
                        "	preco.CodigoFilial = '" + getLojaOrigem() + "'\n" +
                        "	and preco.CodigoCondicaoPagamento = 1\n" +
                        "	and preco.SiglaUnidade in ('UN', 'KG')\n" +
                        "	and preco.datainiciopromocao <= getdate() and preco.datafimpromocao >= getdate() + 360"
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
                    "select tp.CodigoTipoPessoa, p.CodigoPessoa, p.RazaoSocial, p.NomeFantasia, \n"
                    + "       p.DataNascimento, p.Sexo, p.EstadoCivil, p.Contato, p.Observacoes, \n"
                    + "       p.DataCadastro, endP.Endereco, endP.Numero, endP.Bairro, endP.Complemento, \n"
                    + "       m.CodigoIBGEMunicipio, m.Nome as municipio, m.SiglaUF, endP.CEP, endP.PontoReferencia, \n"
                    + "       endP.ContatoEndereco, \n"
                    + "(select top(1) telP.NumeroTelefone \n"
                    + "   from TelefonePessoa telP \n"
                    + "  where telP.CodigoPessoa = p.CodigoPessoa) as Telefone, \n"
                    + "(select docCnpj.NumeroDocumento \n"
                    + "   from DocumentoPessoa docCnpj \n"
                    + "   left join Documento doc on doc.CodigoDocumento = docCnpj.CodigoDocumento \n"
                    + "  where docCnpj.CodigoPessoa = p.CodigoPessoa \n"
                    + "    and doc.CodigoDocumento = 1) as Cnpj, \n"
                    + "(select docInscEst.NumeroDocumento \n"
                    + "   from DocumentoPessoa docInscEst \n"
                    + "   left join Documento doc on doc.CodigoDocumento = docInscEst.CodigoDocumento \n"
                    + "  where docInscEst.CodigoPessoa = p.CodigoPessoa \n"
                    + "    and doc.CodigoDocumento = 3) as InscricaoEstadual, \n"
                    + "(select docRG.NumeroDocumento \n"
                    + "   from DocumentoPessoa docRG \n"
                    + "   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento \n"
                    + "  where docRG.CodigoPessoa = p.CodigoPessoa \n"
                    + "    and doc.CodigoDocumento = 2) as RG, \n"
                    + "(select docRG.OrgaoExpedidor \n"
                    + "   from DocumentoPessoa docRG \n"
                    + "   left join Documento doc on doc.CodigoDocumento = docRG.CodigoDocumento \n"
                    + "  where docRG.CodigoPessoa = p.CodigoPessoa \n"
                    + "    and doc.CodigoDocumento = 2) as OrgaoExp \n"
                    + "from Pessoa p \n"
                    + "left join EnderecoPessoa endP on endP.CodigoPessoa = p.CodigoPessoa \n"
                    + "left join Municipio m on endP.CodigoMunicipio = m.CodigoMunicipio \n"
                    + "inner join TipoPessoa tp on tp.CodigoPessoa = p.CodigoPessoa and tp.CodigoTipoPessoa = 'F' \n"
                    + "order by p.CodigoPessoa"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CodigoPessoa"));
                    imp.setRazao(rst.getString("RazaoSocial"));
                    imp.setFantasia(rst.getString("NomeFantasia"));
                    imp.setCnpj_cpf(rst.getString("Cnpj"));
                    imp.setIe_rg(rst.getString("InscricaoEstadual"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("CodigoIBGEMunicipio"));
                    imp.setUf(rst.getString("SiglaUF"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("Telefone"));
                    imp.setObservacao(rst.getString("Observacoes"));

                    if ((rst.getString("Contato") != null)
                            && (!rst.getString("Contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("Contato"));
                    }

                    try (Statement stm1 = ConexaoSqlServer.getConexao().createStatement()) {
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
                    
                    if ((rst.getString("SiglaUnidade") != null) &&
                            (!rst.getString("SiglaUnidade").trim().isEmpty())) {
                        
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
            
            HashSet<String> movimento = new HashSet<>();            
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "            mv.codigopessoalancamento\n" +
                    "        from\n" +
                    "            movimentofinanceiro mv\n" +
                    "        where\n" +
                    "            mv.statuslancamento = 'G' \n" +
                    "            and mv.tipolancamento = 'C'"
            )) {
                while (rst.next()) {
                    movimento.add(rst.getString("codigopessoalancamento"));
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    coalesce(nullif(ltrim(tp.codigotipopessoa),'') ,'Z') codigotipopessoa,\n" +
                    "    p.codigopessoa,\n" +
                    "    p.razaosocial,\n" +
                    "    p.nomefantasia,\n" +
                    "    p.datanascimento,\n" +
                    "    p.sexo,\n" +
                    "    p.estadocivil,\n" +
                    "    p.contato,\n" +
                    "    p.observacoes,\n" +
                    "    p.datacadastro,\n" +
                    "    endp.endereco,\n" +
                    "    endp.numero,\n" +
                    "    endp.bairro,\n" +
                    "    endp.complemento,\n" +
                    "    m.codigoibgemunicipio,\n" +
                    "    m.nome municipio,\n" +
                    "    m.siglauf,\n" +
                    "    endp.cep,\n" +
                    "    endp.pontoreferencia,\n" +
                    "    endp.contatoendereco,\n" +
                    "    (select\n" +
                    "         top(1) telp.numerotelefone\n" +
                    "     from\n" +
                    "         telefonepessoa telp\n" +
                    "     where\n" +
                    "         telp.codigopessoa = p.codigopessoa) as telefone,\n" +
                    "    (select\n" +
                    "         doccnpj.numerodocumento\n" +
                    "     from\n" +
                    "         documentopessoa doccnpj\n" +
                    "         left join documento doc on doc.codigodocumento = doccnpj.codigodocumento\n" +
                    "     where\n" +
                    "         doccnpj.codigopessoa = p.codigopessoa and\n" +
                    "         doc.codigodocumento = 1) as cnpj,\n" +
                    "    (select\n" +
                    "         docinscest.numerodocumento\n" +
                    "     from\n" +
                    "         documentopessoa docinscest\n" +
                    "         left join documento doc on doc.codigodocumento = docinscest.codigodocumento\n" +
                    "     where\n" +
                    "         docinscest.codigopessoa = p.codigopessoa and\n" +
                    "         doc.codigodocumento = 3) as inscricaoestadual,\n" +
                    "    (select\n" +
                    "         docrg.numerodocumento\n" +
                    "     from\n" +
                    "         documentopessoa docrg\n" +
                    "         left join documento doc on doc.codigodocumento = docrg.codigodocumento\n" +
                    "     where\n" +
                    "         docrg.codigopessoa = p.codigopessoa and\n" +
                    "         doc.codigodocumento = 2) as rg,\n" +
                    "    (select\n" +
                    "         docrg.orgaoexpedidor\n" +
                    "     from\n" +
                    "         documentopessoa docrg\n" +
                    "         left join documento doc on doc.codigodocumento = docrg.codigodocumento\n" +
                    "     where\n" +
                    "         docrg.codigopessoa = p.codigopessoa and\n" +
                    "         doc.codigodocumento = 2) as orgaoexp,\n" +
                    "    (select\n" +
                    "         top(1) c.limitecredito\n" +
                    "     from\n" +
                    "         cliente c\n" +
                    "         inner join documentopessoa dp on dp.numerodocumento = c.cnpjcpfcliente and dp.codigopessoa = p.codigopessoa) limitecredito\n" +
                    "from\n" +
                    "    pessoa p\n" +
                    "    left join enderecopessoa endp on endp.codigopessoa = p.codigopessoa\n" +
                    "    left join municipio m on endp.codigomunicipio = m.codigomunicipio\n" +
                    "    left join tipopessoa tp on tp.codigopessoa = p.codigopessoa\n" +
                    "order by\n" +
                    "    codigotipopessoa, p.codigopessoa"
            )) {
                while (rst.next()) {
                    
                    if (
                            !"C".equals(rst.getString("codigotipopessoa")) &&
                            !movimento.contains(rst.getString("CodigoPessoa"))
                    ) {
                        continue;
                    }                    
                    
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
                    "select CodigoFilial, NomeFantasia \n"
                    + "from Filial\n"
                    + "order by 1"
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
        List<CreditoRotativoIMP> result =  new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	mv.codigofilial,\n" +
                    "	mv.nsufinanceiro id,\n" +
                    "	mv.dataemissao,\n" +
                    "	mv.valorvencimento valor,\n" +
                    "	mv.historicolivre observacao,\n" +
                    "	mv.codigopessoalancamento idcliente,\n" +
                    "	mv.datavencimento,\n" +
                    "	mv.parcela\n" +
                    "from\n" +
                    "	movimentofinanceiro mv\n" +
                    "where\n" +
                    "	mv.codigofilial = '" + getLojaOrigem() + "'\n" +
                    "	and mv.statuslancamento = 'G' \n" +
                    "	and mv.tipolancamento = 'C' \n" +
                    "order by\n" +
                    "	mv.codigofilial,\n" +
                    "	mv.nsufinanceiro"
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
        List<ChequeIMP> result =  new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
        
}
