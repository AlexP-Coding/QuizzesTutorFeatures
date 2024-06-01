describe('Student WeeklyScore Access', () => {
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();
        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Title',
            'Question',
            'Option',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuestion(
            'Question Title2',
            'Question',
            'Option',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuizzWith2Questions(
            'Quiz Title',
            'Question Title',
            'Question Title2'
        );
        cy.contains('Logout').click();
    });

    afterEach(() => {
        cy.deleteFailedAnswers();
        cy.deleteQuestionsAndAnswers();
        cy.deleteWeeklyScores();
    });

    it('student gets weekly score', () => {

        cy.demoStudentLogin();
        cy.solveQuizz('Quiz Title', 2);

        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        )

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard')

        cy.createWeeklyScore();

        cy.intercept('GET', '**/students/dashboards/*/weeklyscores').as('getWeeklyScores')

        cy.get('[data-cy="weeklyScoresMenuButton"]').click();
        cy.wait('@getWeeklyScores')

        cy.intercept('PUT', '**/students/dashboards/*/weeklyscores').as('updateWeeklyScores')

        cy.get('[data-cy="refreshWeeklyScoresMenuButton"]').click();
        cy.wait('@updateWeeklyScores')

        cy.intercept('DELETE', '**/students/weeklyscores/*').as('deleteWeeklyScores')

        cy.get('[data-cy="deleteWeeklyScores"]').eq(1).click();
        cy.wait('@deleteWeeklyScores')

        cy.get('[data-cy="deleteWeeklyScores"]').eq(0).click();
        cy.closeErrorMessage();

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});