package com.radovan.spring.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.UserRepository;

@Component
public class LoadData {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public LoadData(RoleRepository roleRepository, UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		addRolesData();
		addAdminData();
	}

	public void addRolesData() {
		RoleEntity role1 = new RoleEntity("ADMIN");
		RoleEntity role2 = new RoleEntity("ROLE_USER");

		if (roleRepository.findByRole("ADMIN").isEmpty()) {
			roleRepository.save(role1);
		}
		if (roleRepository.findByRole("ROLE_USER").isEmpty()) {
			roleRepository.save(role2);
		}
	}

	public void addAdminData() {
		RoleEntity role = roleRepository.findByRole("ADMIN")
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The role has not been found")));

		if (userRepository.findByEmail("doe@luv2code.com").isEmpty()) {
			List<RoleEntity> roles = new ArrayList<>();
			roles.add(role);
			UserEntity adminEntity = new UserEntity("John", "Doe", "doe@luv2code.com", "admin123", (byte) 1);
			adminEntity.setPassword(passwordEncoder.encode(adminEntity.getPassword()));
			adminEntity.setRoles(roles);

			try {
				UserEntity storedAdmin = userRepository.save(adminEntity);
				role.setUsers(List.of(storedAdmin));
				roleRepository.saveAndFlush(role);
			} catch (Exception e) {
				System.out.println("Admin already added");
			}
		} else {
			System.out.println("Admin already added");
		}
	}
}
