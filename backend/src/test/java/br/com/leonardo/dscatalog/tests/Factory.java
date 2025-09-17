package br.com.leonardo.dscatalog.tests;

import br.com.leonardo.dscatalog.dto.CategoryDTO;
import br.com.leonardo.dscatalog.dto.ProductDTO;
import br.com.leonardo.dscatalog.entities.Category;
import br.com.leonardo.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "img", Instant.now());
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(1L, "Toys");
    }

    public static CategoryDTO createCategoryDTO() {
        Category category = createCategory();
        return new CategoryDTO(category);
    }
}
