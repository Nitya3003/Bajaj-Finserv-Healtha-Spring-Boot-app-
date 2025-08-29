
package com.example.bfhl.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository repository;
    private final SqlSolver solver;

    public String solve(String questionType) {
        return solver.solve(questionType);
    }

    public SolutionEntity storeSolution(String regNo, String questionType, String finalQuery) {
        SolutionEntity entity = new SolutionEntity();
        entity.setRegNo(regNo);
        entity.setQuestionType(questionType);
        entity.setFinalQuery(finalQuery);
        return repository.save(entity);
    }
}
