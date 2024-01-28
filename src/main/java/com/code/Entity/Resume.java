package com.code.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Resume {

	@Id
	private String phoneNumber;
	private String name;
	private String email;
	private Double experience;
	private String skills;

}