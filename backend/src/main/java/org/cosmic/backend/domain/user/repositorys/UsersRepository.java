package org.cosmic.backend.domain.user.repositorys;

import java.util.List;
import java.util.Optional;
import org.cosmic.backend.domain.user.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail_Email(String email);//이메일로 찾기

  Optional<User> findByUserId(Long userId);//key로 찾기

  Optional<List<User>> findByUsername(String name);
}
