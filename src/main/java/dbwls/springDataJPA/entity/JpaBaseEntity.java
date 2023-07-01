package dbwls.springDataJPA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    // 저장 직전에 호출
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updateDate = now;
    }

    // 업데이트 직전에 호출
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
