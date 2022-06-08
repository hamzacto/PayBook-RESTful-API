package com.myProject.repo;

import com.myProject.model.Invoice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "invoices", path = "invoices")

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice,Long> {
}
