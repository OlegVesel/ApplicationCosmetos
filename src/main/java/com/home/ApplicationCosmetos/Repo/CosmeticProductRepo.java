package com.home.ApplicationCosmetos.Repo;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CosmeticProductRepo extends CrudRepository<CosmeticProduct, Long> {
    List<CosmeticProduct> findByNameAndBrandAndOwner(String name, String brand, User owner);

    List<CosmeticProduct> findByNameAndOwner(String name, User owner);

    List<CosmeticProduct> findByBrandAndOwner(String brand, User owner);

    Page<CosmeticProduct> findByOwner(User owner, Pageable pageable);

    List<CosmeticProduct> findByNameLikeAndBrandLike(String name, String brand);

    List<CosmeticProduct> findByName(String name);

    List<CosmeticProduct> findByBrand(String brand);

    @Query(value = "select distinct trim(cp.name) as name from cosmetic_product cp" +
            " where cp.userid = ?1 order by name", nativeQuery = true)
    Iterable<String> distinctName(Long id);

    @Query(value = "select distinct trim(cp.brand) as brand from cosmetic_product cp " +
            "where cp.userid = ?1 order by brand", nativeQuery = true)
    Iterable<String> distinctBrand(Long id);

}