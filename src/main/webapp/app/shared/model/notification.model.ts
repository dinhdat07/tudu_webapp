import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface INotification {
  id?: number;
  message?: string;
  status?: keyof typeof Status | null;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<INotification> = {};
