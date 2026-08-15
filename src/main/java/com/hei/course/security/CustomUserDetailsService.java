package com.hei.course.security;

import com.hei.course.entity.JUsers;
import com.hei.course.mapper.UserMapper;
import com.hei.course.model.Users;
import com.hei.course.repository.JUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final JUsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    JUsers entity =
        usersRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));

    Users user = UserMapper.toModel(entity);

    return new UserPrincipal(user);
  }
}
