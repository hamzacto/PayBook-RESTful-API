package com.javachinna.repo;

import com.javachinna.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CardRepository extends JpaRepository<Card,Integer> {

    @Query(value = "SELECT * FROM card c WHERE c.number = :number",nativeQuery = true)
    Optional<Card> findByNumber(@Param("number") Long Number);

    @Query(value = "SELECT * FROM CARD c WHERE c.OWNER_ID = :id  ",nativeQuery = true)
    List<Card> getByClient(@Param("id") Integer id);

}
