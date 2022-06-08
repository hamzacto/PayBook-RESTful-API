package com.myProject.controller;


import com.myProject.model.Card;
import com.myProject.model.Client;
import com.myProject.model.Purchase;
import com.myProject.service.CardService;
import com.myProject.service.ClientService;
import com.myProject.util.Response;
import com.myProject.util.UserAuth;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@ComponentScan({"com.example.demoJPA.Model",
        "com.example.demoJPA.Repository",
        "com.example.demoJPA.Service"})

@CrossOrigin(origins = "http://localhost:4200")

@RestController
public class ClientController {

    @Autowired
    public ClientService clientService;

    @Autowired
    public CardService cardService;
   /*
    @PostMapping("/init")
    public List<Client> init(){
        return clientService.init();
    }*/

    @GetMapping("/Client/all")
    public ResponseEntity<Response> getClients(){

        return ResponseEntity.ok(
                Response.builder()
                        .timestamp(LocalDateTime.now())
                        .data(Map.of("clients",clientService.getClients()))
                        .message("clients fetched successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @PostMapping("/add-client")
    public AtomicLong AddClient(@RequestBody Client newClient){
       return clientService.AddClient(newClient);
    }

    @PostMapping("/add-clients")

    public Iterable<Client> addClients(@RequestBody ArrayList<Client> clients){
        return clientService.AddClients(clients);
    }

    @PostMapping("/Client/create")
    public ResponseEntity<Response> createClient(@RequestBody Client client){

        /*return ResponseEntity.ok(
                Response.builder()
                        .timestamp(LocalDateTime.now())
                        .data(Map.of("created client",clientService.AddClient(client)))
                        .message("client created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );*/

        return ResponseEntity.ok(
                Response.builder()
                        .timestamp(LocalDateTime.now())
                        .data(Map.of("client",clientService.getClientById(clientService.AddClient(client).intValue())))
                        .message(clientService.AddClient(client).equals(0)?"an error occurred":"client added successfully")
                        .status(clientService.AddClient(client).equals(0)?HttpStatus.EXPECTATION_FAILED
                                :HttpStatus.CREATED)
                        .statusCode(clientService.AddClient(client).equals(0)?HttpStatus.CREATED.value()
                                :HttpStatus.EXPECTATION_FAILED.value())
                        .build()
        );


    }


    @GetMapping("/Client/retrieve")
    public ResponseEntity<Response> getClient(@RequestParam Integer id){
        return ResponseEntity.ok(
                Response.builder()
                        .timestamp(LocalDateTime.now())
                        .data(Map.of("client",clientService.getClientById(id)))
                        .message("client retrived")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );

    }

    @GetMapping("/Client/ByEmail")

    public ResponseEntity<Response> getClientBymail (@RequestParam String email)
    {
        /*return clientService.getClientByMail(email);*/
        return ResponseEntity.ok(
                Response.builder().timestamp(LocalDateTime.now())
                        .data(Map.of("client",clientService.getClientByMail(email)))
                        .message(clientService.getClientByMail(email).isPresent()?"client founded":"client not exist")
                        .status(clientService.getClientByMail(email).isPresent()?HttpStatus.OK:HttpStatus.NO_CONTENT)
                        .statusCode(clientService.getClientByMail(email).isPresent()?HttpStatus.OK.value():
                                HttpStatus.NO_CONTENT.value())
                        .build()
        );
    }

    @PutMapping("/update/client")

    public Client updateClient(@RequestParam Integer id,@RequestBody Client updatedclient){
        return clientService.updateClient(id,updatedclient);
    }

    @GetMapping("login")

    public Boolean login(@RequestBody UserAuth userAuth)
    {
        return clientService.login(userAuth);
    }

    @GetMapping("check-email={email}")

    public Boolean checkEmail(@PathVariable String email )
    {
        return clientService.CheckEmailValidity(email);
    }

    @PostMapping("chargefromdefault")

    public String chargeFromDefault(@PathParam("cId") Integer cId ,
                                    @PathParam("amount") Long amount)
            throws StripeException {
        return  clientService.chargeFromDeafault(cId,amount);
    }

    @GetMapping("Client/DefaultCard")

    public List<Card> getDefaultCard(@PathParam("id") Integer id){
        return cardService.getDefaultCard(id);
    }

    @PostMapping("Client/purchase")

    public Purchase purchase(@RequestParam("clientId") Integer clientId
            , @RequestParam("productId") Long productId) throws Exception {
        return clientService.purchase(Long.valueOf(clientId),productId);
    }




}

