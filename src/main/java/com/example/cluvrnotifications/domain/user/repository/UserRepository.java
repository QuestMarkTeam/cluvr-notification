package com.example.cluvrnotifications.domain.user.repository;

import com.example.cluvrnotifications.common.repository.BaseRepository;
import com.example.cluvrnotifications.domain.user.entity.User;

public interface UserRepository extends BaseRepository<User, Long>, UserRepositoryCustom {

}
