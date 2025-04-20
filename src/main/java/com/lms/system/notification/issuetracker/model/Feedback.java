package com.lms.system.notification.issuetracker.model;

import com.lms.generic.model.BaseEntity;
import com.lms.system.customer.user.model.User;
import com.lms.system.notification.issuetracker.enums.FeedBackStatus;
import com.lms.system.notification.issuetracker.enums.FeedbackType;
import com.lms.system.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FeedbackType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Size(max = 65535)
    @Column(name = "description", nullable = false, length = 65535)
    private String description;

    @JoinColumn(name = "product", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;

    @Enumerated(EnumType.STRING)
    private FeedBackStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    @CreatedBy
    private User createdBy;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdOn;


}
