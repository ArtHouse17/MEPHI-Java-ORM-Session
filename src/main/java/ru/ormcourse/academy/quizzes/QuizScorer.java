package ru.ormcourse.academy.quizzes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class QuizScorer {
    private QuizScorer() {
    }

    static Grade grade(List<QuestionKey> questions, List<Selection> selections) {
        Map<Long, Selection> byQuestion = new HashMap<>();
        for (Selection selection : selections) {
            if (byQuestion.putIfAbsent(selection.questionId(), selection) != null) {
                throw new IllegalArgumentException("A question appears more than once in the submitted answers");
            }
        }
        if (byQuestion.size() != questions.size()) {
            throw new IllegalArgumentException("Submit exactly one answer for every question");
        }

        Map<Long, Integer> pointsByQuestion = new LinkedHashMap<>();
        int score = 0;
        int maxScore = 0;
        for (QuestionKey question : questions) {
            Selection selection = byQuestion.get(question.questionId());
            if (selection == null) {
                throw new IllegalArgumentException("The answers do not match the questions in this quiz");
            }
            if (!question.optionIds().contains(selection.optionId())) {
                throw new IllegalArgumentException("Option #" + selection.optionId()
                        + " does not belong to question #" + question.questionId());
            }
            int points = selection.optionId() == question.correctOptionId() ? question.points() : 0;
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
