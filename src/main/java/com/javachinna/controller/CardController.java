package com.javachinna.controller;


import com.javachinna.model.Card;
import com.javachinna.service.CardService;
import com.javachinna.service.ClientService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ComponentScan({"com.example.demoJPA.Model",
        "com.example.demoJPA.Repository",
        "com.example.demoJPA.Service"})

@RestController

public class CardController {

    @Autowired
    CardService cardService;
    @Autowired
    ClientService clientService;

    @PostMapping("Card/link")
    public AtomicReference<Card> linkCard(@PathParam("cl") Integer id , @RequestBody Card card){
        card.setOwner(clientService.getClientById(id).get());
        return cardService.SaveCard(card);
    }

    @GetMapping("Cards/{id}")
    public List<Card> getCards(@PathVariable("id") Integer id){
        return cardService.getByClient(id);
    }

    @GetMapping("Cards/api/retrive")
    public String retriveStripeCard(@RequestParam String CardId ,
                                    @RequestParam String ClientApiId)
            throws StripeException {
        return cardService.retriveStripeCard(CardId,ClientApiId);
    }


 /*   @PostMapping("Card/payout")
    public  void createPayout(@PathParam("CCid")String CCid, @PathParam("amout") Double amout)
            throws StripeException {
            cardService.createPayout(CCid,amout,"usd");
    }*/
}
