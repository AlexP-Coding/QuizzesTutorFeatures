package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def quiz
    def quizQuestion

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)

        and:
        quiz = createQuiz(1)
        quizQuestion = createQuestion(1, quiz)
    }

    def "student gets failed answers"() {
        given: 'a student'
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz)
        createFailedAnswer(questionAnswer, DateHandler.now())

        when: 'the web service is invoked'
        def response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: 'the request returns OK'
        response.status == 200

        and: 'it is in the database'
        failedAnswerRepository.findAll().size() == 1

        and: 'it is the same failedAnswer'
        response.data.get(0).id == failedAnswerRepository.findAll().get(0).id

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "teacher can't get student's failed answers"() {
        given: 'a demo teacher'
        demoTeacherLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: 'the server understands the request but refuses to authorize it'
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "student can't get another student's failed answers"() {
        given: 'a demo student'
        demoStudentLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: 'the server understands the request but refuses to authorize it'
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

}