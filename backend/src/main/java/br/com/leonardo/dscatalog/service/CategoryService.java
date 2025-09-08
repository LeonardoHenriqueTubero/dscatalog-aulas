package br.com.leonardo.dscatalog.service;

import br.com.leonardo.dscatalog.dto.CategoryDTO;
import br.com.leonardo.dscatalog.entities.Category;
import br.com.leonardo.dscatalog.repositories.CategoryRepository;
import br.com.leonardo.dscatalog.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    @Autowired
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll();
        return result.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category result = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entidade não encontrada"));
        return new CategoryDTO((result));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());

        category = repository.save(category);

        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category category = repository.getReferenceById(id);
            category.setName(dto.getName());

            category = repository.save(category);

            return new CategoryDTO(category);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado, " + id);
        }
    }
}
