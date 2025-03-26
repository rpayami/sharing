package com.sharing.file;

import org.springframework.data.repository.ListCrudRepository;

public interface FileRepository extends ListCrudRepository<File, Long> {
}
