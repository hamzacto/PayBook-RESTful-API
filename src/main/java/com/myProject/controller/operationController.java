package com.myProject.controller;



import com.myProject.model.operation;
import com.myProject.util.operationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
public class operationController {

    @Autowired(required = false)
    public com.myProject.service.operationService operationService;

    @PostMapping("/make-transfer")
    public AtomicReference<operation> makeTransfert(@RequestBody operationRequest operationRequest) {
        return  operationService.makeTransfert(operationRequest);
    }

    @GetMapping("/operations")
    public Page<operation> getOperations(@RequestParam(value = "pn" , defaultValue = "0") Integer pageNumber,
                                         @RequestParam(value = "s",defaultValue = "4") Integer size,
                                         @RequestParam(value = "mc",defaultValue = "") String mc ){
        return operationService.getOperationByReciver(pageNumber,size, "%"+mc+"%");
    }
}
