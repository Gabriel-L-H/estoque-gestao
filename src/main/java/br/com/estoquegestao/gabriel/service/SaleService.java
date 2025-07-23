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
import java.util.List;
import java.util.Optional;

public class SaleService {
    private final SaleDAO saleDAO;
    private final ItemSaleDAO itemSaleDAO;
    private final ProductDAO productDAO;


    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    public SaleService(SaleDAO saleDAO, ItemSaleDAO itemSaleDAO, ProductDAO productDAO) {
        this.saleDAO = saleDAO;
        this.itemSaleDAO = itemSaleDAO;
        this.productDAO = productDAO;
    }

    public void create(Sale sale, List<ItemSale> itemSales) throws SQLException {
        for (ItemSale item : itemSales){
            Optional<Product> optionalProduct = this.productDAO.findProduct(item.getFk_product());
            if (optionalProduct.isEmpty()) {
                logger.warn("Product with ID {} not found", item.getFk_product());
                throw new IllegalArgumentException("Product not found");
            }

            Product product = optionalProduct.get();
            BigDecimal productValue = product.getPrice();

            item.setPrice(productValue.multiply(BigDecimal.valueOf(item.getQuantity())));
            BigDecimal priceItemSale = item.getPrice();

            if(priceItemSale.divide(BigDecimal.valueOf(item.getQuantity())).compareTo(productValue) != 0){
                logger.warn("Prices of itemSale and Products don't match");
                throw new IllegalArgumentException("Prices between itemSale and product different");
            }

            int stockProduct = product.getQuantityStock();
            if (item.getQuantity() > stockProduct){
                logger.warn("Exceed quantity in stock");
                throw new IllegalArgumentException("Quantity exceeds available stock");
            }
            int newStock = stockProduct - item.getQuantity();

            product.setId(item.getFk_product());
            product.setQuantityStock(newStock);
            this.productDAO.update(product);
        }

        sale.setTotalValue(sumItemsSale(itemSales));
        int saleId = this.saleDAO.create(sale);

        itemSales.forEach(item -> item.setFk_sale(saleId));

        this.itemSaleDAO.create(itemSales);
    }

    public BigDecimal sumItemsSale(List<ItemSale> itemSales){
        BigDecimal sum = BigDecimal.ZERO;
        for (ItemSale item : itemSales){
            if (item != null){
                sum = sum.add(item.getPrice());
            }
        }
        return sum;
    }
}
