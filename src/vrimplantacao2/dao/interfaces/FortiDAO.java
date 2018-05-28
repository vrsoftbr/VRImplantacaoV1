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
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao.utils.Utils;
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

    @Override
    public String getSistema() {
        return "Forti";
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "G1 merc1, "
                    + "Nome merc1_desc \n"
                    + "from setor\n"
                    + "order by G1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_desc"));

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "G1 merc1, "
                    + "G2 merc2, "
                    + "Nome merc2_desc \n"
                    + "from setor\n"
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
                    "select "
                    + "G1 merc1, "
                    + "G2 merc2, "
                    + "'1' merc3, "
                    + "Nome merc2_desc, "
                    + "Nome merc3_desc \n"
                    + "from setor\n"
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
                    + "Codigo, \n"
                    + "Setor, \n"
                    + "G1, \n"
                    + "G2, \n"
                    + "'1' as G3, \n"
                    + "Nome, \n"
                    + "Un, \n"
                    + "Estoque, \n"
                    + "Minimo, \n"
                    + "Maximo,\n"
                    + "Custo, \n"
                    + "Venda, \n"
                    + "Icms, \n"
                    + "Data, \n"
                    + "Balanca, \n"
                    + "Validade, \n"
                    + "Descricao,\n"
                    + "MLucro1, \n"
                    + "MargUltRJ\n"
                    + "from PRODUTO\n"
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(Utils.formataNumero(Utils.formataNumero(rst.getString("Codigo"))));
                    imp.setEan(Utils.formataNumero(Utils.formataNumero(rst.getString("Codigo"))));
                    imp.seteBalanca("S".equals(rst.getString("Balanca")));
                    imp.setDescricaoCompleta(rst.getString("Descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("G1"));
                    imp.setCodMercadologico2(rst.getString("G2"));
                    imp.setCodMercadologico3(rst.getString("G3"));
                    imp.setTipoEmbalagem(rst.getString("Un"));
                    imp.setQtdEmbalagem(1);
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setMargem(rst.getDouble("MargUltRJ"));
                    imp.setPrecovenda(rst.getDouble("Venda"));
                    imp.setCustoComImposto(rst.getDouble("Custo"));
                    imp.setCustoSemImposto(rst.getDouble("Custo"));
                    imp.setEstoque(rst.getDouble("Estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("Minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("Maximo"));
                    imp.setIcmsDebitoId(rst.getString("Icms"));
                    imp.setIcmsCreditoId(rst.getString("Icms"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
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
                    imp.setFantasia(rst.getString("Nome"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("UF"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setTel_principal(rst.getString("Fone1"));
                    imp.setCnpj_cpf(rst.getString("CGC"));
                    imp.setIe_rg(rst.getString("Insc"));
                    imp.setObservacao(rst.getString("Obs"));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("Fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("Email") != null)
                            && (!rst.getString("Email").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("Email")
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

                    if ((rst.getString("Sexo") != null)
                            && (!rst.getString("Sexo").trim().isEmpty())) {

                        if ("M".equals(rst.getString("Sexo"))) {
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
                    + "from ALIQUOTA \n"
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
