package com.sharing.file;

import com.sharing.account.Account;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.time.LocalDateTime;

public class File {
    private @Id Long id;
    private @NotNull String name;
    private @NotNull String content;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private AggregateReference.IdOnlyAggregateReference<Account, Long> createdBy;
    private AggregateReference.IdOnlyAggregateReference<Account, Long> updatedBy;
    private AggregateReference.IdOnlyAggregateReference<Account, Long> lastLockedBy;
    private Boolean isLocked;
    private @Version Long version;

    public File(Long id, String name, String content,
                Long createdBy, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.content = content;
        if (createdBy != null) {
            setCreatedById(createdBy);
        }
        this.creationDate = creationDate;
        this.isLocked = false;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedById() {
        return createdBy== null ? null : createdBy.getId();
    }
    public void setCreatedById(Long createdById) {
        this.createdBy = new AggregateReference.IdOnlyAggregateReference<Account, Long>(createdById);
    }

    public Long getLastLockedBy() {
        return lastLockedBy == null ? null : lastLockedBy.getId();
    }
    public void setLastLockedBy(Long lockedById) {
        this.lastLockedBy = new AggregateReference.IdOnlyAggregateReference<Account, Long>(lockedById);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getUpdatedById() {
        return updatedBy == null ? null : updatedBy.getId();
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedBy = new AggregateReference.IdOnlyAggregateReference<Account, Long>(updatedById);
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public void lock(Long lockedBy) {
        setLastLockedBy(lockedBy);
        isLocked = true;
    }

    public void unlock() {
        isLocked = false;
    }
}
