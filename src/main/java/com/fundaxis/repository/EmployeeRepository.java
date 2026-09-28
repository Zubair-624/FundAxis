package com.fundaxis.repository;

import com.fundaxis.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findEmployeeByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    List<Member> findByMemberStatus(Member.MemberStatus memberStatus);



//    Optional<Member> findMemberByMemberId(String memberId);




}
