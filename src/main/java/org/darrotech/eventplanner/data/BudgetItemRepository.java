package org.darrotech.eventplanner.data;

import org.darrotech.eventplanner.models.BudgetItems;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemRepository extends CrudRepository<BudgetItems, Integer> {
    List<BudgetItemRepository> findByUserId(int userId);//for complete authentication
}
