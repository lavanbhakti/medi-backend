package com.mediai.controller;

import com.mediai.entity.Medicine;
import com.mediai.entity.MedicineCategory;
import com.mediai.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@RestController
public class MedicineController {

    @Autowired private MedicineService medicineService;

    public static String uploadDir =
        System.getProperty("user.dir") + "/src/main/resources/static/medicineImages";

    // ── PUBLIC MEDICINE ENDPOINTS ─────────────────────────────

    @GetMapping("/api/medicines")
    public List<Medicine> getAll() { return medicineService.getAll(); }

    @GetMapping("/api/medicines/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        return medicineService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/medicines/category/{catId}")
    public List<Medicine> getByCategory(@PathVariable int catId) {
        return medicineService.findByCategory(catId);
    }

    // ── PUBLIC CATEGORY ENDPOINTS ─────────────────────────────

    @GetMapping("/api/categories")
    public List<MedicineCategory> getAllCategories() {
        return medicineService.getAllCategories();
    }

    // ── ADMIN MEDICINE CRUD ───────────────────────────────────

    @PostMapping("/api/admin/medicines")
    public ResponseEntity<?> addMedicine(
            @RequestParam("name") String name,
            @RequestParam("brand") String brand,
            @RequestParam("price") double price,
            @RequestParam("dosage") String dosage,
            @RequestParam("description") String description,
            @RequestParam("stock") int stock,
            @RequestParam("tags") String tags,
            @RequestParam("categoryId") int categoryId,
            @RequestParam(value = "productImage", required = false) MultipartFile file,
            @RequestParam(value = "imgName", required = false, defaultValue = "") String imgName,
            @RequestParam(value = "id", required = false, defaultValue = "0") long id
    ) throws IOException {
        Medicine m = new Medicine();
        m.setId(id);
        m.setName(name);
        m.setBrand(brand);
        m.setPrice(price);
        m.setDosage(dosage);
        m.setDescription(description);
        m.setStock(stock);
        m.setTags(tags);
        m.setCategory(medicineService.findCategoryById(categoryId).orElseThrow());

        String imageFile = imgName;
        if (file != null && !file.isEmpty()) {
            imageFile = file.getOriginalFilename();
            Path path = Paths.get(uploadDir, imageFile);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        }
        m.setImageName(imageFile);
        medicineService.save(m);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/api/admin/medicines/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable long id) {
        medicineService.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── ADMIN CATEGORY CRUD ───────────────────────────────────

    @PostMapping("/api/admin/categories")
    public ResponseEntity<?> addCategory(@RequestBody MedicineCategory c) {
        medicineService.saveCategory(c);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody MedicineCategory c) {
        c.setId(id);
        medicineService.saveCategory(c);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        medicineService.deleteCategoryById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
