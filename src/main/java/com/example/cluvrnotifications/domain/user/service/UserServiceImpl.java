package com.example.cluvrnotifications.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cluvrnotifications.domain.user.dto.request.LoginUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.request.SignUpUserRequestDto;
import com.example.cluvrnotifications.domain.user.dto.response.LoginUserResponseDto;
import com.example.cluvrnotifications.domain.user.dto.response.SignUpUserResponseDto;
import com.example.cluvrnotifications.domain.user.entity.User;
import com.example.cluvrnotifications.domain.user.entity.enums.UserRole;
import com.example.cluvrnotifications.domain.user.repository.UserRepository;
import com.example.cluvrnotifications.global.jwt.CustomUserDetails;
import com.example.cluvrnotifications.global.jwt.JwtUtil;
import com.example.cluvrnotifications.global.jwt.RefreshTokenServiceImpl;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final RefreshTokenServiceImpl refreshTokenService;

	@Override
	@Transactional
	public SignUpUserResponseDto signUp(SignUpUserRequestDto requestDto) {
		if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		}

		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		User newUser = new User(null,                                      // id → 데이터베이스에서 자동 생성
			requestDto.getName(),                      // name
			requestDto.getBirthday(),                  // birthday
			requestDto.getEmail(),                     // email
			requestDto.getPhoneNumber(),               // phoneNumber
			UserRole.USER,                             // 가입 시 기본 권한(예: USER)
			requestDto.getGender(),                    // gender
			requestDto.getCategoryDetail(),            // categoryDetail
			encodedPassword,                           // 암호화된 password
			0L,                                        // clover 기본값
			requestDto.getImageUrl(),                  // imageUrl (null 허용될 경우 DTO에서 null 가능)
			false                                      // isDeleted: 신규 가입이므로 false
		);

		User savedUser = userRepository.save(newUser);

		return SignUpUserResponseDto.from(savedUser);
	}

	@Override
	public LoginUserResponseDto login(LoginUserRequestDto requestDto) {
		// 1) 이메일·비밀번호 인증 시도
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				requestDto.getEmail(),
				requestDto.getPassword()
			)
		);

		// 인증 성공 시 principal은 CustomUserDetails
		User user = ((CustomUserDetails)authentication.getPrincipal()).getUser();

		// 2) 액세스 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			user.getId(),
			user.getUserRole().name()
		);

		// 3) 리프레시 토큰 생성 및 저장 (Redis 등)
		String refreshToken = refreshTokenService.createRefreshToken(
			user.getId(),
			user.getUserRole().name()
		);

		// 4) DTO 변환 후 반환
		return LoginUserResponseDto.from(user, accessToken, refreshToken);
	}
}

