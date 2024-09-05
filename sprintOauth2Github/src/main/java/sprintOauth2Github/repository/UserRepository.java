package sprintOauth2Github.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import sprintOauth2Github.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByGithubId(String githubId);
}
