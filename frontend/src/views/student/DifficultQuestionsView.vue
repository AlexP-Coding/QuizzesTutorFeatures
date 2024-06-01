frontend/src/views/student/DifficultQuestionsView.vue

<template>
  <v-container fluid>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="difficultQuestions"
        :sort-by="['percentage']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [10, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>
            <h3>Difficult Questions</h3>
            <v-spacer />
            <v-btn
              color="primary"
              dark
              data-cy="updateDifficultQuestionsMenuButton"
              @click="updateDifficultQuestions"
            >REFRESH</v-btn>
          </v-card-title>
        </template>
        <template v-slot:[`item.percentage`]="{ item }">
          {{ item.percentage }}
        </template>
        <template v-slot:[`item.question`]="{ item }">
          {{ item.questionDto.content }}
        </template>

        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item.questionDto)"
              >school</v-icon
              >
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="deleteDifficultQuestionButton"
                @click="deleteDifficultQuestion(item)"
                color="red"
              >delete</v-icon
              >
            </template>
            <span>Delete Difficult Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <student-view-dialog
        v-if="statementQuestion && studentViewDialog"
        v-model="studentViewDialog"
        :statementQuestion="statementQuestion"
        v-on:close-show-question-dialog="onCloseStudentViewDialog"
      />
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import DifficultQuestion from '@/models/dashboard/DifficultQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import StatementQuestion from '@/models/statement/StatementQuestion';
@Component({
  components: {
    'student-view-dialog': StudentViewDialog,
  },
})
export default class DifficultQuestionsView extends Vue {
  @Prop({type: Number, required: true}) dashboardId!: number;
  @Prop({type: String}) lastCheckDifficultQuestions: string = '-';
  difficultQuestions: DifficultQuestion[] = [];
  statementQuestion: StatementQuestion | null = null;
  studentViewDialog: boolean = false;
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Question',
      value: 'question',
      width: '30%',
      align: 'left',
      sortable: false,
    },
    {
      text: 'Percentage',
      value: 'percentage',
      width: '5px',
      align: 'right',
    },
  ];
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.difficultQuestions = await RemoteServices.getDifficultQuestions(this.dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async updateDifficultQuestions() {
    await RemoteServices.updateDifficultQuestions(this.dashboardId);
    this.difficultQuestions = await RemoteServices.getDifficultQuestions(this.dashboardId);
    await this.$emit('refresh');
  }

  async deleteDifficultQuestion(toDeleteDifficultQuestion: DifficultQuestion) {
      if (toDeleteDifficultQuestion.id) {
        try {
            await RemoteServices.deleteDifficultQuestions(toDeleteDifficultQuestion.id);
            this.difficultQuestions = this.difficultQuestions.filter(
            (difficultQuestion) => difficultQuestion.id != toDeleteDifficultQuestion.id
            );
        } 
        catch (error) {
            await this.$store.dispatch('error', error);
        }
    }
  }
  
  async showStudentViewDialog(question: Question) {
    if (question.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          question.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  onCloseStudentViewDialog() {
    this.statementQuestion = null;
    this.studentViewDialog = false;
  }
}
</script>