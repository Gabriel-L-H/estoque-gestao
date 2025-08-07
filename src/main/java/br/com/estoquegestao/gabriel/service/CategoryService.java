package br.com.estoquegestao.gabriel.service;

import br.com.estoquegestao.gabriel.dao.CategoryDAO;
import br.com.estoquegestao.gabriel.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class CategoryService {
    private final CategoryDAO categoryDAO;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public void create(Category category) throws SQLException {
        Optional<Category> categoryInDB = this.categoryDAO.findCategoryByBrand(category.getBrand());
        if (!categoryInDB.isEmpty()){
            Category categoryFound = categoryInDB.get();

            if (categoryFound.getType().equals(category.getType()) &&
                categoryFound.getSupplier().equals(category.getSupplier()) &&
                categoryFound.getBrand().equals(category.getBrand())) {
                logger.warn("Category already exists in the database: {}", categoryFound);
                throw new SQLException("Category already exists in the database");
            } else {
                this.categoryDAO.create(category);
                logger.info("Category created successfully");
            }
        }
        else{
            this.categoryDAO.create(category);
            logger.info("Category created successfully");
        }
    }
}