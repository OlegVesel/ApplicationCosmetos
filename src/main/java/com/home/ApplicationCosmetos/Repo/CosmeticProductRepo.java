package com.home.ApplicationCosmetos.Repo;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CosmeticProductRepo extends CrudRepository<CosmeticProduct, Long> {
    Page<CosmeticProduct> findByNameAndBrandAndOwner(String name, String brand, User owner, Pageable pageable);

    Page<CosmeticProduct> findByNameAndOwner(String name, User owner, Pageable pageable);

    Page<CosmeticProduct> findByBrandAndOwner(String brand, User owner, Pageable pageable);

    Page<CosmeticProduct> findByOwner(User owner, Pageable pageable);


    @Query(value = "select distinct trim(cp.name) as name from cosmetic_product cp" +
            " where cp.userid = ?1 order by name", nativeQuery = true)
    Iterable<String> distinctName(Long id);

    @Query(value = "select distinct trim(cp.brand) as brand from cosmetic_product cp " +
            "where cp.userid = ?1 order by brand", nativeQuery = true)
    Iterable<String> distinctBrand(Long id);

}