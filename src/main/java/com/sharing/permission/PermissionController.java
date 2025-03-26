package com.sharing.permission;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Permission findById(@PathVariable Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return permission.get();
    }

    @PostMapping
    Long create(@RequestBody Permission permission) {
        permissionRepository.save(permission);
        return permission.getId();
    }

    @PutMapping("/{id}")
    void update(@RequestBody Permission permission, @PathVariable Long id) {
        Optional<Permission> result = permissionRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();
        Permission existingPermission = result.get();
        existingPermission.setViewable(permission.isViewable());
        existingPermission.setEditable(permission.isEditable());
        existingPermission.setDeletable(permission.isDeletable());
        permissionRepository.save(existingPermission);
    }

    @DeleteMapping("/{id}")
    public Permission deleteById(@PathVariable Long id) {
        Optional<Permission> result = permissionRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        permissionRepository.deleteById(id);
        return result.get();
    }

}
