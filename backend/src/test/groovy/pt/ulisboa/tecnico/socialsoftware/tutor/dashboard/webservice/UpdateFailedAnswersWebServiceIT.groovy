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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
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
        answerQuiz(true, false, true, quizQuestion, quiz)

    }

    def "student updates failed answers"() {
        given: 'a logged in student'
        failedAnswerRepository.findAll().size() == 0L
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200

        and: "it is now in the database"
        failedAnswerRepository.findAll().size() == 1L

        and: 'it is the same failed answer'
        def response2 = restClient.get(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )
        response2.data.get(0).id == failedAnswerRepository.findAll().get(0).id

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "teacher cant update student's failed answers"() {
        given: 'a demo teacher'
        failedAnswerRepository.findAll().size() == 0L
        demoTeacherLogin()

        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: 'the server understands the request but refuses to authorize it'
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        and: 'no failed answers are added'
        failedAnswerRepository.findAll().size() == 0L

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

    def "student cant update another students failed answers"() {
        given: 'a demo student'
        failedAnswerRepository.findAll().size() == 0L
        demoStudentLogin()

        when: 'the web service is invoked'
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedAnswers',
                requestContentType: 'application/json'
        )

        then: 'the server understands the request but refuses to authorize it'
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        and: 'no failed answers are added'
        failedAnswerRepository.findAll().size() == 0L

        cleanup:
        userRepository.deleteAll()
        courseRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        failedAnswerRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

}