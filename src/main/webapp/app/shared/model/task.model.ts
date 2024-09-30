import dayjs from 'dayjs';
import { IWorkspace } from 'app/shared/model/workspace.model';
import { IUser } from 'app/shared/model/user.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { Privilege } from 'app/shared/model/enumerations/privilege.model';

export interface ITask {
  id?: number;
  title?: string;
  description?: string | null;
  dueDate?: dayjs.Dayjs | null;
  priority?: keyof typeof Priority | null;
  status?: keyof typeof Status | null;
  category?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  privilege?: keyof typeof Privilege | null;
  workspace?: IWorkspace | null;
  users?: IUser[] | null;
}

export const defaultValue: Readonly<ITask> = {};
