import dayjs from 'dayjs';
import { IWorkspace } from 'app/shared/model/workspace.model';

export interface ITask {
  id?: number;
  title?: string;
  description?: string | null;
  dueDate?: dayjs.Dayjs | null;
  priority?: string | null;
  status?: string | null;
  category?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  workspace?: IWorkspace | null;
}

export const defaultValue: Readonly<ITask> = {};
