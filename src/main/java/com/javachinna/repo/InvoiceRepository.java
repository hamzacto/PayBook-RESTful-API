package com.javachinna.repo;

import com.javachinna.model.Invoice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "invoices", path = "invoices")

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice,Long> {
}
