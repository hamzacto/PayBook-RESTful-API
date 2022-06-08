package com.myProject.service;



import com.myProject.model.Client;
import com.myProject.model.Product;
import com.myProject.model.Purchase;
import com.myProject.model.Vendor;
import com.myProject.repo.*;
import com.myProject.util.UserAuth;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;


@Service
@EntityScan("com.example.demoJPA.Model")

@ComponentScan({"com.example.demoJPA.Controller",
        "com.example.demoJPA.Model",
        "com.example.demoJPA.Repository",
        "com.exemple.demoJPA.Service"})

//@AllArgsConstructor(onConstructor = @__(@Autowired))

public class ClientService {


    @Autowired(required = false)
    public Card card;

    @Autowired(required = false)
    public Client client;

    @Autowired
    public ClientRepository clientRepository ;

    @Autowired
    public ChargeRepository chargeRepository;

    @Autowired
    public CardRepository cardRepository ;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public VendorRepository vendorRepository;

    @Autowired
    public PurchaseRepository purchaseRepository;

    public void setClient(Client clientToSet , Client client) {
        clientToSet.setId(client.getId());
        clientToSet.setFirstName(client.getFirstName());
        clientToSet.setLastName(client.getLastName());
        clientToSet.setBalance(client.getBalance());
    }

    public Iterable<Client> getClients (){
        return clientRepository.findAll();
    }

    public AtomicLong AddClient(Client client){
        AtomicLong response = new AtomicLong();
        if (this.CheckEmailValidity(client.getEmail())) {
            clientRepository.GetClientByEmail(client.getEmail()).ifPresentOrElse(
                    (value)-> {
                        response.set(0);
                        try {
                            throw new InstanceAlreadyExistsException("email deja utlisée");
                        } catch (InstanceAlreadyExistsException e) {
                            e.printStackTrace();
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.setStripeApiId(saveCostumerOnStripe(client.getEmail()));
                                clientRepository.save(client);
                                response.set(client.getId());
                            } catch (StripeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }else {
            throw new IllegalArgumentException("email format non accepte");
        }
        return response;
    }

    public Iterable<Client> AddClients(ArrayList<Client> clients){
        List<Client> savedClients= new ArrayList<Client>();
        clients.stream().forEach(
                (client)->{
                    if (AddClient(client).equals(0)){
                        savedClients.add(client);
                    }
                } );
        return savedClients;
    }

    public Optional<Client> getClientById(Integer id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> getClientByMail(String email) {
        return clientRepository.GetClientByEmail(email);
    }

    public Client updateClient(Integer id, Client updatedclient) {
        Optional<Client> clientToUpdate = clientRepository.findById(id);
        clientToUpdate.ifPresent((c)->clientRepository.delete(c));
        return (Client) clientRepository.save(updatedclient);
    }

    public Boolean login(UserAuth userAuth){
        AtomicReference<Boolean> response = new AtomicReference<>(false);
        clientRepository.GetClientByEmail(userAuth.getEmail()).ifPresentOrElse(
                (client)->{
                    if(client.getPassword().equals(userAuth.getPassword())) response.set(true);
                    else response.set(false);
                },()-> response.set(false)
        );
        return response.get();
    }

   /* public List<Client> init() {
        clientRepository.save(new Client("hamza",
                "elmokadem",
                "hamzaelmkadem20@gmail",
                "hamzahamza",
                2000.0));
        clientRepository.save(new Client("anass",
                "elmokadem",
                "anasselmokadem20@gmail",
                "anassanass",
                20.0));
        clientRepository.save(new Client("ahmed",
                "mohamed",
                "ahmdemohadem20@gmail",
                "ahmdeahmed",
                1000.0));
        return (List<Client>) clientRepository.findAll();
    }*/

    public Boolean CheckEmailValidity(String email){
        String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }

    public String saveCostumerOnStripe(String email) throws StripeException {
        Stripe.apiKey="sk_test_51KTWHfAalbaMZ1p6Xxt9QcNKebCskQzkiHwc8IxxkMxNMrKTbMwZMDgdXO" +
                "KLxUyas8rvqCn6t8NH3pnZkWgZQQpe00oW8vQffd";
        Map<String,Object> customerParams = new HashMap<String,Object>();
        customerParams.put("email",email);
        Customer customer = Customer.create(customerParams);
        return  customer.getId();
    }

    public String chargeFromDeafault(Integer cId, Long amount) throws StripeException {
        Map<String,Object> chargeParams = new HashMap<String, Object>();
        String CustomerId = clientRepository.findById(cId).get().getStripeApiId();
        chargeParams.put("amount",amount.toString());
        chargeParams.put("currency","usd");
        chargeParams.put("customer",CustomerId);
        GsonFactoryBean gson = new GsonFactoryBean();
        Charge Stripecharge =  Charge.create(chargeParams);
        com.myProject.model.Charge ToSavecharge = new com.myProject.model.Charge();
        ToSavecharge.setAmout(Double.valueOf(amount));
//        Card chargedCard = cardRepository.getDefaultCardByClient(cId).get();
        ToSavecharge.setChargedCard(null);
        ToSavecharge.setCurrency("usd");
        ToSavecharge.setStripeApiId(Stripecharge.getId());

        //Update balance
        Client chargedClient = clientRepository.getById(cId);
        chargedClient.setBalance(chargedClient.getBalance()+amount);

        chargeRepository.save(ToSavecharge);
        return Stripecharge.toJson();
    }

    public Purchase purchase(Long clientId, Long productId) throws Exception {
        if(clientRepository.findById(Math.toIntExact(clientId)).isPresent()
                && productRepository.findById(productId).isPresent()){
            Client payingClient = clientRepository.findById(Math.toIntExact(clientId)).get();
            Product puchasedProduct = productRepository.findById(productId).get();
            Vendor sellingVendor = productRepository.findById(productId).get().getVendor();
            if(payingClient.getBalance() >= puchasedProduct.getPrice()){
                Double newClientBalance = payingClient.getBalance() - puchasedProduct.getPrice();
                Double newVendorBalance = payingClient.getBalance() + puchasedProduct.getPrice();
                payingClient.setBalance(newClientBalance);
                sellingVendor.setBalance(newVendorBalance);
                Purchase purchase = new Purchase();
                purchase.setClient(payingClient);
                purchase.setProduct(puchasedProduct);
                purchase.setAmount(puchasedProduct.getPrice());
                purchase.setPayementDate(LocalDate.now());
                purchaseRepository.save(purchase);
                return purchase;
            }else {
                throw  new Exception();
            }
        }else {
            throw new Exception();
        }
    }
}

