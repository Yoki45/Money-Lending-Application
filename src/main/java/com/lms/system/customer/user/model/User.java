package com.lms.system.customer.user.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.user.enums.CommunicationType;
import com.lms.system.product.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
@Entity
@Getter
@Setter
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(unique = true, nullable = false, name = "username")
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference  // Maintains the forward reference
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_chanel")
    private CommunicationType communicationType;





}
