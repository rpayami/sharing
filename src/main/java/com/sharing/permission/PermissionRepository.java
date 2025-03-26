package com.sharing.permission;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends ListCrudRepository<Permission, Long> {
    Optional<Permission> findByAccountAndFile(Long accountId, Long fileId);
    List<Permission> findAllByFile(Long fileId);
}

