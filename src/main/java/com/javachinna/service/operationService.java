package com.javachinna.service;


import com.javachinna.model.Client;
import com.javachinna.model.operation;
import com.javachinna.repo.ClientRepository;
import com.javachinna.repo.operationRepository;
import com.javachinna.util.operationRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;


@Data
@Service
@Component
public class operationService {

    @Autowired
    public com.javachinna.repo.operationRepository operationRepository;
    @Autowired
    public ClientRepository clientRepository;




    public operation createOperation(Integer sendingId , String recivingEmail , Double montant){

        Client sendingClient = (Client) clientRepository.findById(sendingId).get();
        Client recivingClient = clientRepository.GetClientByEmail(recivingEmail).get();
        LocalDate operationDate = LocalDate.now();
        return new operation(sendingClient,recivingClient,montant,operationDate);
    }


    public AtomicReference<operation> makeTransfert (operationRequest operationRequest){
        AtomicReference<operation> currentOperation = new AtomicReference<>();
        Client sendingClient = (Client) clientRepository.findById(operationRequest.getSendingid()).get();
        Client recievingClient = clientRepository.GetClientByEmail(operationRequest.getRecievingEmail()).get();

        boolean condition = operationRequest.getMontant() > 0
                && !operationRequest.getMontant().isNaN()
                && !sendingClient.equals(recievingClient) ;

        if(condition) {
            clientRepository.GetClientByEmail(operationRequest.getRecievingEmail()).ifPresentOrElse(
                    (Rclient)->
                    {
                        if(operationRequest.getMontant()<sendingClient.getBalance()){

                            currentOperation.set(createOperation(operationRequest.getSendingid(),
                                    operationRequest.getRecievingEmail(), operationRequest.getMontant()));

                            operationRepository.save(createOperation(operationRequest.getSendingid(),
                                    operationRequest.getRecievingEmail(),
                                    operationRequest.getMontant()));

                            sendingClient.setBalance(
                                    sendingClient.getBalance()-operationRequest.getMontant()
                            );

                            recievingClient.setBalance(
                                    recievingClient.getBalance() + operationRequest.getMontant()
                            );
                            clientRepository.saveAndFlush(sendingClient);
                            clientRepository.saveAndFlush(recievingClient);


                        }
                        else {
                            System.out.println("operation imposible");
                            currentOperation.set(null);
                        }
                    }, ()->{
                        System.out.println("non existant email");
                        currentOperation.set(null);
                    }
            );
        }else {
            System.out.println("operation imposible");
            currentOperation.set(null);
        }
        return currentOperation;
    }

    public Page<operation> getOperations(Integer pageNumber , Integer size) {
        return operationRepository.findAll(PageRequest.of(pageNumber, size));
    }

    public Page<operation> getOperationByReciver(Integer pageNumber , Integer size , String mc){
        return operationRepository.getOperationByReciver(PageRequest.of(pageNumber,size),mc);
    }
}
