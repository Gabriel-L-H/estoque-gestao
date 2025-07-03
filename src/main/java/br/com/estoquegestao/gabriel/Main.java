package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.CategoryDAO;
import br.com.estoquegestao.gabriel.dao.ProductDAO;
import br.com.estoquegestao.gabriel.model.Category;
import br.com.estoquegestao.gabriel.model.Product;
import br.com.estoquegestao.gabriel.model.Tipo;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        try {
            CategoryDAO categoryTest = new CategoryDAO();
            Category category = new Category();
            category.setId(2);
            category.setTipo(Tipo.ELETRONICO);
            category.setMarca("Acer");
            category.setFornecedor("Amazon");
            categoryTest.create(category);
            ProductDAO productTest = new ProductDAO();
            Product product = new Product();
            product.setId(12);
            product.setFk_categoria(2);
            product.setNome("Notebook Gamer Acer");
            product.setPreco(new BigDecimal(5899.99));
            product.setQuantidadeEstoque(4);
            productTest.create(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}