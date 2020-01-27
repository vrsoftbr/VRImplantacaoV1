/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SiitDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Siit";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  codigo,\n"
                    + "  nomefantasia as nome\n"
                    + "from filial\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("nome")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  i.codigo as codigo_icms,\n"
                    + "  i.descricao as descricao_icms,\n"
                    + "  i2.cstcsosn as cst_icms,\n"
                    + "  i2.picms as aliquota_icms,\n"
                    + "  i2.predbc as reducao_icms\n"
                    + "from tributacaoicms i\n"
                    + "inner join tributacaoicmsitem i2 on i2.tributacaoicms_codigo = i.codigo\n"
                    + "and i2.uf = 'GO'\n"
                    + "and i2.cfop = 5102"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo_icms"),
                            rst.getString("descricao_icms"),
                            rst.getInt("cst_icms"),
                            rst.getDouble("aliquota_icms"),
                            rst.getDouble("reducao_icms")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  codigo,\n"
                    + "  descricao,\n"
                    + "  nivel\n"
                    + "from departamento\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    if (rst.getString("codigo").contains(".")) {

                        String merc = rst.getString("codigo") != null ? rst.getString("codigo") : "";
                        String[] cods = merc.split("\\.");

                        for (int i = 0; i < cods.length; i++) {

                            switch (i) {
                                case 0:
                                    imp.setMerc1ID(cods[i]);
                                    imp.setMerc1Descricao(rst.getString("descricao"));
                                    break;
                                case 1:
                                    imp.setMerc2ID(cods[i]);
                                    imp.setMerc2Descricao(rst.getString("descricao"));
                                    break;
                            }
                        }

                        imp.setMerc3ID("1");
                        imp.setMerc3Descricao(rst.getString("descricao"));

                    } else {
                        imp.setMerc1ID(rst.getString("codigo"));
                        imp.setMerc1Descricao(rst.getString("descricao"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  p.codigo as id,\n"
                    + "  ean.codigobarras as ean,\n"
                    + "  ean.codigobalanca,\n"
                    + "  ean.codigobarrasprincipal as ean_principal,\n"
                    + "  ean.diasvalidadebalanca as validade,\n"
                    + "  p.descricao as descricaocompleta,\n"
                    + "  p.descricaocupom as descricaoreduzida,\n"
                    + "  p.ncm as ncm,\n"
                    + "  p.cest as cest,\n"
                    + "  p.tributacaoicms_codigo as id_icms,\n"
                    + "  p.receitapiscofins as naturezareceita,\n"
                    + "  p.datacadastro as datacadastro,\n"
                    + "  p.unidademedida_codigo as tipoembalagem,\n"
                    + "  iv.departamento_codigo as mercadologico,\n"
                    + "  case p.excluido when 1 then 0 else 1 end situacaocadastro,\n"
                    + "  e.quantidade as estoque,\n"
                    + "  e.estoqueminimo,\n"
                    + "  e.estoquemaximo,\n"
                    + "  pr.margemideal as margem,\n"
                    + "  pr.precovenda,\n"
                    + "  p.tributacaopiscofins_codigo as cst_pis,  \n"
                    + "  pc.codigo as codigo_pis,\n"
                    + "  pc.descricao as descricao_pis,\n"
                    + "  pc2.cst as cst_piscofins,\n"
                    + "  i.codigo as codigo_icms,\n"
                    + "  i.descricao as descricao_icms,\n"
                    + "  i2.cstcsosn as cst_icms,\n"
                    + "  i2.picms as aliquota_icms,\n"
                    + "  i2.predbc as reducao_icms  \n"
                    + "from item p\n"
                    + "left join itemvenda iv on iv.item_codigo = p.codigo\n"
                    + "left join itemestoque e on e.item_codigo = p.codigo\n"
                    + "left join itemunidadepreco pr on pr.item_codigo = p.codigo\n"
                    + "left join itemunidadecodigobarras ean on ean.item_codigo = p.codigo\n"
                    + "inner join tributacaopiscofins pc on pc.codigo = p.tributacaopiscofins_codigo\n"
                    + "inner join tributacaopiscofinsitem pc2 on pc2.tributacaopiscofins_codigo = pc.codigo\n"
                    + "  and pc2.uf = 'GO'\n"
                    + "  and pc2.cfop = 5102\n"
                    + "inner join tributacaoicms i on i.codigo = p.tributacaoicms_codigo\n"
                    + "inner join tributacaoicmsitem i2 on i2.tributacaoicms_codigo = i.codigo\n"
                    + "  and i2.uf = 'GO'\n"
                    + "  and i2.cfop = 5102\n"
                    + "order by p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("codigobalanca") == 1);
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_piscofins"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_piscofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));

                    if (rst.getString("mercadologico").contains(".")) {

                        String merc = rst.getString("mercadologico") != null ? rst.getString("mercadologico") : "";
                        String[] cods = merc.split("\\.");

                        for (int i = 0; i < cods.length; i++) {

                            switch (i) {
                                case 0:
                                    imp.setCodMercadologico1(cods[i]);
                                    break;
                                case 1:
                                    imp.setCodMercadologico2(cods[i]);
                                    break;
                            }
                        }
                        imp.setCodMercadologico3("1");
                    } else {
                        imp.setCodMercadologico1(rst.getString("mercadologico"));
                        imp.setCodMercadologico2("1");
                        imp.setCodMercadologico3("1");
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.CUSTO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "distinct(item_codigo) as id\n"
                        + "  from itemestoquediario"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));

                        try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                            try (ResultSet rst2 = stm2.executeQuery(
                                    "select "
                                    + "  max(dataestoque),"
                                    + "  custoliquido as custo\n"
                                    + "from itemestoquediario\n"
                                    + "where item_codigo = " + imp.getImportId()
                                    + " and filialestoque_codigo = " + getLojaOrigem()
                            )) {
                                while (rst2.next()) {
                                    imp.setCustoComImposto(rst2.getDouble("custo"));
                                    imp.setCustoSemImposto(imp.getCustoComImposto());
                                }
                            }
                        }
                        result.add(imp);
                    }
                }                
            }
            return result;
        }
        
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "  p.codigo as id,\n"
                    + "  p.nomerazao as razao,\n"
                    + "  pj.nomefantasia as fantasia,\n"
                    + "  pj.cnpj,\n"
                    + "  pj.ie,\n"
                    + "  p.tipopessoa as tipopessoa,\n"
                    + "  p.datacadastro,\n"
                    + "  pe.endereco_codigo,\n"
                    + "  e.logradouro as endereco,\n"
                    + "  e.numero,\n"
                    + "  e.complemento,\n"
                    + "  e.uf_codigo as uf_ibge,\n"
                    + "  e.cidade_codigo as municipio_ibge,\n"
                    + "  e.cep,\n"
                    + "  b.nome as bairro,\n"
                    + "  b.ceppadrao,\n"
                    + "  c.cep as cep2,\n"
                    + "  cid.nome as municipio,\n"
                    + "  uf.nome as uf,\n"
                    + "  tel.numero as telefone,\n"
                    + "  p.observacao,\n"
                    + "  fo.prazoentregadias as prazoentrega,\n"
                    + "  fo.contato,\n"
                    + "  fo.representante,\n"
                    + "  fo.emailrepresentante\n"
                    + "from participante p\n"
                    + "left join participantepj pj on pj.participante_codigo = p.codigo\n"
                    + "left join participanteendereco pe on pe.participante_codigo = p.codigo\n"
                    + "left join endereco e on e.codigo = pe.endereco_codigo\n"
                    + "left join bairro b on b.codigo = e.bairro_codigo\n"
                    + "left join cep c on c.bairro_codigo = b.codigo\n"
                    + "left join cidade cid on cid.codigo = e.cidade_codigo\n"
                    + "left join uf on uf.codigo = e.uf_codigo\n"
                    + "left join participantetelefone tel on tel.participante_codigo = p.codigo\n"
                    + "left join participantefornecedor fo on fo.participante_codigo = p.codigo\n"
                    + "where p.fornecedor = 1\n"
                    + "order by p.codigo"
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
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_uf(rst.getInt("uf_ibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoentrega"));

                    if ((rst.getString("emailrepresentante") != null)
                            && (!rst.getString("emailrepresentante").trim().isEmpty())) {

                        String contato;

                        if ((rst.getString("contato") != null)
                                && (!rst.getString("contato").trim().isEmpty())) {

                            contato = rst.getString("contato");

                        } else if ((rst.getString("representante") != null)
                                && (!rst.getString("representante").trim().isEmpty())) {

                            contato = rst.getString("representante");
                        } else {

                            contato = "EMAIL REPRESENTANTE";
                        }

                        imp.addEmail(
                                contato,
                                rst.getString("emailrepresentante").toLowerCase(),
                                TipoContato.COMERCIAL
                        );

                    }

                    try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "  numero as telefone\n"
                                + "from participantetelefone \n"
                                + "where participante_codigo in (\n"
                                + "  select participante_codigo \n"
                                + "    from participantetelefone\n"
                                + "    group by participante_codigo\n"
                                + "   having count(participante_codigo) > 1)\n"
                                + "and participante_codigo = " + imp.getImportId()
                                + " order by participante_codigo"
                        )) {
                            while (rst2.next()) {

                                imp.addTelefone(
                                        "TELEFONE",
                                        rst.getString("telefone")
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  participante_codigo as id_fornecedor,\n"
                    + "  item_codigo as id_produto,\n"
                    + "  codigoitemfornecedor as codigoexterno\n"
                    + "from itemcodigofornecedor\n"
                    + "order by participante_codigo"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "  p.codigo as id,\n"
                    + "  p.nomerazao as razao,\n"
                    + "  pf.estadocivil,\n"
                    + "  pf.cpf,\n"
                    + "  pf.rg,\n"
                    + "  pf.siglaemissorrg,\n"
                    + "  pf.uf_emissorrg,\n"
                    + "  pf.dataexpedicao,\n"
                    + "  pf.datanascimento,\n"
                    + "  pf.filiacaopai as pai,\n"
                    + "  pf.filiacaomae as mae,\n"
                    + "  p.tipopessoa as tipopessoa,\n"
                    + "  p.datacadastro,\n"
                    + "  pe.endereco_codigo,\n"
                    + "  e.logradouro as endereco,\n"
                    + "  e.numero,\n"
                    + "  e.complemento,\n"
                    + "  e.uf_codigo as uf_ibge,\n"
                    + "  e.cidade_codigo as municipio_ibge,\n"
                    + "  e.cep,\n"
                    + "  b.nome as bairro,\n"
                    + "  b.ceppadrao,\n"
                    + "  c.cep as cep2,\n"
                    + "  cid.nome as municipio,\n"
                    + "  uf.nome as uf,\n"
                    + "  tel.numero as telefone,\n"
                    + "  ema.email,\n"
                    + "  pc.bloqueado,\n"
                    + "  pc.limitecredito,\n"
                    + "  pc.diavencimentofatura,\n"
                    + "p.observacao\n"
                    + "from participante p\n"
                    + "left join participantepf pf on pf.participante_codigo = p.codigo\n"
                    + "left join participanteendereco pe on pe.participante_codigo = p.codigo\n"
                    + "left join endereco e on e.codigo = pe.endereco_codigo\n"
                    + "left join bairro b on b.codigo = e.bairro_codigo\n"
                    + "left join cep c on c.bairro_codigo = b.codigo\n"
                    + "left join cidade cid on cid.codigo = e.cidade_codigo\n"
                    + "left join uf on uf.codigo = e.uf_codigo\n"
                    + "left join participantetelefone tel on tel.participante_codigo = p.codigo\n"
                    + "left join participantecliente pc on pc.participante_codigo = p.codigo\n"
                    + "left join participanteemails ema on ema.participante_codigo = p.codigo\n"
                    + "where p.cliente = 1\n"
                    + "or p.funcionario = 1\n"
                    + "order by p.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setOrgaoemissor(rst.getString("siglaemissorrg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getString("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setUfIBGE(rst.getInt("uf_ibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setBloqueado(rst.getInt("bloqueado") == 0);
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setDiaVencimento(rst.getInt("diavencimentofatura"));
                    imp.setObservacao(rst.getString("observacao"));

                    try (Statement stm2 = ConexaoMySQL.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select \n"
                                + "  numero as telefone\n"
                                + "from participantetelefone \n"
                                + "where participante_codigo in (\n"
                                + "  select participante_codigo \n"
                                + "    from participantetelefone\n"
                                + "    group by participante_codigo\n"
                                + "   having count(participante_codigo) > 1)\n"
                                + "and participante_codigo = " + imp.getId()
                                + " order by participante_codigo"
                        )) {
                            while (rst2.next()) {

                                imp.addTelefone(
                                        "TELEFONE",
                                        rst.getString("telefone")
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
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  ch.codigo as id,\n"
                    + "  ch.participante_codigo,\n"
                    + "  p.nomerazao as nome,\n"
                    + "  pf.cpf,\n"
                    + "  pf.rg,\n"
                    + "  tel.numero as telefone,\n"
                    + "  ch.dataemissao,\n"
                    + "  ch.datavencimento,\n"
                    + "  ch.valortitulo as valor,\n"
                    + "  ch.numerocheque,\n"
                    + "  ch.codigobanco as banco,\n"
                    + "  ch.codigoagencia as agencia,\n"
                    + "  ch.numeroconta,\n"
                    + "  ch.parcela,\n"
                    + "  ch.atraso,\n"
                    + "  ch.observacao\n"
                    + "from contareceber ch\n"
                    + "left join participante p on p.codigo = ch.participante_codigo \n"
                    + "left join participantepf pf on pf.participante_codigo = p.codigo\n"
                    + "left join participantetelefone tel on tel.participante_codigo = p.codigo\n"
                    + "where numerocheque <> ''\n"
                    + "and ch.databaixa is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setNome(rst.getString("nome"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDate(rst.getDate("dataemissao"));
                    imp.setDataDeposito(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("numeroconta"));
                    imp.setObservacao(rst.getString("observacao")
                            + " " + rst.getString("atraso") + " DIAS ATRASO"
                            + " PARCELA " + rst.getString("parcela"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
