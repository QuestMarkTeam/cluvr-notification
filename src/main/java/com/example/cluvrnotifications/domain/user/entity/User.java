package com.example.cluvrnotifications.domain.user.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;

import com.example.cluvrnotifications.common.entity.BaseTimeEntity;
import com.example.cluvrnotifications.domain.user.entity.enums.CategoryDetail;
import com.example.cluvrnotifications.domain.user.entity.enums.Gender;
import com.example.cluvrnotifications.domain.user.entity.enums.UserRole;

@Getter
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 유저 이름 (varchar(10), NOT NULL) */
	@Column(name = "name", nullable = false, length = 10)
	private String name;

	/** 생년월일 (date, NOT NULL) */
	@Column(name = "birthday", nullable = false)
	private LocalDate birthday;

	/** 이메일 (varchar(50), NOT NULL, UNIQUE) */
	@Column(name = "email", nullable = false, length = 50, unique = true)
	private String email;

	/** 전화번호 (varchar(11), NOT NULL) */
	@Column(name = "phone_number", nullable = false, length = 11)
	private String phoneNumber;

	/**
	 * 역할 (varchar(11), NOT NULL, enum)
	 * - EnumType.STRING 으로 저장
	 */
	@Column(name = "user_role", nullable = false, length = 11)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	/**
	 * 성별 (varchar(5), NOT NULL, enum)
	 * - EnumType.STRING 으로 저장
	 */
	@Column(name = "gender", nullable = false, length = 5)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	/**
	 * 클럽 하위 카테고리 (varchar(11), NOT NULL, enum)
	 * - EnumType.STRING 으로 저장
	 */
	@Column(name = "category_detail", nullable = false, length = 11)
	@Enumerated(EnumType.STRING)
	private CategoryDetail categoryDetail;

	/**
	 * 암호화된 비밀번호 (varchar(60), NOT NULL)
	 * - BCrypt 등으로 해시된 비밀번호를 저장
	 */
	@Column(name = "password", nullable = false, length = 60)
	private String password;

	/** 유저 포인트 (bigint, NOT NULL, DEFAULT 0) */
	@Column(name = "clover", nullable = false)
	private Long point = 0L;

	/** 유저 프로필 이미지 URL (varchar(255), NULL 허용) */
	@Column(name = "image_url", length = 255)
	private String imageUrl;

	/** 소프트 딜리트 여부 (boolean, NOT NULL, DEFAULT true) */
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = true;

	public User(Long id, String name, LocalDate birthday, String email, String phoneNumber, UserRole userRole,
		Gender gender, CategoryDetail categoryDetail, String password, Long point, String imageUrl, Boolean isDeleted) {
		this.id = id;
		this.name = name;
		this.birthday = birthday;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.userRole = userRole;
		this.gender = gender;
		this.categoryDetail = categoryDetail;
		this.password = password;
		this.point = point;
		this.imageUrl = imageUrl;
		this.isDeleted = isDeleted;
	}
}
