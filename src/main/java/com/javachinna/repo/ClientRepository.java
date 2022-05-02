package com.javachinna.repo;


import com.javachinna.model.Card;
import com.javachinna.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Integer> {

    @Query(value="SELECT * FROM CLIENT c WHERE c.email= :email",nativeQuery = true)
    Optional<Client> GetClientByEmail(@Param("email") String email);

    @Modifying
    @Query(value = "UPDATE client c SET c.balance = :balance WHERE c.id= :id",nativeQuery = true)
    Void setBalance(@Param("id") Integer id  ,  @Param("balance") Double balance);

    @Query(value = "SELECT * FROM Client WHERE Client.STRIPE_API_ID = :apikey ",nativeQuery = true)
    Client getByStripeId(@Param("apikey") String apikey);

    @Query(value = "SELECT * FROM card WHERE card.OWNER_ID= :id AND card.IS_DEFAULT  = 1 ",nativeQuery = true)
    Card getDefaultCard(@Param("id") Integer id);

    Client getById(Integer cId);

}
