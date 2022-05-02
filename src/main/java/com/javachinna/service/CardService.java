package com.javachinna.service;


import com.javachinna.model.Card;
import com.javachinna.model.Client;
import com.javachinna.repo.CardRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentSource;
import com.stripe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Component
public class CardService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientService clientService;
    @Autowired(required = false)
    Card card;

    public Optional<Card> findByNumber(Long number){
        return cardRepository.findByNumber(number);
    }

    public AtomicReference<Card> SaveCard(Card c){
        AtomicReference<Card> result = new AtomicReference<>(new Card());
        cardRepository.findByNumber(c.getNumber()).ifPresentOrElse(
                result::set,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cardRepository.save(c);
                            result.set(c);
                            addCardToStripeCustomer(c.getOwner(),c);
                        } catch (StripeException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return result;
    }

    public Page<Card> getCards(Integer pageNumber , Integer size ){
        return cardRepository.findAll(PageRequest.of(pageNumber,size));
    }

    public Card updateCard(Integer id, Card newCard){
        newCard.setId(id);
        return cardRepository.saveAndFlush(newCard);
    }

    public void deleteCard(Card card){
        cardRepository.delete(card);
    }

    public List<Card> getByClient(Integer id){
        return  cardRepository.getByClient(id);
    }

    public PaymentSource addCardToStripeCustomer(Client client, Card c) throws StripeException {
        Stripe.apiKey="sk_test_51KTWHfAalbaMZ1p6Xxt9QcNKebCskQzkiHwc8IxxkMxNMrKTb" +
                "MwZMDgdXOKLxUyas8rvqCn6t8NH3pnZkWgZQQpe00oW8vQffd";

        Map<String, Object> retrieveParams =
                new HashMap<>();
        List<String> expandList = new ArrayList<>();
        expandList.add("sources");
        retrieveParams.put("expand", expandList);
        Customer customer =
                Customer.retrieve(
                        client.getStripeApiId(),
                        retrieveParams,
                        null
                );

        Map<String,Object> cardParams = new HashMap<String,Object>();
        cardParams.put("number",c.getNumber());
        cardParams.put("exp_month",c.getExp_month());
        cardParams.put("exp_year",c.getExp_year());
        cardParams.put("cvc",c.getCvc());

        Map<String,Object> tokenParams = new HashMap<String,Object>();
        tokenParams.put("card",cardParams);
        Token token =Token.create(tokenParams);

        Map<String,Object> source = new HashMap<String,Object>();
        source.put("source",token.getId());
        PaymentSource StripCard =customer.getSources().create(source);
        c.setStripeApiId(StripCard.getId());
        cardRepository.saveAndFlush(c);
        return StripCard;
    }

    public String retriveStripeCard(String CardId ,
                                    String clientApiId) throws StripeException {
        Stripe.apiKey = "sk_test_51KTWHfAalbaMZ1p6Xxt9QcNKebCskQzkiHwc8" +
                "IxxkMxNMrKTbMwZMDgdXOKLxUyas8rvqCn6t8NH3pnZkWgZQQpe00oW8vQffd";

        Map<String, Object> retrieveParams =
                new HashMap<>();
        List<String> expandList = new ArrayList<>();
        expandList.add("sources");
        retrieveParams.put("expand", expandList);
        Customer customer =
                Customer.retrieve(
                        clientApiId,
                        retrieveParams,
                        null
                );
        String paymentSource;
        paymentSource =customer.getSources().retrieve(
                        CardId
                ).toString();
        return paymentSource;
    }

    public List<Card> getDefaultCard(Integer id){
        List<Card> DefaultCard = getByClient(id).stream().filter(
                (card)->{
                    boolean b = card.getIsDefault() == true;
                    return b;
                }
        ).collect(Collectors.toList());
        return DefaultCard;
    }


    /*public void createPayout(String CCid,Double amount,String currency ) throws StripeException {
        Stripe.apiKey="sk_test_51KTWHfAalbaMZ1p6Xxt9QcNKebCskQzkiHwc8IxxkMxNMrKTbMwZMDgdXO" +
                "KLxUyas8rvqCn6t8NH3pnZkWgZQQpe00oW8vQffd";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("destination",CCid);
        params.put("amount",1100);
        params.put("currency",currency);
        Payout payout = Payout.create(params);
    }*/
}
