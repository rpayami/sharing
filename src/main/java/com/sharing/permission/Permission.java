package com.sharing.permission;

import com.sharing.account.Account;
import com.sharing.file.File;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

public class Permission {

    public enum PermissionType {VIEW, EDIT, DELETE};
    private @Id Long id;

    private AggregateReference.IdOnlyAggregateReference<Account, Long> account;
    private AggregateReference.IdOnlyAggregateReference<File, Long> file;

    private Boolean isViewable;
    private Boolean isEditable;
    private Boolean isDeletable;

    public Permission() {

    }
    public Permission(Long id, Long accountId, Long fileId,
                      boolean isViewable, boolean isEditable, boolean isDeletable) {
        this.id = id;
        setAccount(accountId);
        setFile(fileId);
        this.isViewable = isViewable;
        this.isEditable = isEditable;
        this.isDeletable = isDeletable;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return account == null ? null : account.getId();
    }

    public void setAccount(Long accountId) {
        this.account = new AggregateReference.IdOnlyAggregateReference<Account, Long>(accountId);
    }

    public Long getFileId() {
        return file == null ? null : file.getId();
    }

    public void setFile(Long fileId) {
        this.file = new AggregateReference.IdOnlyAggregateReference<File, Long>(fileId);
    }

    public Boolean isViewable() {
        return isViewable;
    }

    public void setViewable(Boolean isViewable) {
        this.isViewable = isViewable;
    }

    public Boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public Boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(Boolean deletable) {
        isDeletable = deletable;
    }

    public Boolean hasPermission(PermissionType permissionType) {
        return switch (permissionType) {
            case VIEW -> isViewable();
            case EDIT -> isEditable();
            case DELETE -> isDeletable();
        };
    }
}
