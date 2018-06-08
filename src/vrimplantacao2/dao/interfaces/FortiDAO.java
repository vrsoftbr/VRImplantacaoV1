/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.remote.Arquivo;
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class FortiDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "Forti";
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Mostrag as merc1, \n"
                    + "Nome as merc1_desc\n"
                    + "from setor\n"
                    + "where Mostrag = G1\n"
                    + "and G2 = '000'\n"
                    + "order by Mostrag"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    byte[] bytes = rst.getBytes("merc1_desc");
                    String descricao = new String(bytes, "UTF-8");

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(descricao);

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "G1 as merc1,\n"
                    + "G2 as merc2,\n"
                    + "Nome as merc2_desc\n"
                    + "from setor\n"
                    + "where G2 <> '000'\n"
                    + "order by G1, G2"
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
                    "select\n"
                    + "G1 as merc1,\n"
                    + "G2 as merc2,\n"
                    + "'1' as merc3, \n"
                    + "Nome as merc3_desc\n"
                    + "from setor\n"
                    + "where G2 <> '000'\n"
                    + "order by G1, G2"
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
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo as id, \n"
                    + "Codigo as ean, \n"
                    + "Setor, \n"
                    + "G1, \n"
                    + "G2, \n"
                    + "Nome, \n"
                    + "Un, \n"
                    + "Estoque, \n"
                    + "Minimo, \n"
                    + "Maximo,\n"
                    + "Compra as custocomimposto, \n"
                    + "Compra as custosemimposto, \n"
                    + "Venda, \n"
                    + "Icms as icmsdebito, \n"
                    + "Icms as icmscredito, \n"
                    + "Data, \n"
                    + "Balanca, \n"
                    + "Validade, \n"
                    + "Descricao,\n"
                    + "Porc as margem \n"
                    + "from PRODUTO\n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca("S".equals(rst.getString("Balanca")));
                    imp.setDescricaoCompleta(rst.getString("Nome"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("G1"));
                    imp.setCodMercadologico2(rst.getString("G2"));
                    imp.setCodMercadologico3("1");
                    imp.setTipoEmbalagem(rst.getString("Un"));
                    imp.setQtdEmbalagem(1);
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("Venda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setEstoque(rst.getDouble("Estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Maximo"));
                    imp.setIcmsDebitoId(rst.getString("icmsdebito"));
                    imp.setIcmsCreditoId(rst.getString("icmscredito"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Arquivo arquivo = new Arquivo(i_arquivo, "r", "utf-8");
        String linha;

        if (opcao == OpcaoProduto.DESC_COMPLETA) {

            while (arquivo.ready()) {
                linha = arquivo.readLine();
                
                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(linha.substring(0, 15).trim());
                imp.setDescricaoCompleta(linha.substring(26, 66));
                result.add(imp);
            }
            return result;
        }
        if (opcao == OpcaoProduto.DESC_REDUZIDA) {

            while (arquivo.ready()) {
                linha = arquivo.readLine();

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(linha.substring(0, 15).trim());
                imp.setDescricaoReduzida(linha.substring(26, 66));
                result.add(imp);
            }
            return result;

        }
        if (opcao == OpcaoProduto.DESC_GONDOLA) {

            while (arquivo.ready()) {
                linha = arquivo.readLine();

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(linha.substring(0, 15).trim());
                imp.setDescricaoGondola(linha.substring(26, 66));
                result.add(imp);
            }
            return result;
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "Codigo,\n"
                    + "Nome,\n"
                    + "Endereco,\n"
                    + "Bairro,\n"
                    + "Cidade,\n"
                    + "UF,\n"
                    + "Cep,\n"
                    + "Fone1,\n"
                    + "Fone2,\n"
                    + "CGC,\n"
                    + "Insc,\n"
                    + "Obs,\n"
                    + "Contato,\n"
                    + "Email\n"
                    + "from FORNECE\n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setTel_principal(rst.getString("Fone1"));
                    imp.setCnpj_cpf(rst.getString("CGC"));
                    imp.setIe_rg(rst.getString("Insc"));
                    imp.setObservacao(rst.getString("Obs"));

                    String fone2 = rst.getString("Fone2");
                    String email = rst.getString("Email");

                    if ((fone2 != null)
                            && (!fone2.trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                fone2,
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((email != null)
                            && (!email.trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                email
                        );
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
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo, \n"
                    + "Nome, \n"
                    + "Endereco, \n"
                    + "Bairro, \n"
                    + "Cidade,\n"
                    + "UF, \n"
                    + "Cep, \n"
                    + "Fone1, \n"
                    + "Fone2, \n"
                    + "Contato, \n"
                    + "RG, \n"
                    + "Cpf, \n"
                    + "IE,\n"
                    + "Datanasc, \n"
                    + "Datacad, \n"
                    + "Credito, \n"
                    + "Divida, \n"
                    + "Email, \n"
                    + "Status,\n"
                    + "Obs, \n"
                    + "Profissao, \n"
                    + "Sexo, \n"
                    + "PontoRef, \n"
                    + "PontoRef2, \n"
                    + "EnderecoEnt, \n"
                    + "BairroEnt, \n"
                    + "CidadeEnt, \n"
                    + "UFEnt, \n"
                    + "CepEnt,\n"
                    + "PontoRefEnt, \n"
                    + "Fantasia, \n"
                    + "Senha\n"
                    + "from CLIENTE\n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("Fantasia"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCnpj(rst.getString("Cpf"));
                    imp.setInscricaoestadual(rst.getString("RG"));
                    imp.setCargo(rst.getString("Profissao"));
                    imp.setValorLimite(rst.getDouble("Credito"));
                    imp.setTelefone(rst.getString("Fone1"));
                    imp.setFax(rst.getString("Fone2"));
                    imp.setEmail(rst.getString("Email"));
                    imp.setDataCadastro(rst.getDate("Datacad"));
                    imp.setDataNascimento(rst.getDate("Datanasc"));
                    imp.setObservacao(rst.getString("Obs"));

                    String sexo = rst.getString("Sexo");

                    if ((sexo != null)
                            && (!sexo.trim().isEmpty())) {

                        if ("M".equals(sexo)) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
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
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Codigo, \n"
                    + "Porc, \n"
                    + "Descricao \n"
                    + "from ALIQUOTA.DB \n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("Codigo"), rst.getString("Porc") + " " + rst.getString("Descricao")));
                }
            }
            return result;
        }
    }
}
