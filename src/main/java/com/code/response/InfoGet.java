package com.code.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoGet {

	private String phoneNumber;
	private String name;
	private String email;
	private Double experience;
	private String skills;

}
