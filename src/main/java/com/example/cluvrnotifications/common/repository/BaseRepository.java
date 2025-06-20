package com.example.cluvrnotifications.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.cluvrnotifications.global.exception.BusinessException;
import com.example.cluvrnotifications.global.response.ResponseCode;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

	default T findByIdOrElseThrow(ID id) {
		return findById(id).orElseThrow(() ->
			new BusinessException(ResponseCode.NOT_FOUND, "해당 Entity를 찾을 수 없습니다. id = " + id)
		);
	}

}
