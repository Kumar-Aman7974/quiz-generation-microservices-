package com.aman.question_service.dao;

import com.aman.question_service.model.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {

    List<Question> findByCategory(String category);


    // using jpql
    @Query(value = "SELECT q.id FROM question q Where q.category = :category ORDER BY RANDOM() LIMIT :numQ",
            nativeQuery = true)
    List<Integer> findRandomQuestionByCategory(String category, int numQ);




}
