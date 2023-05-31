/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.Statement;
import vrframework.classe.Conexao;

/**
 *
 * @author Desenvolvimento
 */
class FornecedorEnderecoDAO {

    void gravarFornecedorEndereco(String sistema, String lojaOrigem) {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert\n"
                    + "	into\n"
                    + "	fornecedorendereco\n"
                    + "             (id_fornecedor,\n"
                    + "	id_tipoendereco,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	id_municipio,\n"
                    + "	id_estado,\n"
                    + "	id_pais,\n"
                    + "	inscricaosuframa,\n"
                    + "	inscricaoestadual,\n"
                    + "	inscricaomunicipal,\n"
                    + "	id_tipoindicadorie,\n"
                    + "	telefone,\n"
                    + "	complemento) (\n"
                    + "	select\n"
                    + "		id,\n"
                    + "		0,\n"
                    + "		endereco,\n"
                    + "		numero,\n"
                    + "		bairro,\n"
                    + "		cep,\n"
                    + "		id_municipio,\n"
                    + "		id_estado,\n"
                    + "		id_pais,\n"
                    + "		inscricaosuframa,\n"
                    + "		inscricaoestadual,\n"
                    + "		inscricaomunicipal,\n"
                    + "		id_tipoindicadorie,\n"
                    + "		telefone,\n"
                    + "		complemento\n"
                    + "	from\n"
                    + "		fornecedor\n"
                    + "	where\n"
                    + "		id not in (select id_fornecedor from fornecedorendereco))"
            );
            gravarFornecedorEnderecoCobranca();
        } catch (Exception e) {
            System.out.println("Erro na execução do script de inserção na classe FornecedorEnderecoDAO");
            e.printStackTrace();
        }
    }

    private void gravarFornecedorEnderecoCobranca() {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "insert\n"
                    + "	into\n"
                    + "	fornecedorendereco\n"
                    + "             (id_fornecedor,\n"
                    + "	id_tipoendereco,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	id_municipio,\n"
                    + "	complemento) (\n"
                    + "	select\n"
                    + "		id,\n"
                    + "		1 id_tipoendereco,\n"
                    + "		enderecocobranca,\n"
                    + "		numerocobranca,\n"
                    + "		bairrocobranca,\n"
                    + "		cepcobranca,\n"
                    + "		id_municipiocobranca,\n"
                    + "		complementocobranca\n"
                    + "	from\n"
                    + "		fornecedor\n"
                    + "	where\n"
                    + "		id not in (select id_fornecedor from fornecedorendereco where id_tipoendereco = 1))"
            );
        } catch (Exception e) {
            System.out.println("Erro na execução do script de inserção endereço cobrança na classe FornecedorEnderecoDAO");
            e.printStackTrace();
        }
    }

    void atualizarFornecedorEndereco() {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update\n"
                    + "	fornecedorendereco fe\n"
                    + "set \n"
                    + "	id_fornecedor = f.id,\n"
                    + "	id_tipoendereco = 0,\n"
                    + "	endereco = f.endereco,\n"
                    + "	numero = f.numero,\n"
                    + "	bairro = f.bairro,\n"
                    + "	cep = f.cep,\n"
                    + "	id_municipio = f.id_municipio,\n"
                    + "	id_estado = f.id_estado,\n"
                    + "	id_pais = f.id_pais,\n"
                    + "	inscricaosuframa = f.inscricaosuframa,\n"
                    + "	inscricaoestadual = f.inscricaoestadual,\n"
                    + "	inscricaomunicipal = f.inscricaomunicipal,\n"
                    + "	id_tipoindicadorie = f.id_tipoindicadorie,\n"
                    + "	telefone = f.telefone,\n"
                    + "	complemento = f.complemento\n"
                    + "from fornecedor f\n"
                    + "where fe.id_fornecedor = f.id and fe.id_tipoendereco = 0"
            );
            atualizarFornecedorEnderecoCobranca();
        } catch (Exception e) {
            System.out.println("Erro na execução do script de atualização na classe FornecedorEnderecoDAO");
            e.printStackTrace();
        }
    }

    private void atualizarFornecedorEnderecoCobranca() {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update\n"
                    + "	fornecedorendereco fe\n"
                    + "set \n"
                    + "	id_fornecedor = f.id,\n"
                    + "	id_tipoendereco = 1,\n"
                    + "	endereco = f.enderecocobranca,\n"
                    + "	numero = f.numerocobranca,\n"
                    + "	bairro = f.bairrocobranca,\n"
                    + "	cep = f.cepcobranca,\n"
                    + "	id_municipio = f.id_municipiocobranca,\n"
                    + "	complemento = f.complementocobranca\n"
                    + "from fornecedor f\n"
                    + "where fe.id_fornecedor = f.id and fe.id_tipoendereco = 1"
            );
        } catch (Exception e) {
            System.out.println("Erro na execução do script de atualização na classe FornecedorEnderecoDAO");
            e.printStackTrace();
        }
    }

}
