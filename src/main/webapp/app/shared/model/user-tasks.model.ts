import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ITask } from 'app/shared/model/task.model';

export interface IUserTasks {
  id?: number;
  privilege?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser | null;
  task?: ITask | null;
}

export const defaultValue: Readonly<IUserTasks> = {};
