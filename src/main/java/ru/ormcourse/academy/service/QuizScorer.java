package ru.ormcourse.academy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class QuizScorer {
    private QuizScorer() {
    }

    static Grade grade(List<QuestionKey> questions, List<Selection> selections) {
        Map<Long, Long> optionByQuestion = new HashMap<>();
        for (Selection selection : selections) {
            if (optionByQuestion.putIfAbsent(selection.questionId(), selection.optionId()) != null) {
                throw new IllegalArgumentException("A question appears more than once in the submitted answers");
            }
        }
        if (optionByQuestion.size() != questions.size()) {
            throw new IllegalArgumentException("Submit exactly one answer for every question");
        }

        Map<Long, Integer> pointsByQuestion = new HashMap<>();
        int score = 0;
        int maxScore = 0;
        for (QuestionKey question : questions) {
            Long optionId = optionByQuestion.get(question.questionId());
            if (optionId == null) {
                throw new IllegalArgumentException("The answers do not match the questions in this quiz");
            }
            if (!question.optionIds().contains(optionId)) {
                throw new IllegalArgumentException("Option #" + optionId
                        + " does not belong to question #" + question.questionId());
            }
            int points = optionId == question.correctOptionId() ? question.points() : 0;
            score += points;
            maxScore += question.points();
            pointsByQuestion.put(question.questionId(), points);
        }
        return new Grade(score, maxScore, Map.copyOf(pointsByQuestion));
    }

    record QuestionKey(long questionId, int points, Set<Long> optionIds, long correctOptionId) {
    }

    record Selection(long questionId, long optionId) {
    }

    record Grade(int score, int maxScore, Map<Long, Integer> pointsByQuestion) {
    }
}
