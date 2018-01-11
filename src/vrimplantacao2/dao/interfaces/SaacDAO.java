package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialDAO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class SaacDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Saac";
    }

    public List<MercadologicoIMP> getMercadologico() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.codigo, upper(m1.descricao) descricao\n"
                    + "from grupo m1\n"
                    + "order by m1.codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codigo, p.barras, upper(p.descricao) descricao,\n"
                    + "upper(p.descricaopdv) descricaoreduzida, p.prcvenda, p.grupo,\n"
                    + "p.subgrupo, p.compraprccusto, p.compraultimopreco, p.compraprccustomedio,\n"
                    + "p.ativo,p.dtcadastro, p.margem, p.ncm,p.piscstent, p.cofinscstent,\n"
                    + "p.piscstsai, p.cofinscstsai, p.natrec,p.cst, p.cstent, a.indice,\n"
                    + "p.cest, p.familia,upper(u.descricaoresumida) as tipoembalagem, "
                    + "estoque,\n"
                    + "estoquemin,\n"
                    + "estoquemax\n"
                    + "from itens p\n"
                    + "inner join aliquotas a on a.codigo = p.aliquota\n"
                    + "inner join unidademedida u on u.codigo = p.unidademedida\n"
                    + "order by codigo"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    int cstSaida = rst.getInt("cst");
                    int cstEntrada = rst.getInt("cstent");
                    if (cstSaida > 9) {
                        cstSaida = Integer.parseInt(String.valueOf(cstSaida).substring(0, 2));
                    }
                    if (cstEntrada > 9) {
                        cstEntrada = Integer.parseInt(String.valueOf(cstEntrada).substring(0, 2));
                    }

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("barras"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("grupo"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(1);
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("prcvenda"));
                    imp.setCustoComImposto(rst.getDouble("compraprccusto"));
                    imp.setCustoSemImposto(rst.getDouble("compraultimopreco"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscstsai"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscstent"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("natrec"));
                    imp.setIcmsCstSaida(cstSaida);
                    imp.setIcmsCstEntrada(cstEntrada);
                    imp.setIcmsAliqSaida(rst.getDouble("indice"));
                    imp.setIcmsAliqEntrada(rst.getDouble("indice"));
                    imp.setIcmsReducaoSaida(0);
                    imp.setIcmsReducaoEntrada(0);
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select f.codfornecedor, upper(f.fornecedor) fantasia, "
                    + "upper(f.razaosocial) razao, f.cgc, f.inscricaoest, "
                    + "f.ativo, f.produtor, f.endereco, f.bairro, f.cep, "
                    + "f.telfixo, f.telfax, f.observacao, f.email, f.site, "
                    + "f.datacadastro, f.endereconum, f.enderecocomp,\n"
                    + "upper(l.cidade) cidade, upper(l.estado) estado\n"
                    + "from fornecedores f\n"
                    + "left join localidades l on l.codigo = f.localidade"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfornecedor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("inscricaoest"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("endereconum"));
                    imp.setComplemento(rst.getString("enderecocomp"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("telfixo"));
                    imp.setObservacao(rst.getString("observacao"));
                    if (Utils.stringToLong(rst.getString("telfax")) > 0) {
                        FornecedorContatoIMP cont = new FornecedorContatoIMP();
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTelefone(rst.getString("telfax"));
                        imp.getContatos().put(cont, "1");
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setEmail(rst.getString("email"));
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportSistema(getSistema());
                        cont.setImportLoja(getLojaOrigem());
                        cont.setImportId("3");
                        cont.setNome("SITE");
                        cont.setEmail(rst.getString("site"));
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "fornecedor,\n"
                    + "produto,\n"
                    + "codigofornecedor\n"
                    + "from vinculoitensfornec"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("fornecedor"));
                    imp.setIdProduto(rst.getString("produto"));
                    imp.setCodigoExterno(rst.getString("codigofornecedor"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select c.codigo, upper(c.nome) nome, upper(c.razaosocial) razao, c.cgccpf, c.rg,\n"
                    + "c.inscest, c.situacao, c.tipo, c.estcivil, c.sexo, c.dtnasc, c.endereco, c.endereconum,\n"
                    + "c.enderecocomp, c.bairro, upper(l.cidade) cidade, upper(l.estado) estado, c.cep,\n"
                    + "c.foneres, c.fonecel, c.filiacaopai, c.filiacaomae, c.email, c.empnome, c.empfone,\n"
                    + "c.empendereco, c.empbairro, upper(lo.cidade) cidadeEmpresa, upper(lo.estado) estadoEmpresa,\n"
                    + "c.empfuncao, c.empdtadmissao, c.empgerente, c.emprenda, c.conjnome, c.conjdtnasc, c.conjrg,\n"
                    + "c.conjcgccpf, c.conjempnome, c.conjempfone, c.conjempendereco, c.conjempfuncao, c.conjempdtadmissao,\n"
                    + "c.conjemprenda, c.limitecheque, c.limitecrediario, c.credpossui, c.creddiavenc, c.credsituacao,\n"
                    + "c.dtcad, c.ativo, c.contato, c.observacoes\n"
                    + "from client c\n"
                    + "left join localidades l on l.codigo = c.localidade\n"
                    + "left join localidades lo on lo.codigo = c.emplocalidade\n"
                    + "left join localidades lo2 on lo2.codigo = c.conjemploc"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cgccpf"));
                    imp.setInscricaoestadual(("F".equals(rst.getString("tipo")) ? rst.getString("rg") : rst.getString("inscest")));
                    imp.setAtivo("A".equals(rst.getString("situacao")));
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

                    if ((rst.getString("estcivil") != null)
                            && (!rst.getString("estcivil").trim().isEmpty())) {
                        if (null != rst.getString("estcivil")) {
                            switch (rst.getString("estcivil")) {
                                case "A":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "C":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "D":
                                    imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                    break;
                                case "S":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "V":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("endereconum"));
                    imp.setComplemento(rst.getString("enderecocomp"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("foneres"));
                    imp.setCelular(rst.getString("fonecel"));
                    imp.setNomePai(rst.getString("filiacaopai"));
                    imp.setNomeMae(rst.getString("filiacaomae"));
                    imp.setEmail(rst.getString("email"));
                    imp.setEmpresa(rst.getString("empnome"));
                    imp.setEmpresaTelefone(rst.getString("empfone"));
                    imp.setEmpresaEndereco(rst.getString("empendereco"));
                    imp.setEmpresaBairro(rst.getString("empbairro"));
                    imp.setEmpresaMunicipio(rst.getString("cidadeEmpresa"));
                    imp.setEmpresaUf(rst.getString("estadoEmpresa"));
                    imp.setCargo(rst.getString("empfuncao"));
                    imp.setDataAdmissao(rst.getDate("empdtadmissao"));
                    imp.setSalario(rst.getDouble("emprenda"));
                    imp.setNomeConjuge(rst.getString("conjnome"));
                    imp.setValorLimite(rst.getDouble("limitecrediario"));
                    imp.setDataCadastro(rst.getDate("dtcad"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));
                    if ((rst.getString("credsituacao") != null)
                            && (!rst.getString("credsituacao").trim().isEmpty())) {
                        if ("L".equals(rst.getString("credsituacao"))) {
                            imp.setBloqueado(false);
                        } else {
                            imp.setBloqueado(true);
                        }
                    } else {
                        imp.setBloqueado(true);
                    }

                    if ((rst.getString("credpossui") != null)
                            && (!rst.getString("credpossui").trim().isEmpty())) {
                        if ("S".equals(rst.getString("credpossui"))) {
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        } else {
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                        }
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, cliente, docnum, parcelanum, valor, "
                    + "datadocumento, datavencimento, caixa\n"
                    + "from finctreceber\n"
                    + "where baixa = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("cliente"));
                    imp.setNumeroCupom(rst.getString("docnum"));
                    imp.setParcela(rst.getInt("parcelanum"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataEmissao(rst.getDate("datadocumento"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setEcf(rst.getString("caixa"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        long cnpj;
        int codigoAtual;
        String nome, telefone;
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, cliente, cpf, nome, valor, banco, cheque, "
                    + "telefone, caixa, devolvido, datadevolucao, alinea, "
                    + "datainclusao, datavencimento, observacoes, cupom\n"
                    + "from finchequerec\n"
                    + "where baixa = 'N'"
            )) {
                while (rst.next()) {
                    cnpj = new ClientePreferencialDAO().getCnpjByCodAnt(rst.getString("cliente"), getLojaOrigem());
                    codigoAtual = new ClientePreferencialDAO().getIdByCodAnt(rst.getString("cliente"), getLojaOrigem());
                    nome = new ClientePreferencialDAO().getNomeByCodAnt(rst.getString("cliente"), getLojaOrigem());
                    telefone = new ClientePreferencialDAO().getTelefoneByCodAnt(rst.getString("cliente"), getLojaOrigem());

                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCpf(rst.getString("cpf").trim());
                    } else {
                        imp.setCpf(String.valueOf(cnpj));
                    }
                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        imp.setNome(rst.getString("nome").trim());
                    } else {
                        imp.setNome(nome);
                    }
                    if ((rst.getString("telefone") != null)
                            && (!rst.getString("telefone").trim().isEmpty())) {
                        imp.setTelefone(rst.getString("telefone").trim());
                    } else {
                        imp.setTelefone(telefone);
                    }
                    if ((rst.getString("banco") != null)
                            && (!rst.getString("banco").trim().isEmpty())) {
                        imp.setBanco(new BancoDAO().getId(rst.getInt("banco")));
                    } else {
                        imp.setBanco(804);
                    }
                    if ((rst.getString("devolvido") != null)
                            && (!rst.getString("devolvido").trim().isEmpty())) {
                        if ("N".equals(rst.getString("devolvido").trim())) {
                            if ((rst.getString("alinea") != null)
                                    && (!rst.getString("alinea").trim().isEmpty())) {
                                imp.setAlinea(rst.getInt("alinea"));
                            } else {
                                imp.setAlinea(0);
                            }
                        } else {
                            if ((rst.getString("alinea") != null)
                                    && (!rst.getString("alinea").trim().isEmpty())) {
                                imp.setAlinea(rst.getInt("alinea"));
                            } else {
                                imp.setAlinea(11);
                            }
                        }
                    } else {
                        imp.setAlinea(0);
                    }
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDate(rst.getDate("datainclusao"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setObservacao(rst.getString("observacoes"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
