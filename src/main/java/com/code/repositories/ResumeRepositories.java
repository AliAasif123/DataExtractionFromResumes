package com.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.Entity.Resume;

public interface ResumeRepositories extends JpaRepository<Resume, String> {

}
