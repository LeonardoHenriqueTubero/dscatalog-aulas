package br.com.leonardo.dscatalog.services;

import br.com.leonardo.dscatalog.dto.ProductDTO;
import br.com.leonardo.dscatalog.entities.Product;
import br.com.leonardo.dscatalog.repositories.ProductRepository;
import br.com.leonardo.dscatalog.service.ProductService;
import br.com.leonardo.dscatalog.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void findAllShouldReturnPageWhenPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDTO> page = service.findAll(pageRequest);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(10, page.getSize());
        Assertions.assertEquals(countTotalProducts, page.getTotalElements());
    }

    @Test
    public void findAllShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDTO> page = service.findAll(pageRequest);

        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    public void findAllShouldReturnSortedPageWhenPageSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDTO> page = service.findAll(pageRequest);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
    }
}
