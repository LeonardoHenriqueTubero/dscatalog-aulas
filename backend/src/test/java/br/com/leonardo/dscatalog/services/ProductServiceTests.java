package br.com.leonardo.dscatalog.services;

import br.com.leonardo.dscatalog.dto.ProductDTO;
import br.com.leonardo.dscatalog.entities.Category;
import br.com.leonardo.dscatalog.entities.Product;
import br.com.leonardo.dscatalog.repositories.CategoryRepository;
import br.com.leonardo.dscatalog.repositories.ProductRepository;
import br.com.leonardo.dscatalog.service.ProductService;
import br.com.leonardo.dscatalog.service.exceptions.DatabaseException;
import br.com.leonardo.dscatalog.service.exceptions.ResourceNotFoundException;
import br.com.leonardo.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when((repository.findById(existingId))).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doNothing().when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.doThrow(EntityNotFoundException.class).when(categoryRepository).getReferenceById(nonExistingId);
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO productDTO = service.findById(existingId);

        Assertions.assertNotNull(productDTO);
        Mockito.verify(repository).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           ProductDTO productDTO = service.findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenExistingId() {
        ProductDTO dto = service.update(existingId, productDTO);

        Assertions.assertNotNull(dto);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(categoryRepository).getReferenceById(existingId);
        Mockito.verify(repository).save(product);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           ProductDTO dto = service.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenExistingId() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
