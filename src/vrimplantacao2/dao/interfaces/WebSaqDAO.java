/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
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
public class WebSaqDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_arquivo = "";

    @Override
    public String getSistema() {
        return "WebSaq";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.coddepto cod_m1, m1.nome desc_m1,\n"
                    + "m2.codgrupo cod_m2, m2.descricao desc_m2,\n"
                    + "m3.codsubgrupo cod_m3, m3.descricao desc_m3\n"
                    + "from departamento m1\n"
                    + "inner join grupoprod m2 on m2.coddepto = m1.coddepto\n"
                    + "inner join subgrupo m3 on m3.codgrupo = m2.codgrupo\n"
                    + "order by m1.coddepto, m2.codgrupo, m3.codsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m1"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(rst.getString("desc_m2"));
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(rst.getString("desc_m3"));
                    result.add(imp);
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
                    + "p.codproduto,\n"
                    + "p.descricao,\n"
                    + "p.descricaofiscal,\n"
                    + "p.coddepto,\n"
                    + "p.codgrupo,\n"
                    + "p.codsubgrupo,\n"
                    + "p.codsimilar,\n"
                    + "p.estminimo,\n"
                    + "p.estmaximo,\n"
                    + "p.pesoliq,\n"
                    + "p.pesobruto,\n"
                    + "p.pesado,\n"
                    + "p.foralinha,\n"
                    + "p.qtdeetiq,\n"
                    + "p.diasvalidade,\n"
                    + "p.pesounid,\n"
                    + "p.vasilhame,\n"
                    + "p.codvasilhame,\n"
                    + "p.codfamilia,\n"
                    + "p.custotab,\n"
                    + "p.precoatc,\n"
                    + "p.precovrj,\n"
                    + "p.margematc,\n"
                    + "p.margemvrj,\n"
                    + "p.datainclusao,\n"
                    + "p.custorep,\n"
                    + "p.altura,\n"
                    + "p.largura,\n"
                    + "p.enviarecommerce,\n"
                    + "p.comprimento,\n"
                    + "p.cest,\n"
                    + "u.sigla as embalagem,\n"
                    + "e.quantidade as qtdembalagem,\n"
                    + "pcs.codcst cstpiscofinssaida,\n"
                    + "pce.codcst cstpiscofinsentrada,\n"
                    + "p.natreceita,\n"
                    + "ncm.codigoncm,\n"
                    + "p.codcfpdv,\n"
                    + "cf.descricao icmsdesc,\n"
                    + "cf.codcst as icmscst,\n"
                    + "cf.aliqicms as icmsaliq,\n"
                    + "cf.aliqredicms as icmsred\n"
                    + "from produto p \n"
                    + "left join embalagem e on e.codembal = p.codembalvda\n"
                    + "inner join unidade u on u.codunidade = e.codunidade\n"
                    + "left join piscofins pcs on pcs.codpiscofins = p.codpiscofinssai\n"
                    + "left join piscofins pce on pce.codpiscofins = p.codpiscofinsent\n"
                    + "left join ncm on ncm.idncm = p.idncm\n"
                    + "left join classfiscal cf on cf.codcf = p.codcfpdv\n"
                    + "order by p.codproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codproduto"));
                    imp.seteBalanca("S".equals(rst.getString("pesado")));
                    imp.setValidade(rst.getInt("diasvalidade"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaofiscal"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("coddepto"));
                    imp.setCodMercadologico2(rst.getString("codgrupo"));
                    imp.setCodMercadologico3(rst.getString("codsubgrupo"));
                    imp.setIdFamiliaProduto(rst.getString("codfamilia"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPrecovenda(rst.getDouble("precovrj"));
                    imp.setCustoComImposto(rst.getDouble("custorep"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margemvrj"));
                    imp.setDataCadastro(rst.getDate("datainclusao"));
                    imp.setNcm(rst.getString("codigoncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));
                    imp.setIcmsDebitoId(rst.getString("codcfpdv"));
                    imp.setIcmsCreditoId(rst.getString("codcfpdv"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opcao == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "with estoque as\n"
                        + "(\n"
                        + "  select max(data) as data, codproduto from produtoestabsaldo where codestabelec = " + getLojaOrigem() + " group by codproduto\n"
                        + ")\n"
                        + "select pe.codproduto, pe.saldo, pe.data \n"
                        + "from produtoestabsaldo pe\n"
                        + "inner join estoque e on e.codproduto = pe.codproduto and pe.data = e.data\n"
                        + "where codestabelec = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codproduto"));
                        imp.setEstoque(rst.getDouble("saldo"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codean, \n"
                    + "quantidade \n"
                    + "from produtoean \n"
                    + "order by codproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codproduto"));
                    imp.setEan(rst.getString("codean"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
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
                    + "f.codfornec,\n"
                    + "f.endereco,\n"
                    + "f.bairro,\n"
                    + "f.cep,\n"
                    + "f.codcidade,\n"
                    + "c.nome as nomecidade,\n"
                    + "c.codoficial cidadeibge,\n"
                    + "f.uf,\n"
                    + "f.contato1,\n"
                    + "f.fone1,\n"
                    + "f.fone2,\n"
                    + "f.fone3,\n"
                    + "f.site,\n"
                    + "f.email,\n"
                    + "f.tppessoa,\n"
                    + "f.cpfcnpj,\n"
                    + "f.rgie,\n"
                    + "f.codatividade,\n"
                    + "f.codbanco,\n"
                    + "f.agencia,\n"
                    + "f.contacorrente,\n"
                    + "f.observacao,\n"
                    + "f.contato2,\n"
                    + "f.contato3,\n"
                    + "f.email1,\n"
                    + "f.email2,\n"
                    + "f.email3,\n"
                    + "f.fone,\n"
                    + "f.fax,\n"
                    + "f.numero,\n"
                    + "f.complemento,\n"
                    + "f.suframa,\n"
                    + "f.datainclusao,\n"
                    + "f.tipocompra,\n"
                    + "f.inscmunicipal,\n"
                    + "f.status\n"
                    + "from fornecedor f\n"
                    + "left join cidade c on c.codcidade = f.codcidade\n"
                    + "order by codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfornec"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("rgie"));
                    imp.setInsc_municipal(rst.getString("inscmunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("nomecidade"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDatacadastro(rst.getDate("datainclusao"));

                    if ((rst.getString("contato1") != null)
                            && (!rst.getString("contato1").trim().isEmpty())) {
                        imp.setObservacao("CONTATO - " + rst.getString("contato1") + " ");
                    }

                    imp.setObservacao(imp.getObservacao() + rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("fone"));

                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 1",
                                rst.getString("fone1"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone3") != null)
                            && (!rst.getString("fone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                rst.getString("fone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("email1") != null)
                            && (!rst.getString("email1").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "EMAIL 1",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email1").toLowerCase()
                        );
                    }
                    if ((rst.getString("email2") != null)
                            && (!rst.getString("email2").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "EMAIL 2",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email2").toLowerCase()
                        );
                    }
                    if ((rst.getString("email3") != null)
                            && (!rst.getString("email3").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "EMAIL 3",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email3").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores(OpcaoFornecedor opcao) throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        if (opcao == OpcaoFornecedor.RAZAO_SOCIAL) {

            linha = 0;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                for (int i = 0; i < sheet.getRows(); i++) {

                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellRazao = sheet.getCell(2, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setRazao(cellRazao.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        if (opcao == OpcaoFornecedor.NOME_FANTASIA) {

            linha = 0;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                for (int i = 0; i < sheet.getRows(); i++) {

                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellFantasia = sheet.getCell(1, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setFantasia(cellFantasia.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codfornec, \n"
                    + "reffornec,\n"
                    + "principal \n"
                    + "from prodfornec\n"
                    + "order by principal desc"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codproduto"));
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    imp.setCodigoExterno(rst.getString("reffornec"));
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
                    + "c.codcliente,\n"
                    + "c.nome,\n"
                    + "c.razaosocial,\n"
                    + "c.enderfat,\n"
                    + "c.bairrofat,\n"
                    + "c.cepfat,\n"
                    + "c.codcidadefat,\n"
                    + "c.uffat,\n"
                    + "c.enderent,\n"
                    + "c.bairroent,\n"
                    + "c.cepent,\n"
                    + "c.codcidadeent,\n"
                    + "c.ufent,\n"
                    + "c.contato,\n"
                    + "c.site,\n"
                    + "c.email,\n"
                    + "c.tppessoa,\n"
                    + "c.cpfcnpj,\n"
                    + "c.rgie,\n"
                    + "c.observacao,\n"
                    + "c.dtnascto,\n"
                    + "c.sexo,\n"
                    + "c.estcivil,\n"
                    + "c.tipomoradia,\n"
                    + "c.dtmoradia,\n"
                    + "c.enderres,\n"
                    + "c.bairrores,\n"
                    + "c.cepres,\n"
                    + "c.codcidaderes,\n"
                    + "cid.nome as nomecidade,\n"
                    + "cid.codoficial as cidadeibge,\n"
                    + "c.ufres,\n"
                    + "c.nomeconj,\n"
                    + "c.cpfconj,\n"
                    + "c.rgconj,\n"
                    + "c.salarioconj,\n"
                    + "c.foneres,\n"
                    + "c.celular,\n"
                    + "c.fonefat,\n"
                    + "c.faxfat,\n"
                    + "c.foneent,\n"
                    + "c.faxent,\n"
                    + "c.dtinclusao,\n"
                    + "c.salario,\n"
                    + "c.senha,\n"
                    + "c.numerofat,\n"
                    + "c.complementofat,\n"
                    + "c.numeroent,\n"
                    + "c.complementoent,\n"
                    + "c.numerores,\n"
                    + "c.complementores,\n"
                    + "(coalesce(c.limite1, 0) + coalesce(c.limite2) - coalesce(c.debito1, 0) - coalesce(debito2, 0)) as valorlimite,\n"
                    + "c.limite1,\n"
                    + "c.emailnfe,\n"
                    + "c.rgemissor,\n"
                    + "c.codstatus,\n"
                    + "s.descricao,\n"
                    + "s.bloqueado\n"
                    + "from cliente c\n"
                    + "inner join statuscliente s on s.codstatus = c.codstatus\n"
                    + "left join cidade cid on cid.codcidade = c.codcidaderes\n"
                    + "order by c.codcliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codcliente"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("rgie"));
                    imp.setOrgaoemissor(rst.getString("rgemissor"));
                    imp.setEndereco(rst.getString("enderres"));
                    imp.setNumero(rst.getString("numerores"));
                    imp.setComplemento(rst.getString("complementores"));
                    imp.setBairro(rst.getString("bairrores"));
                    imp.setCep(rst.getString("cepres"));
                    imp.setMunicipioIBGE(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("ufres"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("dtnascto"));
                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("sexo"))) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setTelefone(rst.getString("foneres"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setNomeConjuge(rst.getString("nomeconj"));

                    imp.setDataCadastro(rst.getDate("dtinclusao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite1"));
                    imp.setBloqueado("S".equals(rst.getString("bloqueado")));
                    imp.setPermiteCreditoRotativo(imp.isBloqueado());
                    imp.setPermiteCheque(imp.isBloqueado());
                    imp.setAtivo("N".equals(rst.getString("bloqueado")));

                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "SITE",
                                null,
                                null,
                                rst.getString("site").toLowerCase()
                        );
                    }
                    if ((rst.getString("faxfat") != null)
                            && (!rst.getString("faxfat").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAXFAT",
                                rst.getString("faxfat"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("faxent") != null)
                            && (!rst.getString("faxent").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FAXENT",
                                rst.getString("faxent"),
                                null,
                                null
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "l.codlancto as id,\n"
                    + "l.parcela,\n"
                    + "l.codparceiro as codcliente,\n"
                    + "l.valorparcela as valor,\n"
                    + "l.valorliquido,\n"
                    + "l.valorjuros,\n"
                    + "l.valordescto,\n"
                    + "l.valoracresc,\n"
                    + "l.dtemissao as dataemissao,\n"
                    + "l.dtvencto as datavencimento,\n"
                    + "l.numnotafis as numerocupom\n"
                    + "from lancamento l\n"
                    + "where l.pagrec = 'R' \n"
                    + "and l.status = 'A' \n"
                    + "and l.tipoparceiro = 'C'\n"
                    + "and l.codestabelec = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setJuros(rst.getDouble("valorjuros"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codcf codigo,\n"
                    + "('NOME: '||descricao||' CST:'||codcst||' ALIQ: '||aliqicms||' REDU: '||aliqredicms) as descricao, \n"
                    + "aliqicms, \n"
                    + "aliqredicms, \n"
                    + "codcst\n"
                    + "from classfiscal\n"
                    + "order by codcf"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
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
                    + "codestabelec codigo, \n"
                    + "razaosocial descricao \n"
                    + "from estabelecimento\n"
                    + "order by codestabelec"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }
}
