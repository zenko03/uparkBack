package com.urban.upark.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.urban.upark.models.Users;

public interface UsersRepository extends JpaRepository<Users,Integer>{
    
    Optional<Users> findByUserName(String userName);

}
