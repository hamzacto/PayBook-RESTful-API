package com.javachinna.repo;


import com.javachinna.model.Vendor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "vendors", path = "vendors")
public interface VendorRepository extends PagingAndSortingRepository<Vendor,Long> {
    List<Vendor> findByName(@Param("name") String name);

    @Override
    Optional<Vendor> findById(Long aLong);

    Optional<Vendor> findByEmail(@Param("email") String email);
}
