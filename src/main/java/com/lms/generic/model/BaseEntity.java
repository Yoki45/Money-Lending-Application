package com.lms.generic.model;

import com.lms.system.customer.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {


    @Column(nullable = false,  updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdOn;

    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    @CreatedBy
    private User createdBy;

    @Column(insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "updated_by", insertable = false)
    @LastModifiedBy
    private User updatedBy;

}
