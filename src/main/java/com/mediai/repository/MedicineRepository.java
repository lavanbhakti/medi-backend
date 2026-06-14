package com.mediai.repository;
import com.mediai.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByCategoryId(int categoryId);

    // Search by name, brand or tags containing keyword
    @Query("SELECT m FROM Medicine m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(m.brand) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(m.tags) LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<Medicine> searchByKeyword(@Param("kw") String keyword);
}
