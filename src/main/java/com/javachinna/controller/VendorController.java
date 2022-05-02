package com.javachinna.controller;



import com.javachinna.model.Invoice;
import com.javachinna.model.Product;
import com.javachinna.model.Vendor;
import com.javachinna.service.VendorService;
import com.javachinna.util.InvoiceParams;
import com.javachinna.util.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class VendorController {

    @Autowired
    VendorService vendorService;

    @PostMapping(value = "/Vendor/create")
    public AtomicLong CreateVendor(@RequestBody Vendor vendor){
        return vendorService.AddVendor(vendor);
    }

    @GetMapping("/Vendor/login")
    public Boolean Login(@RequestBody UserAuth userAuth){
        return vendorService.login(userAuth);
    }

    @PostMapping("Vendors/add-Product")
    public Product addProduct(@RequestParam Long vendorId , @RequestBody Product product){
        return vendorService.addProduct(vendorId,product);
    }

    @PostMapping("Vendor/invoice")
    public Invoice createInvoice(@RequestBody InvoiceParams invoiceParams){
        return vendorService.createInvoice(invoiceParams);
    }

}
