package com.urban.upark.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urban.upark.models.Users;

public interface UsersRepository extends JpaRepository<Users,Integer>{
    
    Optional<Users> findByUserName(String userName);
    
    // OAuth2 queries
    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

}
