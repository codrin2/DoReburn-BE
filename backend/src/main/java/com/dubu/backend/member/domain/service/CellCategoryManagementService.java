package com.dubu.backend.member.domain.service;

import ch.hsr.geohash.GeoHash;
import com.dubu.backend.member.domain.model.Category;
import com.dubu.backend.member.domain.repository.CellCategoryMemberCountRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CellCategoryManagementService {
    private final CellCategoryMemberCountRepository cellCategoryMemberCountRepository;

    public void updateCellCategoryByLocationChange(GeoHash oldGeoHash, GeoHash newGeoHash, List<Category> categories){
        if(oldGeoHash == null){
            categories.forEach(category -> cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(newGeoHash.toBase32(), category.id(), 1));
        }else{
            if(oldGeoHash.compareTo(newGeoHash) > 0){
                for (Category category: categories) {
                    cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(newGeoHash.toBase32(), category.id(), 1);
                    cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(oldGeoHash.toBase32(), category.id(), -1);
                }
            }else{
                for(Category category: categories){
                    cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(oldGeoHash.toBase32(), category.id(), -1);
                    cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(newGeoHash.toBase32(), category.id(), 1);
                }
            }
        }
    }
    public void updateCellCategoryByPlanChange(Point location, List<Long> beforeCategoryIds, List<Long> recentCategoryIds){
        GeoHash geoHash = GeoHash.withCharacterPrecision(location.getY(), location.getX(), 7);

        Map<Long, Integer> categoryChangeAmountMap = calculateCategoryChangeAmount(beforeCategoryIds, recentCategoryIds);

        categoryChangeAmountMap.entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> cellCategoryMemberCountRepository.adjustCellCategoryMemberCount(geoHash.toBase32(), entry.getKey(), entry.getValue()));
    }

    private Map<Long, Integer> calculateCategoryChangeAmount(List<Long> categoryIdsBeforeTodos, List<Long> categoryIdsOfRecentTodos){
        Map<Long, Integer> categoryChangeAmountMap = new HashMap<>();

        categoryIdsBeforeTodos.forEach(id -> categoryChangeAmountMap.merge(id, -1, (existingValue, newValue) -> existingValue - 1));
        categoryIdsOfRecentTodos.forEach(id -> categoryChangeAmountMap.merge(id, 1, (existingValue, newValue) -> existingValue + 1));

        categoryChangeAmountMap.entrySet().removeIf(entry -> entry.getValue() == 0);

        return categoryChangeAmountMap;
    }


}
