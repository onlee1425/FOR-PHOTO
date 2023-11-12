package com.forphoto.v1.domain.member.repository;

import com.forphoto.v1.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmailAndProvider(String email,String provider);

    boolean existsByEmail(String email);



}
