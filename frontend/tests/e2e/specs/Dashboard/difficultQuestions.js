describe('Student DifficultQuestion Access', () => {
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();

        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Difficult Question 1 Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)',
            'Question',
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        cy.createQuestion(
            'Question Difficult Question 2 Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)',
            'Question',
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        cy.createQuizzWith2Questions(
            'Difficult Question Title Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)',
            'Question Difficult Question 1 Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)',
            'Question Difficult Question 2 Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)'
        );
        cy.contains('Logout').click();
    });

    afterEach(() => {
        cy.deleteDifficultQuestions();
        cy.deleteQuestionsAndAnswers();
    });

    it('student accesses difficult Question', () => {
        cy.demoStudentLogin();

        cy.get('[data-cy="quizzesStudentMenuButton"]').click();
        cy.contains('Available').click();

        cy.contains('Difficult Question Title Sat Apr 02 2002 19:42:58 GMT+0100 (Western European Summer Time)').click();
        for (let i = 0; i < 2; i++) {
            cy.get('[data-cy="optionList"]').children().contains('ChooseThisWrong').click();
            cy.get('[data-cy="nextQuestionButton"]').click();
        }

        cy.intercept('GET', '**/students/dashboards/executions/*').as(
            'getDashboard'
        )

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.intercept('GET', '**/students/dashboards/*/difficultQuestions').as('getDifficultQuestions')

        cy.get('[data-cy="difficultQuestionsMenuButton"]').click();

        cy.intercept('PUT', '**/students/dashboards/*/difficultQuestion').as('updateDifficultQuestions')

        cy.get('[data-cy="updateDifficultQuestionsMenuButton"]').click();
        
        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});