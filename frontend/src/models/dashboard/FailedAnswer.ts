import {QuestionAnswer} from '@/models/management/QuestionAnswer'
import {ISOtoString} from '@/services/ConvertDateService';

export default class FailedAnswer {
  id!: number;
  answered!: boolean;
  collected!: string;
  questionAnswerDto!: QuestionAnswer;

  constructor(jsonObj?: FailedAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.answered = jsonObj.answered;
      this.collected = ISOtoString(jsonObj.collected);
      this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);
    }
  }
}
