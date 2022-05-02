package com.javachinna.repo;

import com.javachinna.model.operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface operationRepository extends JpaRepository<operation,Integer> {

    @Query(value = "SELECT * FROM client c , operation p " +
            "WHERE p.RECIVING_CLIENT_ID=c.id AND c.email LIKE :mc ",nativeQuery = true)

    Page<operation> getOperationByReciver(Pageable pageable, @Param("mc") String mc);
}
