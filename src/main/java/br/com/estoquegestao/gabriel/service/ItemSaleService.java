package br.com.estoquegestao.gabriel.service;

import br.com.estoquegestao.gabriel.dao.ItemSaleDAO;
import br.com.estoquegestao.gabriel.dao.ProductDAO;
import br.com.estoquegestao.gabriel.dao.SaleDAO;
import br.com.estoquegestao.gabriel.model.ItemSale;
import br.com.estoquegestao.gabriel.model.Product;
import br.com.estoquegestao.gabriel.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class ItemSaleService {
    private final ItemSaleDAO itemSaleDAO;
    private final ProductDAO productDAO;
    private final SaleDAO saleDAO;
    private static final Logger logger = LoggerFactory.getLogger(ItemSaleService.class);

    public ItemSaleService(ItemSaleDAO itemSaleDAO, ProductDAO productDAO, SaleDAO saleDAO) {
        this.itemSaleDAO = itemSaleDAO;
        this.productDAO = productDAO;
        this.saleDAO = saleDAO;
    }

    public void update(ItemSale itemSale) throws SQLException {
        ItemSale itemInBase = this.itemSaleDAO.findItemSale(itemSale.getId()).get();

        Optional<Product> optionalProduct = this.productDAO.findProduct(itemInBase.getFk_product());
        if (optionalProduct.isEmpty()) {
            logger.warn("Product with ID {} not found", itemInBase.getFk_product());
            throw new IllegalArgumentException("Product not found");
        }

        Product product = optionalProduct.get();
        if (itemSale.getQuantity() > product.getQuantityStock()){
            logger.warn("Exceed quantity in stock");
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }

        int oldQuantity = itemInBase.getQuantity();
        int newQuantity = itemSale.getQuantity() - oldQuantity;
        int stockProduct = this.productDAO.findProduct(itemInBase.getFk_product()).get().getQuantityStock();

        if (newQuantity != 0){
            Sale sale = this.saleDAO.findSale(itemInBase.getFk_sale()).get();

            BigDecimal oldSalePrice = this.saleDAO.findSale(sale.getId()).get().getTotalValue();
            BigDecimal oldItemPrice = this.itemSaleDAO.findItemSale(itemInBase.getId()).get().getPrice();

            BigDecimal productValue = product.getPrice();

            product.setId(itemInBase.getFk_product());
            product.setQuantityStock(stockProduct - newQuantity);
            itemSale.setPrice(productValue.multiply(BigDecimal.valueOf(itemSale.getQuantity())));

            BigDecimal newItemPrice = itemSale.getPrice();
            BigDecimal newSalePrice = (oldSalePrice.subtract(oldItemPrice)).add(newItemPrice);

            if(newItemPrice.divide(BigDecimal.valueOf(itemSale.getQuantity())).compareTo(productValue) != 0){
                logger.warn("Prices of itemSale and Products don't match");
                throw new IllegalArgumentException("Prices between itemSale and product different");
            }

            sale.setTotalValue(newSalePrice);

            this.productDAO.update(product);
            this.itemSaleDAO.update(itemSale);
            this.saleDAO.update(sale);
        }
    }
}