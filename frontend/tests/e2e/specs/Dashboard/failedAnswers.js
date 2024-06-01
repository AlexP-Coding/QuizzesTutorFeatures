describe('Student Walkthrough', () => {
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();

        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Failed Answer 1 Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)',
            'Question',
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        cy.createQuestion(
            'Question Failed Answer 2 Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)',
            'Question',
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        cy.createQuizzWith2Questions(
            'Failed Answers Title Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)',
            'Question Failed Answer 1 Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)',
            'Question Failed Answer 2 Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)'
        );
        cy.contains('Logout').click();
    });

    afterEach(() => {
        cy.deleteFailedAnswers();
        cy.deleteQuestionsAndAnswers();
    });

    it('student accesses failed Answer', () => {
        cy.demoStudentLogin();

        cy.solveQuizzFailed('Failed Answers Title Sat Apr 02 2022 19:43:40 GMT+0100 (Western European Summer Time)', 2);

        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        )

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.intercept('GET', '**/students/dashboards/*/failedAnswers').as('getFailedAnswers')

        cy.get('[data-cy="failedAnswersMenuButton"]').click();
        cy.wait('@getFailedAnswers'); //200

        cy.intercept('PUT', '**/students/dashboards/*/failedAnswers').as('updateFailedAnswers')

        cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
        cy.wait('@updateFailedAnswers');

        cy.get('[data-cy="showStudentViewDialog"]')
            .should('have.length.at.least', 1)
            .eq(0)
            .click();

        cy.get('[data-cy="closeButton"]').click();

        cy.intercept('DELETE', '**/students/failedanswers/*').as('deleteFailedAnswers')

        cy.get('[data-cy="deleteFailedAnswerButton"]')
            .should('have.length.at.least', 1)
            .eq(0)
            .click();

        cy.wait('@deleteFailedAnswers');

        cy.closeErrorMessage();
        cy.setFailedAnswersAsOld();

        cy.intercept('PUT', '**/students/dashboards/*/failedAnswers').as('updateFailedAnswers')

        cy.get('[data-cy="refreshFailedAnswersMenuButton"]').click();
        cy.wait('@updateFailedAnswers');

        cy.intercept('DELETE', '**/students/failedanswers/*').as('deleteFailedAnswers')

        cy.get('[data-cy="deleteFailedAnswerButton"]')
            .should('have.length.at.least', 1)// at least
            .eq(0)
            .click();

        cy.wait('@deleteFailedAnswers');

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});