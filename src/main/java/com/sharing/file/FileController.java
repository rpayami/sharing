package com.sharing.file;

import com.sharing.account.AccountPrincipal;
import com.sharing.permission.Permission;
import com.sharing.permission.PermissionRepository;
import jakarta.validation.Valid;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = "*")

public class FileController {

    private final FileRepository fileRepository;
    private final PermissionRepository permissionRepository;

    public FileController(FileRepository fileRepository, PermissionRepository permissionRepository) {
        this.fileRepository = fileRepository;
        this.permissionRepository = permissionRepository;
    }

    private Long getCurrentAccountId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof AccountPrincipal ?
                ((AccountPrincipal)principal).getAccount().getId() : 1L; /* for testing */
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    Long create(@RequestBody @Valid File file) {
        Long currentAccountId = getCurrentAccountId();
        if (currentAccountId != null) {
            file.setCreatedById(currentAccountId);
        }
        LocalDateTime now = LocalDateTime.now();
        file.setCreationDate(now);
        fileRepository.save(file);

        Permission permission = new Permission(null, currentAccountId, file.getId(), true, true, true);
        permissionRepository.save(permission);

        return file.getId();
    }

    /* Checks if another user has locked the file. */
    private void verifyLock(Long currentAccountId, File file) {
        boolean isLocked = file.isLocked();
        Long lockedById = file.getLastLockedBy();
        if (isLocked && lockedById != null && !lockedById.equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account " + lockedById +
                    " has locked the file " + file.getId());
        }
    }

    private boolean hasPermission(Long currentAccountId, Long fileId, Permission.PermissionType permissionType) {
        Optional<Permission> resultPermission = permissionRepository.findByAccountAndFile(currentAccountId, fileId);
        return resultPermission.isPresent() && resultPermission.get().hasPermission(permissionType);
    }

    private void verifyPermission(Long currentAccountId, Long fileId, Permission.PermissionType permissionType) {
        Optional<Permission> resultPermission = permissionRepository.findByAccountAndFile(currentAccountId, fileId);
        if (!hasPermission(currentAccountId, fileId, permissionType)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account " + currentAccountId +
                    " does not have " + permissionType.toString() + " permission for file " + fileId);
        }
    }

    @PutMapping("/{id}")
    @Transactional
    void update(@RequestBody @Valid File file, @PathVariable Long id) {
        Optional<File> resultFile = fileRepository.findById(id);
        if (resultFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        File existingFile = resultFile.get();
        Long currentAccountId = getCurrentAccountId();

        verifyPermission(currentAccountId, existingFile.getId(), Permission.PermissionType.EDIT);

        verifyLock(currentAccountId, existingFile);

        LocalDateTime now = LocalDateTime.now();
        existingFile.setUpdatedById(currentAccountId);
        existingFile.setUpdateDate(now);
        existingFile.setName(file.getName());
        existingFile.setContent(file.getContent());
        fileRepository.save(existingFile);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public File deleteById(@PathVariable Long id) {
        Optional<File> resultFile = fileRepository.findById(id);
        if (resultFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        File existingFile = resultFile.get();
        Long currentAccountId = getCurrentAccountId();

        verifyPermission(currentAccountId, existingFile.getId(), Permission.PermissionType.DELETE);

        verifyLock(currentAccountId, existingFile);

        List<Permission> permissions = permissionRepository.findAllByFile(id);
        for (Permission permission : permissions) {
            permissionRepository.deleteById(permission.getId());
        }

        fileRepository.deleteById(id);
        return resultFile.get();
    }

    @GetMapping
    List<File> findAll() {
        Long currentAccountId = getCurrentAccountId();
        //For the current user, only show the files with 'View' permission
        return fileRepository.findAll().stream().filter(file -> hasPermission(
                currentAccountId, file.getId(), Permission.PermissionType.VIEW)).toList();
    }

    @GetMapping("/{id}")
    public File findById(@PathVariable Long id) {
        Long currentAccountId = getCurrentAccountId();
        Optional<File> resultFile = fileRepository.findById(id);
        if (resultFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        File file = resultFile.get();

        verifyPermission(currentAccountId, file.getId(), Permission.PermissionType.VIEW);

        return file;
    }


    @PutMapping("/{id}/lock")
    @Transactional
    synchronized void lockFile(@PathVariable Long id) {
        Optional<File> result = fileRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Long currentAccountId = getCurrentAccountId();
        File existingFile = result.get();
        boolean isLocked = existingFile.isLocked();
        Long lockedById = existingFile.getLastLockedBy();
        if (isLocked) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File " + id + " is already locked by " + lockedById);
        }
        existingFile.lock(currentAccountId);
        fileRepository.save(existingFile);
    }

    @PutMapping("/{id}/unlock")
    @Transactional
    synchronized void unlockFile(@PathVariable Long id) {
        Optional<File> result = fileRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Long currentAccountId = getCurrentAccountId();
        File existingFile = result.get();
        if (!existingFile.isLocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File " + id + " is already unlocked");
        }
        Long lockedById = existingFile.getLastLockedBy();
        if (!existingFile.getLastLockedBy().equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File " + id + " is locked by other user: "
                    + lockedById);
        }
        existingFile.unlock();
        try {
            fileRepository.save(existingFile);
        }
        catch (OptimisticLockingFailureException lockingException) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent update detected on File " + id);
        }
    }

    @PutMapping("/{id}/share/{accountId}")
    @Transactional
    void shareFile(@PathVariable Long id, @PathVariable Long accountId, @RequestBody Permission permission) {
        Optional<File> result = fileRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        File file = result.get();
        Long currentAccountId = getCurrentAccountId();
        if (!currentAccountId.equals(file.getCreatedById())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File " + id + " is not created by" +
                    " the current account id " + currentAccountId);
        }
        Optional<Permission> resultPermission = permissionRepository.findByAccountAndFile(accountId, file.getId());
        if (resultPermission.isPresent()) {
            Permission existingPermission = resultPermission.get();
            existingPermission.setViewable(permission.isViewable());
            existingPermission.setEditable(permission.isEditable());
            existingPermission.setDeletable(permission.isDeletable());
            permissionRepository.save(existingPermission);
        } else {
            Permission newPermission = new Permission(null, accountId, file.getId(),
                    permission.isViewable(), permission.isEditable(), permission.isDeletable());
            permissionRepository.save(newPermission);
        }
    }
}
