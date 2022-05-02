package com.javachinna.service;

import com.javachinna.model.Client;
import com.javachinna.model.Invoice;
import com.javachinna.model.Product;
import com.javachinna.model.Vendor;
import com.javachinna.repo.InvoiceRepository;
import com.javachinna.repo.ProductRepository;
import com.javachinna.repo.VendorRepository;
import com.javachinna.util.InvoiceParams;
import com.javachinna.util.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class VendorService {
    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    public AtomicLong AddVendor(Vendor vendor){
        AtomicLong response = new AtomicLong();
        if (this.clientService.CheckEmailValidity(vendor.getEmail())) {
            vendorRepository.findByEmail(vendor.getEmail()).ifPresentOrElse(
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
                                vendor.setBalance((double) 0);
                                vendorRepository.save(vendor);
                                response.set(vendor.getId());
                            } catch (Exception e) {
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

    public Boolean login(UserAuth userAuth){
        AtomicReference<Boolean> response = new AtomicReference<>(false);
        vendorRepository.findByEmail(userAuth.getEmail()).ifPresentOrElse(
                (vendor)->{
                    if(vendor.getPassword().equals(userAuth.getPassword())) response.set(true);
                    else response.set(false);
                },()-> response.set(false)
        );
        return response.get();
    }

    public Product addProduct(Long vendorId, Product product) {
        Vendor sellingVendor = vendorRepository.findById(vendorId).get();
        if(product.getPrice()>0){
            product.setVendor(sellingVendor);
            productRepository.save(product);
            return product;
        }
        return null;
    }
    public Invoice createInvoice(InvoiceParams invoiceParams) {
            Product product = productRepository.findById(invoiceParams.getProductId()).get();
            Client client = clientService.getClientById(Math.toIntExact(invoiceParams.getClientId())).get();

            Invoice invoice = new Invoice();
            invoice.setClient(client);
            invoice.setAmount(product.getPrice());
            invoice.setBillingDate(LocalDate.now());
            invoice.setProduct(product);
            invoice.setPayementDeadLine("");
            invoiceRepository.save(invoice);

            return invoice;

    }

}
