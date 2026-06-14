package com.mediai.service;

import com.mediai.entity.Medicine;
import com.mediai.entity.MedicineCategory;
import com.mediai.repository.MedicineCategoryRepository;
import com.mediai.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {
    @Autowired
    private MedicineRepository medicineRepo;
    @Autowired
    private MedicineCategoryRepository categoryRepo;

    public List<Medicine> getAll() { return medicineRepo.findAll(); }

    public Optional<Medicine> findById(long id) { return medicineRepo.findById(id); }

    public List<Medicine> findByCategory(int catId) { return medicineRepo.findByCategoryId(catId); }

    public Medicine save(Medicine m) { return medicineRepo.save(m); }

    public void deleteById(long id) { medicineRepo.deleteById(id); }

    public List<MedicineCategory> getAllCategories() { return categoryRepo.findAll(); }

    public Optional<MedicineCategory> findCategoryById(int id) { return categoryRepo.findById(id); }

    public MedicineCategory saveCategory(MedicineCategory c) { return categoryRepo.save(c); }

    public void deleteCategoryById(int id) { categoryRepo.deleteById(id); }

    /**
     * Given a list of medicine names from AI, find matching medicines in DB.
     * Searches by name, brand, and tags.
     */
    public List<Medicine> findByAISuggestions(List<String> names) {
        List<Medicine> result = new ArrayList<>();
        for (String name : names) {
            List<Medicine> found = medicineRepo.searchByKeyword(name.trim());
            for (Medicine m : found) {
                if (!result.stream().anyMatch(r -> r.getId() == m.getId())) {
                    result.add(m);
                }
            }
        }
        return result;
    }
}
